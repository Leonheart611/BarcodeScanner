package dynamia.com.barcodescanner.ui.binreclass

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
import dynamia.com.barcodescanner.ui.transferstore.transferdetail.TransferDetailViewModel
import dynamia.com.core.util.crossFade

@AndroidEntryPoint
class BinReclassPostDialog : BottomSheetDialogFragment() {

    val viewModel: TransferDetailViewModel by viewModels()
    private var _viewBinding: PickingPostBottomDialogBinding? = null
    val viewBinding by lazy { _viewBinding!! }

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
        viewBinding.tvPickingPostTitle.text = "Post All Bin Reclass"
        viewModel.postBinReclassData()
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
                    with(viewBinding) {
                        ivStatusPostTransferShipment.crossFade(
                            animateDuration.toLong(),
                            pbTransferShipmentPostDialog
                        )
                        btnDismisPickingPost.isEnabled = true
                    }
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

}