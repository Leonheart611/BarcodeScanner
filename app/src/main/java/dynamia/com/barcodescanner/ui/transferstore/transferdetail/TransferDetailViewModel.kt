package dynamia.com.barcodescanner.ui.transferstore.transferdetail

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dynamia.com.barcodescanner.ui.transferstore.TransferType
import dynamia.com.barcodescanner.ui.transferstore.TransferType.*
import dynamia.com.barcodescanner.ui.transferstore.transferinput.TransferInputViewModel
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.entinty.*
import dynamia.com.core.data.repository.PurchaseOrderRepository
import dynamia.com.core.data.repository.StockOpnameRepository
import dynamia.com.core.data.repository.TransferReceiptRepository
import dynamia.com.core.data.repository.TransferShipmentRepository
import dynamia.com.core.util.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TransferDetailViewModel(
    val transferShipmentRepository: TransferShipmentRepository,
    private val transferReceiptRepository: TransferReceiptRepository,
    val purchaseOrderRepository: PurchaseOrderRepository,
    val sharedPreferences: SharedPreferences,
    private val stockOpnameRepository: StockOpnameRepository,
) : ViewModelBase(sharedPreferences) {

    private val _pickingDetailViewState = MutableLiveData<TransferListViewState>()
    val transferListViewState: LiveData<TransferListViewState> by lazy { _pickingDetailViewState }

    private val _pickingPostViewState = MutableLiveData<PickingDetailPostViewState>()
    val pickingPostViewState: LiveData<PickingDetailPostViewState> by lazy { _pickingPostViewState }

    private val _transferInputViewState =
        MutableLiveData<TransferDetailInputViewState>()
    val transferInputViewState: LiveData<TransferDetailInputViewState> by lazy { _transferInputViewState }

    private var transferLineData: TransferShipmentLine? = null
    private var transferHeaderData: TransferShipmentHeader? = null
    private var transferReceiptHeader: TransferReceiptHeader? = null

    private var purchaseLineData: PurchaseOrderLine? = null
    private var stockOpnameData: StockOpnameData? = null


    fun insertDataValue(no: String, identifier: String, transferType: TransferType) {
        viewModelScope.launch {
            try {
                io {
                    when (transferType) {
                        SHIPMENT -> {
                            transferShipmentRepository.getTransferHeaderDetail(no).collect {
                                transferHeaderData = it
                            }
                            transferShipmentRepository.getLineDetailFromBarcode(no, identifier)
                                .collect { data ->
                                    ui {
                                        transferLineData = data
                                        insertTransferInput("1")
                                    }
                                }
                        }
                        RECEIPT -> {
                            transferReceiptRepository.getTransferHeaderDetail(no).collect {
                                transferReceiptHeader = it
                            }
                            transferShipmentRepository.getLineDetailFromBarcode(no, identifier)
                                .collect { data ->
                                    ui {
                                        transferLineData = data
                                        insertTransferReceiptInput("1")
                                    }
                                }
                        }
                        PURCHASE -> {
                            purchaseOrderRepository.getPurchaseOrderLineByBarcode(no, identifier)
                                .collect { data ->
                                    ui {
                                        purchaseLineData = data
                                        insertPurchaseInput("1")
                                    }
                                }
                        }
                        STOCKOPNAME -> {
                            stockOpnameRepository.getStockOpnameDetailByBarcode(identifier)
                                .collect { data ->
                                    ui {
                                        stockOpnameData = data
                                        insertStockOpnameData("1")
                                    }
                                }
                        }
                    }

                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    TransferDetailInputViewState.ErrorGetData(e.localizedMessage)
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
                                insertDateTime = "${getCurrentDate()}T${getCurrentTime()}"
                            )
                        )
                        ui {
                            _transferInputViewState.value =
                                TransferDetailInputViewState.SuccessSaveData
                        }
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    TransferDetailInputViewState.ErrorGetData(e.localizedMessage)
            }
        }
    }

    private fun insertTransferInput(qty: String) {
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
                                        TransferDetailInputViewState.SuccessSaveData
                                } else {
                                    _transferInputViewState.value =
                                        TransferDetailInputViewState.ErrorSaveData
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    TransferDetailInputViewState.ErrorGetData(e.localizedMessage)
            }

        }
    }


    fun postShipmentData() {
        viewModelScope.launch {
            try {
                var dataPosted = 0
                io {
                    val listEntries =
                        transferShipmentRepository.getAllUnsycnTransferInput()
                    ui {
                        _pickingPostViewState.value =
                            PickingDetailPostViewState.GetUnpostedData(listEntries.size)
                        _pickingPostViewState.value =
                            PickingDetailPostViewState.GetSuccessfullyPostedData(dataPosted)
                    }
                    for (data in listEntries) {
                        val param = gson.toJson(data)
                        transferShipmentRepository.postTransferData(param).collect {
                            dataPosted++
                            ui {
                                _pickingPostViewState.value =
                                    PickingDetailPostViewState.GetSuccessfullyPostedData(dataPosted)
                            }
                            data.apply {
                                sync_status = true
                            }
                            transferShipmentRepository.updateTransferInput(data)
                        }
                    }
                    ui { _pickingPostViewState.value = PickingDetailPostViewState.AllDataPosted }
                }
            } catch (e: Exception) {
                _pickingPostViewState.value =
                    PickingDetailPostViewState.ErrorPostData(e.localizedMessage)
            }
        }
    }

    fun postReceiptData() {
        viewModelScope.launch {
            try {
                var dataPosted = 0
                io {
                    val listEntries =
                        transferReceiptRepository.getAllUnsycnTransferReceiptInput()
                    ui {
                        _pickingPostViewState.value =
                            PickingDetailPostViewState.GetUnpostedData(listEntries.size)
                        _pickingPostViewState.value =
                            PickingDetailPostViewState.GetSuccessfullyPostedData(dataPosted)
                    }
                    for (data in listEntries) {
                        val param = gson.toJson(data)
                        transferReceiptRepository.postTransferReceiptInput(param).collect {
                            dataPosted++
                            ui {
                                _pickingPostViewState.value =
                                    PickingDetailPostViewState.GetSuccessfullyPostedData(dataPosted)
                            }
                            data.apply {
                                sync_status = true
                            }
                            transferReceiptRepository.updateTransferReceiptInput(data)
                        }
                    }
                    ui { _pickingPostViewState.value = PickingDetailPostViewState.AllDataPosted }
                }
            } catch (e: Exception) {
                _pickingPostViewState.value =
                    PickingDetailPostViewState.ErrorPostData(e.localizedMessage)
            }
        }
    }

    fun postPurchaseData() {
        viewModelScope.launch {
            try {
                var dataPosted = 0
                io {
                    val listEntries =
                        purchaseOrderRepository.getAllUnSyncPurchaseInput()
                    ui {
                        _pickingPostViewState.value =
                            PickingDetailPostViewState.GetUnpostedData(listEntries.size)
                        _pickingPostViewState.value =
                            PickingDetailPostViewState.GetSuccessfullyPostedData(dataPosted)
                    }
                    for (data in listEntries) {
                        val param = gson.toJson(data)
                        purchaseOrderRepository.postPurchaseOrderData(param).collect {
                            dataPosted++
                            ui {
                                _pickingPostViewState.value =
                                    PickingDetailPostViewState.GetSuccessfullyPostedData(dataPosted)
                            }
                            data.postSuccess()
                            purchaseOrderRepository.updatePurchaseInputData(data)
                        }
                    }
                    ui { _pickingPostViewState.value = PickingDetailPostViewState.AllDataPosted }
                }
            } catch (e: Exception) {
                _pickingPostViewState.value =
                    PickingDetailPostViewState.ErrorPostData(e.localizedMessage)
            }
        }
    }


    fun getTransferShipingDetail(no: String) {
        viewModelScope.launch {
            try {
                io {
                    transferShipmentRepository.getTransferHeaderDetail(no).collect { data ->
                        ui {
                            _pickingDetailViewState.value =
                                TransferListViewState.SuccessGetLocalData(data)
                        }
                    }
                }
            } catch (e: Exception) {
                _pickingDetailViewState.value =
                    TransferListViewState.ErrorGetLocalData(e.localizedMessage)
            }
        }
    }

    fun getTransferReceiptDetail(no: String) {
        viewModelScope.launch {
            try {
                io {
                    transferReceiptRepository.getTransferHeaderDetail(no).collect { data ->
                        ui {
                            _pickingDetailViewState.value =
                                TransferListViewState.SuccessGetReceiptLocalData(data)
                        }
                    }
                }
            } catch (e: Exception) {
                _pickingDetailViewState.value =
                    TransferListViewState.ErrorGetLocalData(e.localizedMessage)
            }
        }
    }

    fun getPurchaseOrderDetail(no: String) {
        viewModelScope.launch {
            try {
                io {
                    purchaseOrderRepository.getPurchaseOrderDetail(no).collect {
                        ui {
                            _pickingDetailViewState.value =
                                TransferListViewState.SuccessGetPurchaseData(it)
                        }
                    }
                }
            } catch (e: Exception) {
                _pickingDetailViewState.value =
                    TransferListViewState.ErrorGetLocalData(e.localizedMessage)
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
                                        TransferDetailInputViewState.SuccessSaveData
                                } else {
                                    _transferInputViewState.value =
                                        TransferDetailInputViewState.ErrorSaveData
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    TransferDetailInputViewState.ErrorGetData(e.localizedMessage)
            }
        }
    }

    private fun insertPurchaseInput(qty: String) {
        viewModelScope.launch {
            try {
                io {
                    purchaseLineData?.let { line ->
                        val value = purchaseOrderRepository.insertPurchaseOrderData(
                            PurchaseInputData(
                                documentNo = line.documentNo,
                                quantity = qty.toInt(),
                                lineNo = line.lineNo,
                                itemNo = line.no,
                                transferFromBinCode = "",
                                transferToBinCode = "",
                                userName = sharedPreferences.getUserName(),
                                insertDateTime = "${getCurrentDate()}T${getCurrentTime()}"
                            )
                        )
                        ui {
                            if (value) {
                                _transferInputViewState.value =
                                    TransferDetailInputViewState.SuccessSaveData
                            } else {
                                _transferInputViewState.value =
                                    TransferDetailInputViewState.ErrorSaveData
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    TransferDetailInputViewState.ErrorGetData(e.localizedMessage)
            }
        }
    }


    sealed class TransferListViewState {
        class SuccessGetLocalData(val value: TransferShipmentHeader) : TransferListViewState()
        class SuccessGetReceiptLocalData(val values: TransferReceiptHeader) :
            TransferListViewState()

        class SuccessGetPickingLineData(val values: MutableList<TransferShipmentLine>) :
            TransferListViewState()

        class ErrorGetLocalData(val message: String) : TransferListViewState()
        class SuccessGetPurchaseData(val value: PurchaseOrderHeader) : TransferListViewState()
    }

    sealed class PickingDetailPostViewState {
        class GetUnpostedData(val data: Int) : PickingDetailPostViewState()
        class GetSuccessfullyPostedData(val data: Int) : PickingDetailPostViewState()
        class ErrorPostData(val message: String) : PickingDetailPostViewState()
        object AllDataPosted : PickingDetailPostViewState()
    }

    sealed class TransferDetailInputViewState {
        object SuccessSaveData : TransferDetailInputViewState()
        object ErrorSaveData : TransferDetailInputViewState()
        class ErrorGetData(val message: String) : TransferDetailInputViewState()
    }


}
