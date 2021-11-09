package dynamia.com.barcodescanner.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.databinding.HomePostAllDialogBinding
import dynamia.com.core.util.crossFade
import dynamia.com.core.util.gone

@AndroidEntryPoint
class HomePostAllDialog : BottomSheetDialogFragment() {

    private val viewModel: HomeViewModel by viewModels()
    private var animateDuration: Int = 0
    private lateinit var _viewBinding: HomePostAllDialogBinding
    val viewBinding by lazy { _viewBinding }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        animateDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
        _viewBinding = HomePostAllDialogBinding.inflate(inflater, container, false)
        return viewBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setObseverable()
        callAllApi()
        viewBinding.btnDismissAllPost.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    private fun callAllApi() {
        with(viewModel) {
            postReceiptData()
            postShipmentData()
            postPurchaseData()
            postStockOpnameData()
            postBinReclassData()
            postInventoryData()
        }
    }

    private fun setupView() {
        with(viewBinding) {
            includeTransfer.btnDismisPickingPost.gone()
            includeReceipt.btnDismisReceiptPost.gone()
            includePurchase.btnDismisReceiptImport.gone()
            includeStock.btnDismisStockCount.gone()
            includeBinreclass.btnDismisBinReclassPost.gone()
            includeInventory.btnDismissInventoryPost.gone()
        }
    }

    private fun setObseverable() {
        viewModel.homePostViewState.observe(viewLifecycleOwner, {
            when (it) {
                /**
                 * Transfer Shipment View State
                 */
                is HomeViewModel.HomePostViewState.GetUnpostedTransferShipment -> {
                    viewBinding.includeTransfer.tvTransferTotalUnposted.text = it.data.toString()
                }
                is HomeViewModel.HomePostViewState.GetSuccessTransferShipment -> {
                    viewBinding.includeTransfer.tvTransferShipmentPostedCount.text =
                        it.data.toString()
                }
                is HomeViewModel.HomePostViewState.ErrorPostTransferShipment -> {
                    with(viewBinding.includeTransfer) {
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
                    }
                }
                HomeViewModel.HomePostViewState.AllDataPostedTransferShipment -> {
                    viewBinding.includeTransfer.ivStatusPostTransferShipment.crossFade(
                        animateDuration.toLong(),
                        viewBinding.includeTransfer.pbTransferShipmentPostDialog
                    )
                }

                /**
                 * Transfer Receipt View State
                 */
                is HomeViewModel.HomePostViewState.GetUnpostedTransferReceipt -> {
                    viewBinding.includeReceipt.tvReceiptTotalPost.text = it.data.toString()
                }
                is HomeViewModel.HomePostViewState.GetSuccessfulTransferReceipt -> {
                    viewBinding.includeReceipt.tvReceiptPostedCount.text = it.data.toString()
                }
                is HomeViewModel.HomePostViewState.ErrorPostTransferReceipt -> {
                    with(viewBinding.includeReceipt) {
                        ivStatusPostReceipt.crossFade(
                            animateDuration.toLong(),
                            pbReceiptPostDialog
                        )
                        ivStatusPostReceipt.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_error_circle,
                                null
                            )
                        )
                        tvErrorReceiptPost.text = it.message
                    }
                }
                HomeViewModel.HomePostViewState.SuccessPostallTransferReceipt -> {
                    viewBinding.includeReceipt.ivStatusPostReceipt.crossFade(
                        animateDuration.toLong(),
                        viewBinding.includeReceipt.pbReceiptPostDialog
                    )
                }

                /**
                 * Purchase View State
                 */
                is HomeViewModel.HomePostViewState.GetUnpostedPurchase -> {
                    viewBinding.includePurchase.tvPurhcaseUnpostedCount.text = it.data.toString()
                }
                is HomeViewModel.HomePostViewState.GetSuccessfulPurchase -> {
                    viewBinding.includePurchase.tvPurchasePosted.text = it.data.toString()
                }
                is HomeViewModel.HomePostViewState.ErrorPostPurchase -> {
                    with(viewBinding.includePurchase) {
                        ivPostStatusPurchase.crossFade(
                            animateDuration.toLong(),
                            pbPostPurchase
                        )
                        ivPostStatusPurchase.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_error_circle,
                                null
                            )
                        )
                        tvErrorPostPurchase.text = it.message
                    }
                }
                HomeViewModel.HomePostViewState.SuccessPostallPurchase -> {
                    viewBinding.includePurchase.ivPostStatusPurchase.crossFade(
                        animateDuration.toLong(),
                        viewBinding.includePurchase.pbPostPurchase
                    )
                }

