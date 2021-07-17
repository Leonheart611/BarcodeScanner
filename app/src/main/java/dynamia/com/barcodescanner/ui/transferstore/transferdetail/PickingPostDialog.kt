package dynamia.com.barcodescanner.ui.transferstore.transferdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.transferstore.TransferType
import dynamia.com.barcodescanner.ui.transferstore.TransferType.*
import dynamia.com.core.util.crossFade
import kotlinx.android.synthetic.main.picking_post_bottom_dialog.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PickingPostDialog : BottomSheetDialogFragment() {

    val viewModel: TransferDetailViewModel by viewModel()
    private val postType by lazy { arguments?.getSerializable(POST_TYPE) as TransferType }

    private var animateDuration: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        animateDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
        return inflater.inflate(R.layout.picking_post_bottom_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (postType) {
            SHIPMENT -> viewModel.postShipmentData()
            RECEIPT -> viewModel.postReceiptData()
            PURCHASE -> viewModel.postPurchaseData()
        }
        setObseverable()
        setClicklistener()
    }

    private fun setObseverable() {
        viewModel.pickingPostViewState.observe(viewLifecycleOwner, {
            when (it) {
                is TransferDetailViewModel.PickingDetailPostViewState.GetUnpostedData -> {
                    tv_transfer_total_unposted.text = it.data.toString()
                }
                is TransferDetailViewModel.PickingDetailPostViewState.GetSuccessfullyPostedData -> {
                    tv_transfer_shipment_posted_count.text = it.data.toString()
                }
                is TransferDetailViewModel.PickingDetailPostViewState.ErrorPostData -> {
                    iv_status_post_transfer_shipment.crossFade(
                        animateDuration.toLong(),
                        pb_transfer_shipment_post_dialog
                    )
                    iv_status_post_transfer_shipment.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_error_circle,
                            null
                        )
                    )
                    tv_error_transfer_shipment_post.text = it.message
                    btn_dismis_picking_post.isEnabled = true
                }
                TransferDetailViewModel.PickingDetailPostViewState.AllDataPosted -> {
                    iv_status_post_transfer_shipment.crossFade(
                        animateDuration.toLong(),
                        pb_transfer_shipment_post_dialog
                    )
                    btn_dismis_picking_post.isEnabled = true
                }
            }
        })
    }

    private fun setClicklistener() {
        btn_dismis_picking_post.isEnabled = false
        btn_dismis_picking_post.setOnClickListener {
            dismissAllowingStateLoss()
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