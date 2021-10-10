package dynamia.com.barcodescanner.ui.transferstore.transferdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.databinding.PickingPostBottomDialogBinding
import dynamia.com.barcodescanner.ui.transferstore.TransferType
import dynamia.com.barcodescanner.ui.transferstore.TransferType.*
import dynamia.com.core.util.crossFade

@AndroidEntryPoint
class PickingPostDialog : BottomSheetDialogFragment() {

    val viewModel: TransferDetailViewModel by viewModels()
    private val postType by lazy { arguments?.getSerializable(POST_TYPE) as TransferType }
    private lateinit var _viewBinding: PickingPostBottomDialogBinding
    val viewBinding by lazy { _viewBinding }

    private var animateDuration: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        animateDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
        _viewBinding = PickingPostBottomDialogBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (postType) {
            SHIPMENT -> viewModel.postShipmentData()
            RECEIPT -> viewModel.postReceiptData()
            PURCHASE -> viewModel.postPurchaseData()
            INVENTORY -> viewModel.postInventoryData()
        }
        setObseverable()
        setClicklistener()
    }

    private fun setObseverable() {
        viewModel.pickingPostViewState.observe(viewLifecycleOwner, {
            when (it) {
                is TransferDetailViewModel.PickingDetailPostViewState.GetUnpostedData -> {
                    viewBinding.tvTransferTotalUnposted.text = it.data.toString()
                }
                is TransferDetailViewModel.PickingDetailPostViewState.GetSuccessfullyPostedData -> {
                    viewBinding.tvTransferShipmentPostedCount.text = it.data.toString()
                }
                is TransferDetailViewModel.PickingDetailPostViewState.ErrorPostData -> {
                    with(viewBinding) {
                        ivStatusPostTransferShipment.crossFade(
                            animateDuration.toLong(),
                            pbTransferShipmentPostDialog
                        )
                        ivStatusPostTransferShipment.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_error_circle,
                                null
                            )
                        )
                        tvErrorTransferShipmentPost.text = it.message
                        btnDismisPickingPost.isEnabled = true
                    }
                }
                TransferDetailViewModel.PickingDetailPostViewState.AllDataPosted -> {
                    viewBinding.ivStatusPostTransferShipment.crossFade(
                        animateDuration.toLong(),
                        viewBinding.pbTransferShipmentPostDialog
                    )
                    viewBinding.btnDismisPickingPost.isEnabled = true
                }
            }
        })
    }

    private fun setClicklistener() {
        with(viewBinding) {
            btnDismisPickingPost.isEnabled = false
            btnDismisPickingPost.setOnClickListener {
                dismissAllowingStateLoss()
            }
        }
    }

    companion object {
        fun newInstance(type: TransferType): PickingPostDialog {
            val argument = Bundle().apply {
                putSerializable(POST_TYPE, type)
            }
            return PickingPostDialog().apply {
                arguments = argument
            }
        }

        const val POST_TYPE = "post_type"
    }

}