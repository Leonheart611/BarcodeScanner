package dynamia.com.barcodescanner.ui.transferstore.transferinput

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.databinding.TransferInputFragmentBinding
import dynamia.com.barcodescanner.ui.MainActivity
import dynamia.com.barcodescanner.ui.history.HistoryType
import dynamia.com.barcodescanner.ui.transferstore.TransferType.*
import dynamia.com.core.base.BaseFragmentBinding
import dynamia.com.core.data.entinty.PurchaseOrderLine
import dynamia.com.core.data.entinty.StockOpnameData
import dynamia.com.core.data.entinty.TransferShipmentLine
import dynamia.com.core.util.*
import java.util.*

@AndroidEntryPoint
class TransferInputFragment :
    BaseFragmentBinding<TransferInputFragmentBinding>(TransferInputFragmentBinding::inflate) {
    private val viewModel: TransferInputViewModel by viewModels()
    private val args: TransferInputFragmentArgs by navArgs()
    var activity: MainActivity? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        activity = requireActivity() as MainActivity
        setupListener()
        setupObserverable()
    }

    private fun setupObserverable() {
        with(viewModel) {
            transferInputViewState.observe(viewLifecycleOwner, {
                when (it) {
                    is TransferInputViewModel.TransferInputViewState.ErrorGetData -> {
                        context?.showLongToast(it.message)
                    }
                    is TransferInputViewModel.TransferInputViewState.LoadingSearchPickingList -> {
                        activity?.showLoading(it.status)
                    }
                    is TransferInputViewModel.TransferInputViewState.SuccessGetValue -> {
                        showSuccessfulData(it.data)
                    }
                    is TransferInputViewModel.TransferInputViewState.ErrorSaveData -> {
                        context?.showLongToast(it.message)
                    }
                    TransferInputViewModel.TransferInputViewState.SuccessSaveData -> {
                        context?.showLongToast("Success Save Data")
                        args.barcodeNo?.let { viewBinding.includeTransferInput.etTranferinputQty.text?.clear() }
                            ?: kotlin.run { clearData() }
                    }
                    is TransferInputViewModel.TransferInputViewState.SuccessGetPurchaseValue -> {
                        showSuccessPurchaseData(it.data)
                    }
                    is TransferInputViewModel.TransferInputViewState.SuccessGetStockOpnameValue -> {
                        showSuccessStockOpname(it.data)
                    }
                }
            })
            inputValidation.observe(viewLifecycleOwner, {
                when (it) {
                    TransferInputViewModel.InputValidation.BarcodeEmpty -> {
                        viewBinding.includeTransferInput.tilTransferinputBarcode.error =
                            "Must Fill this Field"
                    }
                    TransferInputViewModel.InputValidation.QtyEmpty -> {
                        viewBinding.includeTransferInput.tilTransferinputQty.error =
                            "Must Fill this Field"
                    }
                }
            })
        }
    }

    private fun showSuccessfulData(data: TransferShipmentLine) {
        with(viewBinding.includeTransferInput) {
            tvTransferItemName.text = data.description
            tilTransferinputName.editText?.setText(data.no)
            when (args.transferType) {
                SHIPMENT -> tvTransferQty.text = "${data.alredyScanned}/${data.quantity}"
                RECEIPT -> tvTransferQty.text = "${data.alredyScanned}/${data.qtyInTransit}"
            }
        }
    }

    private fun showSuccessPurchaseData(data: PurchaseOrderLine) {
        with(viewBinding.includeTransferInput) {
            tvTransferItemName.text = data.description
            tilTransferinputName.editText?.setText(data.no)
            tvTransferQty.text = "${data.alredyScanned}/${data.quantity}"
        }
    }

    private fun showSuccessStockOpname(data: StockOpnameData) {
        with(viewBinding.includeTransferInput) {
            tvTransferItemName.text = data.itemIdentifier
            tilTransferinputName.editText?.setText(data.itemNo)
            tvTransferQty.text = "${data.alredyScanned}/${data.qtyCalculated}"
            etTransferinputBincode.setText(data.binCode)
        }
    }

    private fun setupView() {
        with(viewBinding) {
            toolbarPickingListInput.title = viewModel.getCompanyName()
            includeTransferInput.etTransferInputBarcode.requestFocus()
            args.barcodeNo?.let {
                includeTransferInput.etTransferInputBarcode.setText(it)
                includeTransferInput.etTransferInputBarcode.isEnabled = false
                getPickingListLineData(it)
                includeTransferInput.etTranferinputQty.requestFocus()
            }
            includeTransferInput.etTransferInputBarcode.setOnEditorActionListener { _, keyCode, event ->
                if (((event?.action ?: -1) == KeyEvent.ACTION_DOWN)
                    || keyCode == EditorInfo.IME_ACTION_NEXT && (args.transferType != STOCKOPNAME)
                ) {
                    getPickingListLineData(includeTransferInput.etTransferInputBarcode.text.toString())
                    includeTransferInput.etTranferinputQty.requestFocus()
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

            includeTransferInput.etTransferinputBincode.setOnEditorActionListener { _, actionId, event ->
                if (((event?.action ?: -1) == KeyEvent.ACTION_DOWN)
                    || actionId == EditorInfo.IME_ACTION_NEXT && (args.transferType == STOCKOPNAME)
                ) {
                    getPickingListLineData(includeTransferInput.etTransferInputBarcode.text.toString(),
                        includeTransferInput.etTransferinputBincode.text.toString())
                    includeTransferInput.etTranferinputQty.requestFocus()
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

            tvTransferInputTitle.text = when (args.transferType) {
                SHIPMENT -> getString(R.string.transfer_store)
                RECEIPT -> getString(R.string.transfer_receipt_title)
                PURCHASE -> getString(R.string.purchase_order_title)
                STOCKOPNAME -> getString(R.string.stock_opname_title)
            }
            includeTransferInput.tilTransferBincode.isVisible = args.transferType == STOCKOPNAME
        }
    }


    private fun getPickingListLineData(barcode: String, binCode: String = "") {
        when (args.transferType) {
            SHIPMENT -> viewModel.getShipmentListLineValue(args.transferNo, barcode)
            RECEIPT -> viewModel.getReceiptListLineValue(args.transferNo, barcode)
            PURCHASE -> viewModel.getPurchaseLineValue(args.transferNo, barcode)
            STOCKOPNAME -> viewModel.getStockOpnameValue(barcode, args.stockId, binCode)
        }
    }

    private fun setupListener() {
        with(viewBinding) {
            toolbarPickingListInput.setOnClickListener { view?.findNavController()?.popBackStack() }
            includeTransferInput.btnReset.setOnClickListener {
                args.barcodeNo?.let { includeTransferInput.etTranferinputQty.text?.clear() }
                    ?: kotlin.run { clearData() }
            }
            includeTransferInput.btnSave.setOnClickListener {
                viewModel.checkUserInputValidation(
                    includeTransferInput.etTransferInputBarcode.text.toString(),
                    includeTransferInput.etTranferinputQty.text.toString(),
                    args.transferType
                )
            }
            toolbarPickingListInput.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.history_data -> {
                        val action =
                            TransferInputFragmentDirections.actionReceivingFragmentToHistoryInputFragment(
                                documentNo = args.transferNo,
                                historyType = when (args.transferType) {
                                    SHIPMENT -> HistoryType.SHIPMENT
                                    RECEIPT -> HistoryType.RECEIPT
                                    PURCHASE -> HistoryType.PURCHASE
                                    STOCKOPNAME -> HistoryType.STOCKOPNAME
                                }
                            )
                        view?.findNavController()?.navigate(action)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun clearData() {
        with(viewBinding.includeTransferInput) {
            etTransferInputBarcode.text?.clear()
            etTransferinputName.text?.clear()
            etTranferinputQty.text?.clear()
            etTransferInputBarcode.requestFocus()
        }
    }

}
