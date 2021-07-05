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
import dynamia.com.core.data.entinty.TransferReceiptHeaderAssets
import dynamia.com.core.data.entinty.TransferShipmentHeaderAsset
import dynamia.com.core.data.entinty.TransferShipmentLineAsset
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
        setObserverable()
        callAllApi()
        //getAllDataFromAssets()
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
                SuccessGetReceiptImport -> {
                    iv_status_receipt_import.crossFade(animateDuration.toLong(), pb_receipt_import)
                }
                is FailedGetReceiptImport -> {
                    tv_error_receipt_import.text = it.message
                    iv_status_receipt_import.crossFade(animateDuration.toLong(), pb_receipt_import)
                    iv_status_receipt_import.setImageDrawable(
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
            }
        })
    }

    private fun callAllApi() {
        viewModel.getTransferData()
        viewModel.getReceiptDataAsync()
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
        viewModel.saveAssetData(
            transferShipmentHeader,
            transferShipmentLine,
            transferReceiptHeader
        )
    }

    fun setOnClicklistener() {
        btn_dialog_close.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

}