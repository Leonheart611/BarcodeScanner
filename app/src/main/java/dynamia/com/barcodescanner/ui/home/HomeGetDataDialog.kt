package dynamia.com.barcodescanner.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.databinding.BottomsheetHomeDataDialoogBinding
import dynamia.com.barcodescanner.ui.home.HomeViewModel.HomeGetApiViewState.*
import dynamia.com.core.data.entinty.*
import dynamia.com.core.util.EventObserver
import dynamia.com.core.util.crossFade
import dynamia.com.core.util.readJsonAsset
import kotlinx.coroutines.GlobalScope
import kotlin.coroutines.coroutineContext

@AndroidEntryPoint
class HomeGetDataDialog : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "HomeGetDataSheetDialogFragment"
    }

    private var animateDuration: Int = 0
    private val viewModel: HomeViewModel by viewModels()
    private var _viewBinding: BottomsheetHomeDataDialoogBinding? = null
    private val viewBinding by lazy { _viewBinding!! }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        animateDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
        _viewBinding = BottomsheetHomeDataDialoogBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserverable()
        callAllApi()
        //getAllDataFromAssets()
        setOnClicklistener()
    }

    private fun setObserverable() {
        viewModel.homeGetApiViewState.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                SuccessGetShipingData -> {
                    viewBinding.ivStatusPicking.crossFade(
                        animateDuration.toLong(),
                        viewBinding.pbTransferstore
                    )
                }
                is FailedGetShippingData -> {
                    with(viewBinding) {
                        tvErrorTransferstore.text = it.message
                        ivStatusPicking.crossFade(animateDuration.toLong(), pbTransferstore)
                        ivStatusPicking.setImageDrawable(
                            getDrawable(
                                resources,
                                R.drawable.ic_error_circle,
                                null
                            )
                        )
                    }
                }
                SuccessGetPurchaseData -> {
                    viewBinding.ivStatusPurchaseOrder.crossFade(
                        animateDuration.toLong(),
                        viewBinding.pbPurchaseOrder
                    )
                }
                is FailedGetPurchase -> {
                    with(viewBinding) {
                        tvErrorPurchaseOrder.text = it.message
                        ivStatusPurchaseOrder.crossFade(animateDuration.toLong(), pbPurchaseOrder)
                        ivStatusPurchaseOrder.setImageDrawable(
                            getDrawable(
                                resources,
                                R.drawable.ic_error_circle,
                                null
                            )
                        )
                    }

                }
                SuccessGetReceipt -> {
                    viewBinding.ivStatusReceiptLocal.crossFade(
                        animateDuration.toLong(),
                        viewBinding.pbReceipt
                    )
                }
                is FailedGetReceipt -> {
                    with(viewBinding) {
                        tvErrorReceipt.text = it.message
                        ivStatusReceiptLocal.crossFade(animateDuration.toLong(), pbReceipt)
                        ivStatusReceiptLocal.setImageDrawable(
                            getDrawable(
                                resources,
                                R.drawable.ic_error_circle,
                                null
                            )
                        )
                    }
                }
                is FailedGetStockOpname -> {
                    with(viewBinding) {
                        tvErrorStockOpname.text = it.message
                        ivStatusStockOpname.crossFade(
                            animateDuration.toLong(),
                            pbStockOpname
                        )
                        ivStatusStockOpname.setImageDrawable(
                            getDrawable(
                                resources,
                                R.drawable.ic_error_circle,
                                null
                            )
                        )
                    }
                }
                SuccessGetStockOpname -> {
                    viewBinding.ivStatusStockOpname.crossFade(
                        animateDuration.toLong(),
                        viewBinding.pbStockOpname
                    )
                }
                is FailedGetInventory -> {
                    with(viewBinding) {
                        tvErrorInventory.text = it.message
                        ivStatusInventory.crossFade(
                            animateDuration.toLong(),
                            pbInventory
                        )
                        ivStatusInventory.setImageDrawable(
                            getDrawable(
                                resources,
                                R.drawable.ic_error_circle,
                                null
                            )
                        )
                    }
                }
                SuccessGetInventory -> {
                    viewBinding.ivStatusInventory.crossFade(
                        animateDuration.toLong(),
                        viewBinding.pbInventory
                    )
                }
            }
        })
        viewModel.homeGetDataCount.observe(viewLifecycleOwner, EventObserver {
            if (it == 8) {
                viewBinding.btnDialogClose.isVisible = true
                viewModel.progress = 0
            } else {
                viewBinding.btnDialogClose.isVisible = false
            }
        })
    }

    private fun callAllApi() {
        viewModel.getTransferData() // 2count
        viewModel.getReceiptDataAsync() // 1count
        viewModel.getPurchaseDataAsync() // 2count
        viewModel.getStockOpname() // 1count
        viewModel.getInventoryData() // 2 count
    }


    private fun getAllDataFromAssets() {
        val transferShipmentHeader = Gson().fromJson(
            this.activity?.readJsonAsset("ShipingHeader.json"),
            TransferShipmentHeaderAsset::class.java
        )
        val transferReceiptHeader = Gson().fromJson(
            this.activity?.readJsonAsset("TransferReceiptHeader.json"),
            TransferReceiptHeaderAssets::class.java
        )
        val transferShipmentLine = Gson().fromJson(
            activity?.readJsonAsset("ShipingLine.json"),
            TransferShipmentLineAsset::class.java
        )
        val purchaseOrderHeader = Gson().fromJson(
            activity?.readJsonAsset("PurchaseOrderHeader.json"),
            PurchaseOrderHeaderAssets::class.java
        )
        val purchaseOrderLine = Gson().fromJson(
            activity?.readJsonAsset("PurchaseOrderLine.json"),
            PurchaseOrderLineAsset::class.java
        )
        val stockOpnameList = Gson().fromJson(
            activity?.readJsonAsset("StockOpnameData.json"),
            StockOpnameDataAssets::class.java
        )
        val inventoryPickHeaderAssets = Gson().fromJson(
            activity?.readJsonAsset("InventoryPickHeader.json"),
            InventoryPickHeaderAssets::class.java
        )
        val inventoryPickLine = Gson().fromJson(
            activity?.readJsonAsset("InventoryPickLine.json"),
            InventoryPickLineAsset::class.java
        )
        viewModel.saveAssetData(
            transferShipmentHeader,
            transferShipmentLine,
            transferReceiptHeader,
            purchaseOrderHeader,
            purchaseOrderLine,
            stockOpnameList,
            inventoryPickHeaderAssets,
            inventoryPickLine
        )
    }

    fun setOnClicklistener() {
        viewBinding.btnDialogClose.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

}