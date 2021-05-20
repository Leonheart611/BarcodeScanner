package dynamia.com.barcodescanner.ui.transferstore.transferdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dynamia.com.barcodescanner.R
import dynamia.com.core.util.crossFade
import kotlinx.android.synthetic.main.picking_post_bottom_dialog.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PickingPostDialog : BottomSheetDialogFragment() {

    val viewModel: TransferDetailViewModel by viewModel()
    private var animateDuration: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        animateDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
        return inflater.inflate(R.layout.picking_post_bottom_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.postPickingDataNew()
        setObseverable()
        setClicklistener()
    }

    fun setObseverable() {
        viewModel.pickingPostViewState.observe(viewLifecycleOwner, {
            when (it) {
                is TransferDetailViewModel.PickingDetailPostViewState.GetUnpostedData -> {
                    tv_picking_total_post.text = it.data.toString()
                }
                is TransferDetailViewModel.PickingDetailPostViewState.GetSuccessfullyPostedData -> {
                    tv_picking_posted_count.text = it.data.toString()
                }
                is TransferDetailViewModel.PickingDetailPostViewState.ErrorPostData -> {
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
                    btn_dismis_picking_post.isEnabled = true
                }
                TransferDetailViewModel.PickingDetailPostViewState.AllDataPosted -> {
                    iv_status_post_picking.crossFade(
                        animateDuration.toLong(),
                        pb_picking_post_dialog
                    )
                    btn_dismis_picking_post.isEnabled = true
                }
            }
        })
    }

    fun setClicklistener() {
        btn_dismis_picking_post.isEnabled = false
        btn_dismis_picking_post.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

}