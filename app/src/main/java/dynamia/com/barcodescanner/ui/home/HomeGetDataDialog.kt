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
import dynamia.com.core.data.model.*
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
        savedInstanceState: Bundle?
    ): View? {
        animateDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
        return inflater.inflate(R.layout.bottomsheet_home_data_dialoog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserverable()
        //callAllApi()
        getAllDataFromAssets()
        setOnClicklistener()
    }

    private fun setObserverable() {
        viewModel.homeGetApiViewState.observe(viewLifecycleOwner, {
            when (it) {
                SuccessGetPickingList -> {
                    iv_status_picking.crossFade(animateDuration.toLong(), pb_picking_list)
                }
                is FailedGetPickingList -> {
                    tv_error_pickinglist.text = it.message
                    iv_status_picking.crossFade(animateDuration.toLong(), pb_picking_list)
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
                SuccessGetReceiptLocal -> {
                    iv_status_receipt_local.crossFade(animateDuration.toLong(), pb_receipt_local)
                }
                is FailedGetReceiptLocal -> {
                    tv_error_receipt_local.text = it.message
                    iv_status_receipt_local.crossFade(animateDuration.toLong(), pb_receipt_local)
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
        viewModel.getReceiptImportAPI()
        viewModel.getPickingListApi()
        viewModel.getReceiptLocalApi()
    }


    fun getAllDataFromAssets() {

        val pickingListheader = Gson().fromJson(
            this.activity?.readJsonAsset("PickingListHeader.json"),
            PickingListHeader::class.java
        )
        val pickingListLine = Gson().fromJson(
            activity?.readJsonAsset("PickingListLine.json"),
            PickingListLine::class.java
        )
        val receiptImportHeader = Gson().fromJson(
            activity?.readJsonAsset("ReceiptImportHeader.json"),
            ReceiptImportHeader::class.java
        )
        val receiptImportLine = Gson().fromJson(
            activity?.readJsonAsset("ReceiptImportLine.json"),
            ReceiptImportLine::class.java
        )
        val receiptLocalHeader = Gson().fromJson(
            activity?.readJsonAsset("ReceiptLocalHeader.json"),
            ReceiptLocalHeader::class.java
        )
        val receiptLocalLine = Gson().fromJson(
            activity?.readJsonAsset("ReceiptLocalLine.json"),
            ReceiptLocalLine::class.java
        )

        val peminjamHeader = Gson().fromJson(
            activity?.readJsonAsset("PeminjamanHeader.json"),
            PeminjamanHeaderAsset::class.java
        )

        val peminjamLine = Gson().fromJson(
            activity?.readJsonAsset("PeminjamanDetail.json"),
            PeminjamanDetailAsset::class.java
        )

        val dorPickingHeader = Gson().fromJson(
            activity?.readJsonAsset("DORPickingListHeader.json"),
            DorPickHeaderAsset::class.java
        )

        val dorPickingDetail = Gson().fromJson(
            activity?.readJsonAsset("DORPickingListDetail.json"),
            DorPickingDetailAsset::class.java
        )

        viewModel.savePickingHeader(
            pickingListheader,
            pickingListLine,
            receiptImportHeader,
            receiptImportLine,
            receiptLocalHeader,
            receiptLocalLine
        )


    }

    fun setOnClicklistener() {
        btn_dialog_close.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

}