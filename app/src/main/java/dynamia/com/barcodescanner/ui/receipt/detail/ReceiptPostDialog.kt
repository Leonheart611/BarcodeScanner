package dynamia.com.barcodescanner.ui.receipt.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dynamia.com.barcodescanner.R
import dynamia.com.core.util.Constant.RECEIPT_LOCAL
import dynamia.com.core.util.crossFade
import kotlinx.android.synthetic.main.receipt_post_bottom_sheet_dialog.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class ReceiptPostDialog : BottomSheetDialogFragment() {

    private val viewModel: ReceiptDetailViewModel by sharedViewModel()
    private var animateDuration = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        animateDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
        return inflater.inflate(R.layout.receipt_post_bottom_sheet_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setObseverable()
        setClicklistener()
    }

    private fun setClicklistener() {
        btn_dismis_receipt_post.isEnabled = false
        btn_dismis_receipt_post.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    private fun setupView() {
        when (arguments?.getString(IS_FROM)) {
            RECEIPT_LOCAL -> {
                tv_receipt_post_title.text = getString(R.string.receipt_local_post)
                viewModel.postReceiptLocalNew()
            }
            else -> {
                tv_receipt_post_title.text = getString(R.string.receipt_import_post)
                viewModel.postReceiptImportNew()
            }
        }

    }

    private fun setObseverable() {
        viewModel.receiptPostViewState.observe(viewLifecycleOwner, {
            when (it) {
                is ReceiptDetailViewModel.ReceiptPostViewState.GetUnpostedReceipt -> {
                    tv_receipt_total_post.text = it.data.toString()
                }
                is ReceiptDetailViewModel.ReceiptPostViewState.GetSuccessfulPosted -> {
                    tv_receipt_posted_count.text = it.data.toString()
                }
                is ReceiptDetailViewModel.ReceiptPostViewState.ErrorReceiptPost -> {
                    iv_status_post_receipt.crossFade(
                        animateDuration.toLong(),
                        pb_receipt_post_dialog
                    )
                    iv_status_post_receipt.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_error_circle,
                            null
                        )
                    )
                    tv_error_receipt_post.text = it.message
                    btn_dismis_receipt_post.isEnabled = true
                }
                ReceiptDetailViewModel.ReceiptPostViewState.SuccessPostallData -> {
                    iv_status_post_receipt.crossFade(
                        animateDuration.toLong(),
                        pb_receipt_post_dialog
                    )
                    btn_dismis_receipt_post.isEnabled = true
                }
            }
        })
    }

    companion object {
        const val IS_FROM = "IS_FROM"
        fun newInstance(isFrom: String): ReceiptPostDialog =
            ReceiptPostDialog().apply {
                arguments = Bundle().apply {
                    putString(IS_FROM, isFrom)
                }
            }
    }
}