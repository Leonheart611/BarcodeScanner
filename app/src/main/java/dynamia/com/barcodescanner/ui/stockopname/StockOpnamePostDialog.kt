package dynamia.com.barcodescanner.ui.stockopname

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
import dynamia.com.core.util.crossFade

@AndroidEntryPoint
class StockOpnamePostDialog : BottomSheetDialogFragment() {

    val viewModel: StockOpnameViewModel by viewModels()
    private lateinit var _viewBinding: PickingPostBottomDialogBinding
    val viewBinding by lazy { _viewBinding }


    private var animateDuration: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        animateDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
        _viewBinding = PickingPostBottomDialogBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setObseverable()
        setClicklistener()
        viewModel.postStockOpnameData()
    }

    private fun setupView() {
        viewBinding.tvPickingPostTitle.text = "Stock Opname Data"
    }

    private fun setObseverable() {
        viewModel.stockOpnameViewState.observe(viewLifecycleOwner, {
            when (it) {
                StockOpnameViewModel.StockOpnameViewState.AllDataPosted -> {
                    viewBinding.ivStatusPostTransferShipment.crossFade(
                        animateDuration.toLong(),
                        viewBinding.pbTransferShipmentPostDialog
                    )
                    viewBinding.btnDismisPickingPost.isEnabled = true
                }
                is StockOpnameViewModel.StockOpnameViewState.ErrorPostData -> {
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
                is StockOpnameViewModel.StockOpnameViewState.GetSuccessfullyPostedData -> {
                    viewBinding.tvTransferShipmentPostedCount.text = it.data.toString()
                }
                is StockOpnameViewModel.StockOpnameViewState.GetUnpostedData -> {
                    viewBinding.tvTransferTotalUnposted.text = it.data.toString()
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