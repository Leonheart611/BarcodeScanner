package dynamia.com.barcodescanner.ui.transferstore.transferinput

import android.media.MediaPlayer
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
import dynamia.com.core.data.entinty.InventoryPickLine
import dynamia.com.core.data.entinty.PurchaseOrderLine
import dynamia.com.core.data.entinty.TransferShipmentLine
import dynamia.com.core.util.EventObserver
import dynamia.com.core.util.showLongToast

@AndroidEntryPoint
class TransferInputFragment :
    BaseFragmentBinding<TransferInputFragmentBinding>(TransferInputFragmentBinding::inflate) {
    private val viewModel: TransferInputViewModel by viewModels()
    private val args: TransferInputFragmentArgs by navArgs()
    var activity: MainActivity? = null
    private var mpFail: MediaPlayer? = null
    private var mpSuccess: MediaPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mpFail = MediaPlayer.create(context, R.raw.error)
        mpSuccess = MediaPlayer.create(context, R.raw.correct_sound)
        setupView()
        activity = requireActivity() as MainActivity
        setupListener()
        setupObserverable()
    }

    private fun setupObserverable() {
        with(viewModel) {
            getReceiptHeaderValue(args.transferNo, args.transferType)
            transferInputViewState.observe(viewLifecycleOwner, EventObserver {
                when (it) {
                    is TransferInputViewModel.TransferInputViewState.ErrorGetData -> {
                        context?.showLongToast(it.message)
                        activity?.showLoading(false)
                        mpFail?.start()
                    }
                    is TransferInputViewModel.TransferInputViewState.LoadingSearchPickingList -> {
                        activity?.showLoading(it.status)
                    }
                    is TransferInputViewModel.TransferInputViewState.ErrorSaveData -> {
                        context?.showLongToast(it.message)
                    }
                    TransferInputViewModel.TransferInputViewState.SuccessSaveData -> {
                        context?.showLongToast("Success Save Data")
                        viewBinding.includeTransferInput.etTranferinputQty.text?.clear()
                    }

                    else -> {}
                }
            })

            inputValidation.observe(viewLifecycleOwner) {
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
            }
            when (args.transferType) {
                SHIPMENT -> transferShipmentLine.observe(viewLifecycleOwner) { showSuccessfulData(it) }
                RECEIPT -> transferShipmentLine.observe(viewLifecycleOwner) { showSuccessfulData(it) }
                PURCHASE -> purchaserOrderLine.observe(viewLifecycleOwner) {
                    showSuccessPurchaseData(
                        it
                    )
                }
                INVENTORY -> inventoryLine.observe(viewLifecycleOwner) { showSuccessInventoryData(it) }
                else -> {}
            }

            soundSuccess.observe(viewLifecycleOwner, EventObserver {
                //if (it) mpSuccess?.start()
            })
        }
    }

    private fun showSuccessInventoryData(data: InventoryPickLine) {
        with(viewBinding.includeTransferInput) {
            tvTransferItemName.text = data.itemNo
            tilTransferinputName.editText?.setText(data.itemRefNo)
            tvTransferQty.text = "${data.alredyScanned}"
            etTransferinputBincode.setText(data.binCode)
        }
    }

    private fun showSuccessfulData(data: TransferShipmentLine) {
        with(viewBinding.includeTransferInput) {
            tvTransferItemName.text = data.itemNo
            tilTransferinputName.editText?.setText(data.itemRefNo)
            when (args.transferType) {
                SHIPMENT -> tvTransferQty.text = "${data.alredyScanned}/${data.quantity}"
                RECEIPT -> tvTransferQty.text = "${data.alreadyScanedReceipt}/${data.qtyInTransit}"
                else -> {}
            }
        }
    }

    private fun showSuccessPurchaseData(data: PurchaseOrderLine) {
        with(viewBinding.includeTransferInput) {
            tvTransferItemName.text = data.itemRefNo
            tilTransferinputName.editText?.setText(data.no)
            tvTransferQty.text = "${data.alredyScanned}/${data.quantity}"
        }
    }

    private fun setupView() {
        with(viewBinding) {
            toolbarPickingListInput.title = viewModel.getCompanyName()
            when (args.transferType) {
                INVENTORY, PURCHASE -> {
                    includeTransferInput.etTransferinputBincode.requestFocus()
                }
                else -> {
                    includeTransferInput.etTransferInputBarcode.requestFocus()
                }
            }
            args.barcodeNo?.let {
                includeTransferInput.etTransferInputBarcode.setText(it)
                includeTransferInput.etTransferInputBarcode.isEnabled = false
                getPickingListLineData(it, args.binCode)
                includeTransferInput.etTranferinputQty.requestFocus()
            }
            if (args.stockId != 0) {
                when (args.transferType) {
                    PURCHASE -> viewModel.getPurchaseLineValue(args.transferNo, "", args.stockId)
                    else -> {}
                }
            }
            includeTransferInput.etTransferInputBarcode.setOnEditorActionListener { _, keyCode, event ->
                if (((event?.action ?: -1) == KeyEvent.ACTION_DOWN)
                    || keyCode == EditorInfo.IME_ACTION_NEXT && (args.transferType != STOCKOPNAME)
                ) {
                    getPickingListLineData(
                        includeTransferInput.etTransferInputBarcode.text.toString(),
                        includeTransferInput.etTransferinputBincode.text.toString()
                    )
                    includeTransferInput.etTranferinputQty.requestFocus()
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

            includeTransferInput.etTransferinputBincode.setOnEditorActionListener { _, actionId, event ->
                if (((event?.action ?: -1) == KeyEvent.ACTION_DOWN)
                    || actionId == EditorInfo.IME_ACTION_NEXT && (args.transferType == INVENTORY)
                ) {
                    includeTransferInput.etTransferInputBarcode.requestFocus()
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

            tvTransferInputTitle.text = when (args.transferType) {
                SHIPMENT -> getString(R.string.transfer_store)
                RECEIPT -> getString(R.string.transfer_receipt_title)
                PURCHASE -> getString(R.string.purchase_order_title)
                STOCKOPNAME -> getString(R.string.stock_opname_title)
                INVENTORY -> getString(R.string.inventory_pick_title)
            }
            includeTransferInput.tilTransferBincode.isVisible =
                (args.transferType == STOCKOPNAME || args.transferType == INVENTORY || args.transferType == PURCHASE || args.transferType == RECEIPT)
            includeTransferInput.tilInputBox.isVisible = when (args.transferType) {
                INVENTORY -> true
                STOCKOPNAME -> false
                else -> true
            }
        }
    }


    private fun getPickingListLineData(barcode: String, binCode: String = "") {
        when (args.transferType) {
            SHIPMENT -> viewModel.getShipmentListLineValue(args.transferNo, barcode, args.stockId)
            RECEIPT -> viewModel.getReceiptListLineValue(args.transferNo, barcode, args.stockId)
            PURCHASE -> viewModel.getPurchaseLineValue(args.transferNo, barcode, args.stockId)
            STOCKOPNAME -> viewModel.getStockOpnameValue(barcode, args.stockId, binCode)
            INVENTORY -> viewModel.getInventoryLineValue(
                args.transferNo,
                barcode,
                binCode,
                args.stockId
            )
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
                    args.transferType,
                    box = includeTransferInput.etBoxInput.text.toString(),
                    bin = includeTransferInput.etTransferinputBincode.text.toString()
                )
            }
            toolbarPickingListInput.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.history_data -> {
                        val action = when (args.transferType) {
                            SHIPMENT -> TransferInputFragmentDirections.actionReceivingFragmentToHistoryInputFragment(
                                documentNo = args.transferNo,
                                historyType = HistoryType.SHIPMENT
                            )

                            RECEIPT -> TransferInputFragmentDirections.actionReceivingFragmentToHistoryInputFragment(
                                documentNo = args.transferNo,
                                historyType = HistoryType.RECEIPT
                            )

                            PURCHASE -> TransferInputFragmentDirections.actionReceivingFragmentToHistoryInputFragment(
                                documentNo = args.transferNo,
                                historyType = HistoryType.PURCHASE
                            )

                            STOCKOPNAME ->
                                TransferInputFragmentDirections.actionTransferInputFragmentToHistoryInputFragment(
                                    documentNo = args.transferNo,
                                    historyType = HistoryType.STOCKOPNAME
                                )

                            INVENTORY -> TransferInputFragmentDirections.actionTransferInputFragmentToHistoryInputFragment(
                                documentNo = args.transferNo,
                                historyType = HistoryType.INVENTORY
                            )
                        }
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

    override fun onStop() {
        super.onStop()
        mpFail?.release()
        mpSuccess?.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        mpFail?.release()
        mpSuccess?.release()
    }
}