                /**
                 * Stock View State
                 */

                is HomeViewModel.HomePostViewState.GetUnpostedStock -> {
                    viewBinding.includeStock.tvStockTotalPost.text = it.data.toString()
                }
                is HomeViewModel.HomePostViewState.GetSuccessfulStock -> {
                    viewBinding.includeStock.tvStockPostedCount.text = it.data.toString()
                }
                is HomeViewModel.HomePostViewState.ErrorPostStock -> {
                    with(viewBinding.includeStock) {
                        ivStatusPostStockCount.crossFade(
                            animateDuration.toLong(),
                            pbPostStockCount
                        )
                        ivStatusPostStockCount.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_error_circle,
                                null
                            )
                        )
                        tvErrorStockCountPost.text = it.message
                    }
                }
                HomeViewModel.HomePostViewState.SuccessPostallStock -> {
                    viewBinding.includeStock.ivStatusPostStockCount.crossFade(
                        animateDuration.toLong(),
                        viewBinding.includeStock.pbPostStockCount
                    )
                }

                /**
                 * Binreclass View state
                 */

                is HomeViewModel.HomePostViewState.ErrorPostBinReclass -> {
                    with(viewBinding.includeBinreclass) {
                        ivBinReclassStatusPost.crossFade(
                            animateDuration.toLong(),
                            pbBinReclassPost
                        )
                        ivBinReclassStatusPost.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_error_circle,
                                null
                            )
                        )
                        tvErrorMessageBinReclass.text = it.message
                    }
                }
                is HomeViewModel.HomePostViewState.GetSuccessfulBinReclass -> {
                    viewBinding.includeBinreclass.tvBinReclassPostCount.text = it.data.toString()
                }

                is HomeViewModel.HomePostViewState.GetUnpostedBinReclass -> {
                    viewBinding.includeBinreclass.tvBinReclassUnpostedCount.text =
                        it.data.toString()
                }
                HomeViewModel.HomePostViewState.SuccessPostallBinReclass -> {
                    viewBinding.includeBinreclass.ivBinReclassStatusPost.crossFade(
                        animateDuration.toLong(),
                        viewBinding.includeBinreclass.pbBinReclassPost
                    )
                }
            }
        })

        viewModel.inventoryPostViewState.observe(viewLifecycleOwner, {
            when (it) {
                is HomeViewModel.InventoryPostViewState.ErrorPost -> {
                    with(viewBinding.includeInventory) {
                        ivInventoryPostStatus.crossFade(
                            animateDuration.toLong(),
                            pbInventoryPost
                        )
                        ivInventoryPostStatus.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_error_circle,
                                null
                            )
                        )
                        tvInventoryErrorMessage.text = it.message
                    }
                }
                is HomeViewModel.InventoryPostViewState.SuccessData -> {
                    viewBinding.includeInventory.tvInventoryPostCount.text = it.data.toString()
                }
                HomeViewModel.InventoryPostViewState.SuccessPostallData -> {
                    viewBinding.includeInventory.ivInventoryPostStatus.crossFade(
                        animateDuration.toLong(),
                        viewBinding.includeInventory.pbInventoryPost
                    )
                }
                is HomeViewModel.InventoryPostViewState.UnpostedData -> {
                    viewBinding.includeInventory.tvInventoryUnpostedCount.text =
                        it.data.toString()
                }
            }
        })

    }

}