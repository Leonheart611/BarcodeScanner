package dynamia.com.barcodescanner.ui.transferstore.transferinput

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dynamia.com.barcodescanner.di.ViewModelBase
import dynamia.com.barcodescanner.ui.transferstore.TransferType
import dynamia.com.barcodescanner.ui.transferstore.TransferType.*
import dynamia.com.core.data.entinty.*
import dynamia.com.core.data.repository.*
import dynamia.com.core.util.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransferInputViewModel @Inject constructor(
    private val transferShipmentRepository: TransferShipmentRepository,
    private val transferReceiptRepository: TransferReceiptRepository,
    private val purchaseOrderRepository: PurchaseOrderRepository,
    private val stockOpnameRepository: StockOpnameRepository,
    private val inventoryRepository: InventoryRepository,
    val sharedPreferences: SharedPreferences,
) : ViewModelBase(sharedPreferences) {

    private val _transferInputViewState = MutableLiveData<Event<TransferInputViewState>>()
    val transferInputViewState: LiveData<Event<TransferInputViewState>> by lazy { _transferInputViewState }
    private val _inputValidaton = MutableLiveData<InputValidation>()
    val inputValidation: LiveData<InputValidation> by lazy { _inputValidaton }

    private val _soundSuccess = MutableLiveData<Event<Boolean>>()
    val soundSuccess: LiveData<Event<Boolean>> by lazy { _soundSuccess }

    private var transferLineData: TransferShipmentLine? = null
    private var transferHeaderData: TransferShipmentHeader? = null
    private var transferReceiptHeader: TransferReceiptHeader? = null
    private var inventoryPickHeader: InventoryPickHeader? = null

    private var purchaseHeader: PurchaseOrderHeader? = null
    private var purchaseLineData: PurchaseOrderLine? = null
    private var inventoryPickLine: InventoryPickLine? = null

    private var stockOpnameData: StockOpnameData? = null

    private var id = MutableLiveData<Int>()

    val transferShipmentLine = Transformations.switchMap(id) {
        transferShipmentRepository.getLineDetailLiveData(it)
    }

    val purchaserOrderLine = Transformations.switchMap(id) {
        purchaseOrderRepository.getPurchaseOrderLineLiveData(it)
    }

    val inventoryLine = Transformations.switchMap(id) {
        inventoryRepository.getInventoryPickLineLiveData(it)
    }

    fun getHistoryValueDetail(no: Int) {
        viewModelScope.launch {
            io {
                transferShipmentRepository.getTransferInputHistory(no).collect {
                    ui {
                        _transferInputViewState.value =
                            Event(TransferInputViewState.SuccessGetHistoryValue(it))
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
                            Event(TransferInputViewState.SuccessGetPurchaseHistory(it))
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
                            Event(TransferInputViewState.SuccessGetReceiptHistoryValue(it))
                    }
                }
            }
        }
    }

    fun getStockOpnameValue(identifier: String, id: Int, bincode: String) {
        viewModelScope.launch {
            try {
                _transferInputViewState.value =
                    Event(TransferInputViewState.LoadingSearchPickingList(true))
                io {
                    if (id == 0) {
                        stockOpnameRepository.getStockOpnameDetailByBarcode(identifier, bincode)
                            .collect { data ->
                                ui {
                                    stockOpnameData = data
                                    _transferInputViewState.value =
                                        Event(TransferInputViewState.LoadingSearchPickingList(false))
                                }
                            }
                    } else {
                        stockOpnameRepository.getStockOpnameDetailByBarcode(identifier, id)
                            .collect { data ->
                                ui {
                                    stockOpnameData = data
                                    _transferInputViewState.value =
                                        Event(TransferInputViewState.LoadingSearchPickingList(false))
                                }
                            }
                    }


                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    Event(TransferInputViewState.LoadingSearchPickingList(false))
                _transferInputViewState.value =
                    Event(TransferInputViewState.ErrorGetData(e.localizedMessage))
            }
        }
    }

    fun getShipmentListLineValue(no: String, identifier: String, stockId: Int) {
        viewModelScope.launch {
            try {
                _transferInputViewState.value =
                    Event(TransferInputViewState.LoadingSearchPickingList(true))
                io {
                    transferShipmentRepository.getTransferHeaderDetail(no).collect {
                        transferHeaderData = it
                    }
                    if (stockId != 0) {
                        transferShipmentRepository.getLineDetailFromId(stockId).collect { data ->
                            ui {
                                transferLineData = data
                                id.value = data.id!!
                                _soundSuccess.value = Event(true)
                                _transferInputViewState.value =
                                    Event(TransferInputViewState.LoadingSearchPickingList(false))
                            }
                        }
                    } else {
                        transferShipmentRepository.getLineDetailFromBarcode(no, identifier)
                            .collect { data ->
                                ui {
                                    transferLineData = data
                                    id.value = data.id!!
                                    _soundSuccess.value = Event(true)
                                    _transferInputViewState.value =
                                        Event(TransferInputViewState.LoadingSearchPickingList(false))
                                }
                            }
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    Event(TransferInputViewState.LoadingSearchPickingList(false))
                _transferInputViewState.value =
                    Event(TransferInputViewState.ErrorGetData(e.localizedMessage))
            }
        }
    }

    fun getInventoryLineValue(no: String, identifier: String, bincode: String, stockId: Int) {
        viewModelScope.launch {
            try {
                _transferInputViewState.value =
                    Event(TransferInputViewState.LoadingSearchPickingList(true))
                io {
                    inventoryRepository.getInventoryHeaderDetail(no)
                        .collect { inventoryPickHeader = it }

                    if (stockId != 0) {
                        inventoryRepository.getInventoryPickLineFromId(stockId).collect {
                            inventoryPickLine = it
                            ui {
                                id.value = it.id!!
                                _soundSuccess.value = Event(true)
                                _transferInputViewState.value =
                                    Event(TransferInputViewState.LoadingSearchPickingList(false))
                            }
                        }
                    } else {
                        inventoryRepository.getDetailInventoryPickLine(no, bincode, identifier)
                            .collect {
                                inventoryPickLine = it
                                ui {
                                    id.value = it.id!!
                                    _soundSuccess.value = Event(true)
                                    _transferInputViewState.value =
                                        Event(TransferInputViewState.LoadingSearchPickingList(false))
                                }
                            }
                    }
                }
            } catch (e: Exception) {
                _transferInputViewState.value =
                    Event(TransferInputViewState.LoadingSearchPickingList(false))
                _transferInputViewState.value =
                    Event(TransferInputViewState.ErrorGetData(e.localizedMessage))
            }
        }
    }


    fun getReceiptListLineValue(no: String, identifier: String, stockId: Int) {
        viewModelScope.launch {
            try {
                _transferInputViewState.value =
                    Event(TransferInputViewState.LoadingSearchPickingList(true))
                io {
                    transferReceiptRepository.getTransferHeaderDetail(no).collect {
                        transferReceiptHeader = it
                    }
                    if (stockId != 0) {
                        transferShipmentRepository.getLineDetailFromId(stockId)
                            .collect { data ->
                                ui {
                                    transferLineData = data
                                    id.value = data.id!!
                                    _soundSuccess.value = Event(true)
                                    _transferInputViewState.value =
                                        Event(TransferInputViewState.LoadingSearchPickingList(false))
                                }
                            }
                    } else {
                        transferShipmentRepository.getLineDetailFromBarcode(no, identifier)
                            .collect { data ->
                                ui {
                                    transferLineData = data
                                    id.value = data.id!!
                                    _soundSuccess.value = Event(true)
                                    _transferInputViewState.value =
                                        Event(TransferInputViewState.LoadingSearchPickingList(false))
                                }
                            }
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    Event(TransferInputViewState.LoadingSearchPickingList(false))
                _transferInputViewState.value =
                    Event(TransferInputViewState.ErrorGetData(e.localizedMessage))
            }
        }
    }

    fun getPurchaseLineValue(no: String, identifier: String, stockId: Int) {
        viewModelScope.launch {
            try {
                _transferInputViewState.value =
                    Event(TransferInputViewState.LoadingSearchPickingList(true))
                io {
                    with(purchaseOrderRepository) {
                        getPurchaseOrderDetail(no).collect {
                            purchaseHeader = it
                        }
                        if (stockId != 0) {
                            getPurchaseOrderLineDetailById(stockId).collect { data ->
                                ui {
                                    purchaseLineData = data
                                    id.value = data.id!!
                                    _soundSuccess.value = Event(true)
                                    _transferInputViewState.value =
                                        Event(TransferInputViewState.LoadingSearchPickingList(false))
                                }
                            }
                        } else {
                            getPurchaseOrderLineByBarcode(no, identifier).collect { data ->
                                ui {
                                    purchaseLineData = data
                                    id.value = data.id!!
                                    _soundSuccess.value = Event(true)
                                    _transferInputViewState.value =
                                        Event(TransferInputViewState.LoadingSearchPickingList(false))
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    Event(TransferInputViewState.LoadingSearchPickingList(false))
                _transferInputViewState.value =
                    Event(TransferInputViewState.ErrorGetData(e.localizedMessage))
            }
        }
    }

    private fun insertInventoryInput(qty: String, bin: String, box: String) {
        viewModelScope.launch {
            try {
                io {
                    inventoryPickHeader?.let { header ->
                        inventoryPickLine?.let { line ->
                            inventoryRepository.insertInputInventory(
                                InventoryInputData(
                                    documentNo = header.no,
                                    lineNo = line.lineNo,
                                    itemNo = line.itemNo,
                                    quantity = qty.toInt(),
                                    binCode = bin,
                                    locationCode = header.locationCode,
                                    userName = getUserName(),
                                    insertDateTime = "${getCurrentDate()}T${getCurrentTime()}",
                                    box = box
                                ), line.id ?: 0
                            ).collect {
                                ui {
                                    _transferInputViewState.value =
                                        Event(TransferInputViewState.SuccessSaveData)
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _transferInputViewState.value =
                    Event(TransferInputViewState.ErrorGetData(e.localizedMessage))
            }
        }
    }

    private fun insertStockOpnameData(qty: String, box: String) {
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
                                headerId = data.id!!,
                                box = box
                            )
                        )
                        ui {
                            _transferInputViewState.value =
                                Event(TransferInputViewState.SuccessSaveData)
                        }
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    Event(TransferInputViewState.ErrorGetData(e.localizedMessage))
            }
        }
    }

    private fun insertPurchaseInput(qty: String, box: String, binCode: String) {
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
                                    insertDateTime = "${getCurrentDate()}T${getCurrentTime()}",
                                    box = box,
                                    newBinCode = binCode
                                )
                            )
                            ui {
                                if (value) {
                                    _transferInputViewState.value =
                                        Event(TransferInputViewState.SuccessSaveData)
                                } else {
                                    _transferInputViewState.value =
                                        Event(TransferInputViewState.ErrorSaveData("QTY data melebihi batas yang di perbolehkan"))
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    Event(TransferInputViewState.ErrorGetData(e.localizedMessage))
            }
        }
    }

    private fun insertTransferShipmentInput(qty: String, box: String) {
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
                                    itemNo = line.itemNo,
                                    transferFromBinCode = header.transferFromCode,
                                    transferToBinCode = header.transferToCode,
                                    userName = sharedPreferences.getUserName(),
                                    insertDateTime = "${getCurrentDate()}T${getCurrentTime()}",
                                    box = box
                                )
                            )
                            ui {
                                if (value) {
                                    _transferInputViewState.value =
                                        Event(TransferInputViewState.SuccessSaveData)
                                } else {
                                    _transferInputViewState.value =
                                        Event(TransferInputViewState.ErrorSaveData("QTY data melebihi batas yang di perbolehkan"))
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    Event(TransferInputViewState.ErrorGetData(e.localizedMessage))
            }
        }
    }

    private fun insertTransferReceiptInput(qty: String, box: String) {
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
                                    itemNo = line.itemNo,
                                    transferFromBinCode = header.transferFromCode,
                                    transferToBinCode = header.transferToCode,
                                    userName = sharedPreferences.getUserName(),
                                    insertDateTime = "${getCurrentDate()}T${getCurrentTime()}",
                                    box = box
                                )
                            )
                            ui {
                                if (value) {
                                    _transferInputViewState.value =
                                        Event(TransferInputViewState.SuccessSaveData)
                                } else {
                                    _transferInputViewState.value =
                                        Event(TransferInputViewState.ErrorSaveData("QTY data melebihi batas yang di perbolehkan"))
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    Event(TransferInputViewState.ErrorGetData(e.localizedMessage))
            }
        }
    }

    fun checkUserInputValidation(
        barcode: String,
        qty: String,
        typesInput: TransferType,
        box: String,
        bin: String = ""
    ) {
        if (barcode.isEmpty()) {
            _inputValidaton.value = InputValidation.BarcodeEmpty
        }
        if (qty.isEmpty()) {
            _inputValidaton.value = InputValidation.QtyEmpty
        }
        if (barcode.isNotEmpty() && qty.isNotEmpty()) {
            when (typesInput) {
                SHIPMENT -> insertTransferShipmentInput(qty, box)
                RECEIPT -> insertTransferReceiptInput(qty, box)
                PURCHASE -> insertPurchaseInput(qty, box = box, binCode = bin)
                STOCKOPNAME -> insertStockOpnameData(qty, box)
                INVENTORY -> insertInventoryInput(qty, bin = bin, box = box)
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
                            Event(TransferInputViewState.SuccessDeleteData)
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    Event(TransferInputViewState.ErrorDeleteData(e.localizedMessage))
            }
        }
    }

    fun updatePurchaseInputData(id: Int, newQty: Int) {
        viewModelScope.launch {
            try {
                io {
                    purchaseOrderRepository.updatePurchaseInputDataQty(id, newQty)
                    ui {
                        _transferInputViewState.value =
                            Event(TransferInputViewState.SuccessUpdateData)
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    Event(TransferInputViewState.ErrorUpdateData(e.localizedMessage))
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
                            Event(TransferInputViewState.SuccessDeleteData)
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    Event(TransferInputViewState.ErrorDeleteData(e.localizedMessage))
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
                            Event(TransferInputViewState.SuccessDeleteData)
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    Event(TransferInputViewState.ErrorDeleteData(e.localizedMessage))
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
                                        Event(TransferInputViewState.SuccessUpdateData)
                                } else {
                                    _transferInputViewState.value =
                                        Event(TransferInputViewState.ErrorUpdateData("Qty has Reach maximum allowed, please change"))
                                }
                            }
                        }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    Event(TransferInputViewState.ErrorUpdateData(e.localizedMessage))
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
                                        Event( TransferInputViewState.SuccessUpdateData)
                                } else {
                                    _transferInputViewState.value =
                                        Event(TransferInputViewState.ErrorUpdateData("Qty has Reach maximum allowed, please change"))
                                }
                            }
                        }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    Event(TransferInputViewState.ErrorUpdateData(e.localizedMessage))
            }
        }
    }

    fun getStockOpnameHistoryDetail(id: Int) {
        viewModelScope.launch {
            io {
                stockOpnameRepository.getInputStockOpnameDetail(id).collect {
                    ui {
                        _transferInputViewState.value =
                            Event(TransferInputViewState.SuccessGetStockInputHistory(it))
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
                        _transferInputViewState.value = Event(TransferInputViewState.SuccessUpdateData)
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    Event(TransferInputViewState.ErrorUpdateData(e.localizedMessage))
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
                            Event(TransferInputViewState.SuccessDeleteData)
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    Event(TransferInputViewState.ErrorDeleteData(e.localizedMessage))
            }
        }
    }

    fun updateInventoryInput(id: Int, newQty: Int) {
        viewModelScope.launch {
            try {
                io {
                    inventoryRepository.updateInventoryQty(id, newQty).collect {
                        if (it) {
                            ui {
                                _transferInputViewState.value =
                                    Event(TransferInputViewState.SuccessUpdateData)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    Event(TransferInputViewState.ErrorUpdateData(e.localizedMessage))
            }
        }
    }

    fun getInventoryDetailInput(id: Int) {
        viewModelScope.launch {
            io {
                inventoryRepository.getInventoryInputDetail(id).collect {
                    ui {
                        _transferInputViewState.value =
                            Event(TransferInputViewState.SuccessGetInventoryInput(it))
                    }
                }
            }
        }
    }

    fun deleteInventory(id: Int) {
        viewModelScope.launch {
            try {
                io {
                    inventoryRepository.deleteInventoryInput(id)
                    ui {
                        _transferInputViewState.value =
                            Event(TransferInputViewState.SuccessDeleteData)
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    Event(TransferInputViewState.ErrorDeleteData(e.localizedMessage))
            }
        }

    }


    sealed class TransferInputViewState {
        /**
         * History Success Value
         */
        class SuccessGetHistoryValue(val data: TransferInputData) : TransferInputViewState()
        class SuccessGetReceiptHistoryValue(val data: TransferReceiptInput) :
            TransferInputViewState()

        class SuccessGetStockInputHistory(val data: StockOpnameInputData) : TransferInputViewState()

        class SuccessGetPurchaseHistory(val data: PurchaseInputData) : TransferInputViewState()

        class SuccessGetInventoryInput(val data: InventoryInputData) : TransferInputViewState()

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
