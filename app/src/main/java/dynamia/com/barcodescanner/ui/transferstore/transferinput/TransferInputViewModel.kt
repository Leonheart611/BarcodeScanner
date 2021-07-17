package dynamia.com.barcodescanner.ui.transferstore.transferinput

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dynamia.com.barcodescanner.ui.transferstore.TransferType
import dynamia.com.barcodescanner.ui.transferstore.TransferType.*
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.entinty.*
import dynamia.com.core.data.repository.PurchaseOrderRepository
import dynamia.com.core.data.repository.StockOpnameRepository
import dynamia.com.core.data.repository.TransferReceiptRepository
import dynamia.com.core.data.repository.TransferShipmentRepository
import dynamia.com.core.util.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TransferInputViewModel(
    private val transferShipmentRepository: TransferShipmentRepository,
    private val transferReceiptRepository: TransferReceiptRepository,
    private val purchaseOrderRepository: PurchaseOrderRepository,
    private val stockOpnameRepository: StockOpnameRepository,
    val sharedPreferences: SharedPreferences,
) : ViewModelBase(sharedPreferences) {

    private val _transferInputViewState = MutableLiveData<TransferInputViewState>()
    val transferInputViewState: LiveData<TransferInputViewState> by lazy { _transferInputViewState }
    private val _inputValidaton = MutableLiveData<InputValidation>()
    val inputValidation: LiveData<InputValidation> by lazy { _inputValidaton }

    private var transferLineData: TransferShipmentLine? = null
    private var transferHeaderData: TransferShipmentHeader? = null
    private var transferReceiptHeader: TransferReceiptHeader? = null

    private var purchaseHeader: PurchaseOrderHeader? = null
    private var purchaseLineData: PurchaseOrderLine? = null

    private var stockOpnameData: StockOpnameData? = null

    fun getHistoryValueDetail(no: Int) {
        viewModelScope.launch {
            io {
                transferShipmentRepository.getTransferInputHistory(no).collect {
                    ui {
                        _transferInputViewState.value =
                            TransferInputViewState.SuccessGetHistoryValue(it)
                    }
                }
            }
        }
    }

    fun getPurchaseHistoryDetail(no: Int) {
        viewModelScope.launch {
            io {
                purchaseOrderRepository.getPurchaseInputDetail(no).collect {
                    ui {
                        _transferInputViewState.value =
                            TransferInputViewState.SuccessGetPurchaseHistory(it)
                    }
                }
            }
        }
    }

    fun getHistoryReceiptDetail(no: Int) {
        viewModelScope.launch {
            io {
                transferReceiptRepository.getTransferInputDetail(no).collect {
                    ui {
                        _transferInputViewState.value =
                            TransferInputViewState.SuccessGetReceiptHistoryValue(it)
                    }
                }
            }
        }
    }

    fun getStockOpnameValue(identifier: String, id: Int) {
        viewModelScope.launch {
            try {
                _transferInputViewState.value =
                    TransferInputViewState.LoadingSearchPickingList(true)
                io {
                    if (id == 0) {
                        stockOpnameRepository.getStockOpnameDetailByBarcode(identifier)
                            .collect { data ->
                                ui {
                                    stockOpnameData = data
                                    _transferInputViewState.value =
                                        TransferInputViewState.SuccessGetStockOpnameValue(data)
                                    _transferInputViewState.value =
                                        TransferInputViewState.LoadingSearchPickingList(false)
                                }
                            }
                    } else {
                        stockOpnameRepository.getStockOpnameDetailByBarcode(identifier, id)
                            .collect { data ->
                                ui {
                                    stockOpnameData = data
                                    _transferInputViewState.value =
                                        TransferInputViewState.SuccessGetStockOpnameValue(data)
                                    _transferInputViewState.value =
                                        TransferInputViewState.LoadingSearchPickingList(false)
                                }
                            }
                    }


                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    TransferInputViewState.LoadingSearchPickingList(false)
                _transferInputViewState.value =
                    TransferInputViewState.ErrorGetData(e.localizedMessage)
            }
        }
    }

    fun getShipmentListLineValue(no: String, identifier: String) {
        viewModelScope.launch {
            try {
                _transferInputViewState.value =
                    TransferInputViewState.LoadingSearchPickingList(true)
                io {
                    transferShipmentRepository.getTransferHeaderDetail(no).collect {
                        transferHeaderData = it
                    }
                    transferShipmentRepository.getLineDetailFromBarcode(no, identifier)
                        .collect { data ->
                            ui {
                                transferLineData = data
                                _transferInputViewState.value =
                                    TransferInputViewState.SuccessGetValue(data)
                                _transferInputViewState.value =
                                    TransferInputViewState.LoadingSearchPickingList(false)
                            }
                        }

                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    TransferInputViewState.LoadingSearchPickingList(false)
                _transferInputViewState.value =
                    TransferInputViewState.ErrorGetData(e.localizedMessage)
            }
        }
    }

    fun getReceiptListLineValue(no: String, identifier: String) {
        viewModelScope.launch {
            try {
                _transferInputViewState.value =
                    TransferInputViewState.LoadingSearchPickingList(true)
                io {
                    transferReceiptRepository.getTransferHeaderDetail(no).collect {
                        transferReceiptHeader = it
                    }
                    transferShipmentRepository.getLineDetailFromBarcode(no, identifier)
                        .collect { data ->
                            ui {
                                transferLineData = data
                                _transferInputViewState.value =
                                    TransferInputViewState.SuccessGetValue(data)
                                _transferInputViewState.value =
                                    TransferInputViewState.LoadingSearchPickingList(false)
                            }
                        }

                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    TransferInputViewState.LoadingSearchPickingList(false)
                _transferInputViewState.value =
                    TransferInputViewState.ErrorGetData(e.localizedMessage)
            }
        }
    }

    fun getPurchaseLineValue(no: String, identifier: String) {
        viewModelScope.launch {
            try {
                _transferInputViewState.value =
                    TransferInputViewState.LoadingSearchPickingList(true)
                io {
                    with(purchaseOrderRepository) {
                        getPurchaseOrderDetail(no).collect {
                            purchaseHeader = it
                        }
                        getPurchaseOrderLineByBarcode(no, identifier).collect { data ->
                            ui {
                                purchaseLineData = data
                                _transferInputViewState.value =
                                    TransferInputViewState.SuccessGetPurchaseValue(data)
                                _transferInputViewState.value =
                                    TransferInputViewState.LoadingSearchPickingList(false)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    TransferInputViewState.LoadingSearchPickingList(false)
                _transferInputViewState.value =
                    TransferInputViewState.ErrorGetData(e.localizedMessage)
            }
        }
    }

    private fun insertStockOpnameData(qty: String) {
        viewModelScope.launch {
            try {
                io {
                    stockOpnameData?.let { data ->
                        stockOpnameRepository.insertInputStockOpname(
                            StockOpnameInputData(
                                documentNo = data.documentNo,
                                quantity = qty.toInt(),
                                lineNo = data.lineNo,
                                itemNo = data.itemNo,
                                binCode = data.binCode,
                                userName = sharedPreferences.getUserName(),
                                insertDateTime = "${getCurrentDate()}T${getCurrentTime()}",
                                locationCode = data.locationCode,
                                headerId = data.id!!
                            )
                        )
                        ui {
                            _transferInputViewState.value = TransferInputViewState.SuccessSaveData
                        }
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    TransferInputViewState.ErrorGetData(e.localizedMessage)
            }
        }
    }

    private fun insertPurchaseInput(qty: String) {
        viewModelScope.launch {
            try {
                io {
                    purchaseHeader?.let {
                        purchaseLineData?.let { line ->
                            val value = purchaseOrderRepository.insertPurchaseOrderData(
                                PurchaseInputData(
                                    documentNo = line.documentNo,
                                    quantity = qty.toInt(),
                                    lineNo = line.lineNo,
                                    itemNo = line.no,
                                    userName = sharedPreferences.getUserName(),
                                    insertDateTime = "${getCurrentDate()}T${getCurrentTime()}"
                                )
                            )
                            ui {
                                if (value) {
                                    _transferInputViewState.value =
                                        TransferInputViewState.SuccessSaveData
                                } else {
                                    _transferInputViewState.value =
                                        TransferInputViewState.ErrorSaveData("QTY data melebihi batas yang di perbolehkan")
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    TransferInputViewState.ErrorGetData(e.localizedMessage)
            }
        }
    }


    private fun insertTransferShipmentInput(qty: String) {
        viewModelScope.launch {
            try {
                io {
                    transferHeaderData?.let { header ->
                        transferLineData?.let { line ->
                            val value = transferShipmentRepository.insertTransferInput(
                                TransferInputData(
                                    documentNo = line.documentNo,
                                    quantity = qty.toInt(),
                                    lineNo = line.lineNo,
                                    itemNo = line.no,
                                    transferFromBinCode = header.transferFromCode,
                                    transferToBinCode = header.transferToCode,
                                    userName = sharedPreferences.getUserName(),
                                    insertDateTime = "${getCurrentDate()}T${getCurrentTime()}"
                                )
                            )
                            ui {
                                if (value) {
                                    _transferInputViewState.value =
                                        TransferInputViewState.SuccessSaveData
                                } else {
                                    _transferInputViewState.value =
                                        TransferInputViewState.ErrorSaveData("QTY data melebihi batas yang di perbolehkan")
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    TransferInputViewState.ErrorGetData(e.localizedMessage)
            }
        }
    }

    private fun insertTransferReceiptInput(qty: String) {
        viewModelScope.launch {
            try {
                io {
                    transferReceiptHeader?.let { header ->
                        transferLineData?.let { line ->
                            val value = transferReceiptRepository.insertTransferReceiptInput(
                                TransferReceiptInput(
                                    documentNo = line.documentNo,
                                    quantity = qty.toInt(),
                                    lineNo = line.lineNo,
                                    itemNo = line.no,
                                    transferFromBinCode = header.transferFromCode,
                                    transferToBinCode = header.transferToCode,
                                    userName = sharedPreferences.getUserName(),
                                    insertDateTime = "${getCurrentDate()}T${getCurrentTime()}"
                                )
                            )
                            ui {
                                if (value) {
                                    _transferInputViewState.value =
                                        TransferInputViewState.SuccessSaveData
                                } else {
                                    _transferInputViewState.value =
                                        TransferInputViewState.ErrorSaveData("QTY data melebihi batas yang di perbolehkan")
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    TransferInputViewState.ErrorGetData(e.localizedMessage)
            }
        }
    }

    fun checkUserInputValidation(
        barcode: String,
        qty: String,
        typesInput: TransferType,
    ) {
        if (barcode.isEmpty()) {
            _inputValidaton.value = InputValidation.BarcodeEmpty
        }
        if (qty.isEmpty()) {
            _inputValidaton.value = InputValidation.QtyEmpty
        }
        if (barcode.isNotEmpty() && qty.isNotEmpty()) {
            when (typesInput) {
                SHIPMENT -> insertTransferShipmentInput(qty)
                RECEIPT -> insertTransferReceiptInput(qty)
                PURCHASE -> insertPurchaseInput(qty)
                STOCKOPNAME -> insertStockOpnameData(qty)
            }
        }
    }

    fun deleteTransferShipmentEntry(id: Int) {
        viewModelScope.launch {
            try {
                io {
                    transferShipmentRepository.deleteTransferInput(id)
                    ui {
                        _transferInputViewState.value =
                            TransferInputViewState.SuccessDeleteData
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    TransferInputViewState.ErrorDeleteData(e.localizedMessage)
            }
        }
    }

    fun updatePurchaseInputData(id: Int, newQty: Int) {
        viewModelScope.launch {
            try {
                io {
                    purchaseOrderRepository.updatePurchaseInputDataQty(id, newQty)
                    ui {
                        _transferInputViewState.value = TransferInputViewState.SuccessUpdateData
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    TransferInputViewState.ErrorUpdateData(e.localizedMessage)
            }
        }
    }

    fun deletePurchaseOrderEntry(id: Int) {
        viewModelScope.launch {
            try {
                io {
                    purchaseOrderRepository.deletePurchaseInputData(id)
                    ui {
                        _transferInputViewState.value =
                            TransferInputViewState.SuccessDeleteData
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    TransferInputViewState.ErrorDeleteData(e.localizedMessage)
            }
        }
    }

    fun deleteTransferReceiptEntry(id: Int) {
        viewModelScope.launch {
            try {
                io {
                    transferReceiptRepository.deleteTransferInput(id)
                    ui {
                        _transferInputViewState.value =
                            TransferInputViewState.SuccessDeleteData
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    TransferInputViewState.ErrorDeleteData(e.localizedMessage)
            }
        }
    }

    fun updateTransferShipmentEntry(id: Int, newQty: Int) {
        viewModelScope.launch {
            try {
                io {
                    transferShipmentRepository.updateTransferShipmentInputQty(id, newQty)
                        .collect { result ->
                            ui {
                                if (result) {
                                    _transferInputViewState.value =
                                        TransferInputViewState.SuccessUpdateData
                                } else {
                                    _transferInputViewState.value =
                                        TransferInputViewState.ErrorUpdateData("Qty has Reach maximum allowed, please change")
                                }
                            }
                        }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    TransferInputViewState.ErrorUpdateData(e.localizedMessage)
            }
        }
    }

    fun updateTransferReceiptEntry(id: Int, newQty: Int) {
        viewModelScope.launch {
            try {
                io {
                    transferReceiptRepository.updateTransferReceiptInputQty(id, newQty)
                        .collect { result ->
                            ui {
                                if (result) {
                                    _transferInputViewState.value =
                                        TransferInputViewState.SuccessUpdateData
                                } else {
                                    _transferInputViewState.value =
                                        TransferInputViewState.ErrorUpdateData("Qty has Reach maximum allowed, please change")
                                }
                            }
                        }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    TransferInputViewState.ErrorUpdateData(e.localizedMessage)
            }
        }
    }

    fun getStockOpnameHistoryDetail(id: Int) {
        viewModelScope.launch {
            io {
                stockOpnameRepository.getInputStockOpnameDetail(id).collect {
                    ui {
                        _transferInputViewState.value =
                            TransferInputViewState.SuccessGetStockInputHistory(it)
                    }
                }
            }
        }
    }

    fun updateStockOpnameInputData(no: Int, newQty: Int) {
        viewModelScope.launch {
            try {
                io {
                    stockOpnameRepository.updateInputStockOpnameQty(no, newQty)
                    ui {
                        _transferInputViewState.value = TransferInputViewState.SuccessUpdateData
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    TransferInputViewState.ErrorUpdateData(e.localizedMessage)
            }
        }
    }

    fun deleteStockOpnameInputData(no: Int) {
        viewModelScope.launch {
            try {
                io {
                    stockOpnameRepository.deleteInputStockOpname(no)
                    ui {
                        _transferInputViewState.value =
                            TransferInputViewState.SuccessDeleteData
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    TransferInputViewState.ErrorDeleteData(e.localizedMessage)
            }
        }
    }


    sealed class TransferInputViewState {
        /**
         * History Success Value
         */
        class SuccessGetValue(val data: TransferShipmentLine) : TransferInputViewState()
        class SuccessGetHistoryValue(val data: TransferInputData) : TransferInputViewState()
        class SuccessGetReceiptHistoryValue(val data: TransferReceiptInput) :
            TransferInputViewState()

        class SuccessGetStockInputHistory(val data: StockOpnameInputData) : TransferInputViewState()

        class SuccessGetPurchaseHistory(val data: PurchaseInputData) : TransferInputViewState()

        class SuccessGetPurchaseValue(val data: PurchaseOrderLine) : TransferInputViewState()
        class SuccessGetStockOpnameValue(val data: StockOpnameData) : TransferInputViewState()

        object SuccessSaveData : TransferInputViewState()
        class ErrorSaveData(val message: String) : TransferInputViewState()
        class LoadingSearchPickingList(val status: Boolean) : TransferInputViewState()

        object SuccessDeleteData : TransferInputViewState()
        class ErrorDeleteData(val message: String) : TransferInputViewState()

        object SuccessUpdateData : TransferInputViewState()
        class ErrorUpdateData(val message: String) : TransferInputViewState()

        class ErrorGetData(val message: String) : TransferInputViewState()
    }

    sealed class InputValidation {
        object BarcodeEmpty : InputValidation()
        object QtyEmpty : InputValidation()
    }
}
