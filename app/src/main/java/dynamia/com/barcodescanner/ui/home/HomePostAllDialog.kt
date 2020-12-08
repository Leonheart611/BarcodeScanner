package dynamia.com.barcodescanner.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dynamia.com.barcodescanner.R
import dynamia.com.core.util.crossFade
import dynamia.com.core.util.gone
import kotlinx.android.synthetic.main.fragment_post_stock_count_dialog.*
import kotlinx.android.synthetic.main.home_post_all_dialog.*
import kotlinx.android.synthetic.main.picking_post_bottom_dialog.*
import kotlinx.android.synthetic.main.receipt_post_bottom_sheet_dialog.*
import kotlinx.android.synthetic.main.receipt_post_import_bottom_sheet_dialog.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomePostAllDialog : BottomSheetDialogFragment() {

    private val viewModel: HomeViewModel by viewModel()
    private var animateDuration: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        animateDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
        return inflater.inflate(R.layout.home_post_all_dialog, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setObseverable()
        callAllApi()
        btn_dismiss_all_post.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    private fun callAllApi() {
        viewModel.postPickingDataNew()
        viewModel.postReceiptImportNew()
        viewModel.postReceiptLocalNew()
        viewModel.postStockCountDataNew()
    }

    private fun setupView() {
        btn_dismis_picking_post.gone()
        btn_dismis_receipt_post.gone()
        btn_dismis_receipt_import.gone()
        btn_dismis_stock_count.gone()
    }

    private fun setObseverable() {
        viewModel.homePostViewState.observe(viewLifecycleOwner, {
            when (it) {
                is HomeViewModel.HomePostViewState.GetUnpostedPicking -> {
                    tv_picking_total_post.text = it.data.toString()
                }
                is HomeViewModel.HomePostViewState.GetSuccessfullyPicking -> {
                    tv_picking_posted_count.text = it.data.toString()
                }
                is HomeViewModel.HomePostViewState.ErrorPostPicking -> {
                    iv_status_post_picking.crossFade(
                        animateDuration.toLong(),
                        pb_picking_post_dialog
                    )
                    iv_status_post_picking.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_error_circle,
                            null
                        )
                    )
                    tv_error_picking_post.text = it.message
                }
                HomeViewModel.HomePostViewState.AllDataPostedPicking -> {
                    iv_status_post_picking.crossFade(
                        animateDuration.toLong(),
                        pb_picking_post_dialog
                    )
                }


                is HomeViewModel.HomePostViewState.GetUnpostedLocal -> {
                    tv_receipt_total_post.text = it.data.toString()
                }
                is HomeViewModel.HomePostViewState.GetSuccessfulLocal -> {
                    tv_receipt_posted_count.text = it.data.toString()
                }
                is HomeViewModel.HomePostViewState.ErrorPostLocal -> {
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
                }
                HomeViewModel.HomePostViewState.SuccessPostallLocal -> {
                    iv_status_post_receipt.crossFade(
                        animateDuration.toLong(),
                        pb_receipt_post_dialog
                    )
                }


                is HomeViewModel.HomePostViewState.GetUnpostedImport -> {
                    tv_receipt_import_count.text = it.data.toString()
                }
                is HomeViewModel.HomePostViewState.GetSuccessfulImport -> {
                    tv_import_post_posted.text = it.data.toString()
                }
                is HomeViewModel.HomePostViewState.ErrorPostImport -> {
                    iv_post_status_import.crossFade(
                        animateDuration.toLong(),
                        pb_post_import
                    )
                    iv_post_status_import.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_error_circle,
                            null
                        )
                    )
                    tv_error_post_import.text = it.message
                }
                HomeViewModel.HomePostViewState.SuccessPostallImport -> {
                    iv_post_status_import.crossFade(
                        animateDuration.toLong(),
                        pb_post_import
                    )
                }


                is HomeViewModel.HomePostViewState.GetUnpostedCount -> {
                    tv_stock_total_post.text = it.data.toString()
                }
                is HomeViewModel.HomePostViewState.GetSuccessfulCount -> {
                    tv_stock_posted_count.text = it.data.toString()
                }
                is HomeViewModel.HomePostViewState.ErrorPostCount -> {
                    iv_status_post_stock_count.crossFade(
                        animateDuration.toLong(),
                        pb_post_stock_count
                    )
                    iv_status_post_stock_count.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_error_circle,
                            null
                        )
                    )
                    tv_error_stock_count_post.text = it.message
                }
                HomeViewModel.HomePostViewState.SuccessPostallCount -> {
                    iv_status_post_stock_count.crossFade(
                        animateDuration.toLong(),
                        pb_post_stock_count
                    )
                }
            }
        })
    }

}