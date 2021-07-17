package dynamia.com.barcodescanner.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.home.HomeViewModel.HomeGetApiViewState.*
import dynamia.com.core.data.entinty.*
import dynamia.com.core.util.crossFade
import dynamia.com.core.util.readJsonAsset
import kotlinx.android.synthetic.main.bottomsheet_home_data_dialoog.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeGetDataDialog : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "HomeGetDataSheetDialogFragment"
    }

    private var animateDuration: Int = 0

    private val viewModel: HomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        animateDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
        return inflater.inflate(R.layout.bottomsheet_home_data_dialoog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // setObserverable()
        callAllApi()
        getAllDataFromAssets()
        setOnClicklistener()
    }

    private fun setObserverable() {
        viewModel.homeGetApiViewState.observe(viewLifecycleOwner, {
            when (it) {
                SuccessGetShipingData -> {
                    iv_status_picking.crossFade(animateDuration.toLong(), pb_transferstore)
                }
                is FailedGetShippingData -> {
                    tv_error_transferstore.text = it.message
                    iv_status_picking.crossFade(animateDuration.toLong(), pb_transferstore)
                    iv_status_picking.setImageDrawable(
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_error_circle, null)
                    )
                }
                SuccessGetPurchaseData -> {
                    iv_status_purchase_order.crossFade(animateDuration.toLong(), pb_purchase_order)
                }
                is FailedGetPurchase -> {
                    tv_error_purchase_order.text = it.message
                    iv_status_purchase_order.crossFade(animateDuration.toLong(), pb_purchase_order)
                    iv_status_purchase_order.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_error_circle,
                            null
                        )
                    )
                }
                SuccessGetReceipt -> {
                    iv_status_receipt_local.crossFade(animateDuration.toLong(), pb_receipt)
                }
                is FailedGetReceipt -> {
                    tv_error_receipt.text = it.message
                    iv_status_receipt_local.crossFade(animateDuration.toLong(), pb_receipt)
                    iv_status_receipt_local.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_error_circle,
                            null
                        )
                    )
                }
                is FailedGetStockOpname -> {
                    tv_error_stock_opname.text = it.message
                    iv_status_stock_opname.crossFade(animateDuration.toLong(), pb_stock_opname)
                    iv_status_stock_opname.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_error_circle,
                            null
                        )
                    )
                }
                SuccessGetStockOpname -> {
                    iv_status_stock_opname.crossFade(animateDuration.toLong(), pb_stock_opname)
                }
            }
        })
    }

    private fun callAllApi() {
        viewModel.getTransferData()
        viewModel.getReceiptDataAsync()
        viewModel.getPurchaseDataAsync()
        viewModel.getStockOpname()
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
        viewModel.saveAssetData(
            transferShipmentHeader,
            transferShipmentLine,
            transferReceiptHeader,
            purchaseOrderHeader,
            purchaseOrderLine,
            stockOpnameList
        )
    }

    fun setOnClicklistener() {
        btn_dialog_close.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

}