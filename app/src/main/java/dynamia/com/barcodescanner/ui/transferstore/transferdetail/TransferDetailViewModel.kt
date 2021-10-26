package dynamia.com.barcodescanner.ui.transferstore.transferdetail

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dynamia.com.barcodescanner.ui.transferstore.TransferType
import dynamia.com.barcodescanner.ui.transferstore.TransferType.*
import dynamia.com.barcodescanner.di.ViewModelBase
import dynamia.com.core.data.entinty.*
import dynamia.com.core.data.repository.*
import dynamia.com.core.util.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransferDetailViewModel @Inject constructor(
    val transferShipmentRepository: TransferShipmentRepository,
    private val transferReceiptRepository: TransferReceiptRepository,
    val purchaseOrderRepository: PurchaseOrderRepository,
    val inventoryRepository: InventoryRepository,
    val sharedPreferences: SharedPreferences,
    private val stockOpnameRepository: StockOpnameRepository,
    private val binreclassRepository: BinreclassRepository,
) : ViewModelBase(sharedPreferences) {
    data class LineParam(
        val documentNo: String,
        var page: Int
    )

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
    private var inventoryPickHeader: InventoryPickHeader? = null

    private var purchaseLineData: PurchaseOrderLine? = null
    private var stockOpnameData: StockOpnameData? = null
    private var inventoryPickLine: InventoryPickLine? = null

    private val lineParam = MutableLiveData<LineParam>()
    val shipmentLineData: LiveData<List<TransferShipmentLine>> = Transformations.switchMap(
        lineParam
    ) { param ->
        transferShipmentRepository.getLineListFromHeaderLiveData(
            param.documentNo,
            param.page
        )
    }

    val transferReceipt: LiveData<List<TransferShipmentLine>> = Transformations.switchMap(
        lineParam
    ) { param ->
        transferShipmentRepository.getLineListFromReceiptLiveData(
            param.documentNo,
            param.page
        )
    }

    val purchaseLineLiveData: LiveData<List<PurchaseOrderLine>> =
        Transformations.switchMap(lineParam) { param ->
            purchaseOrderRepository.getPurchaseOrderLineByNo(param.documentNo, param.page)
        }

    val inventoryLineLiveData: LiveData<List<InventoryPickLine>> =
        Transformations.switchMap(lineParam) { param ->
            inventoryRepository.getAllInventoryPickLine(param.documentNo, param.page)
        }

    fun setLine(data: LineParam) {
        lineParam.value = data
    }

    fun insertDataValue(
        no: String,
        identifier: String,
        transferType: TransferType,
        binCode: String = "",
        box: String
    ) {
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
                                        insertTransferInput("1", box)
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
                                        insertTransferReceiptInput("1", box)
                                    }
                                }
                        }
                        PURCHASE -> {
                            purchaseOrderRepository.getPurchaseOrderLineByBarcode(no, identifier)
                                .collect { data ->
                                    ui {
                                        purchaseLineData = data
                                        insertPurchaseInput("1", box)
                                    }
                                }
                        }
                        STOCKOPNAME -> {
                            stockOpnameRepository.getStockOpnameDetailByBarcode(identifier, binCode)
                                .collect { data ->
                                    ui {
                                        stockOpnameData = data
                                        insertStockOpnameData("1", box)
                                    }
                                }
                        }
                        INVENTORY -> {
                            inventoryRepository.getInventoryHeaderDetail(no)
                                .collect { inventoryPickHeader = it }
                            inventoryRepository.getDetailInventoryPickLine(no, identifier)
                                .collect { inventoryPickLine = it }
                            insertInventoryData(box)
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

    /**
     * Get Detail Data From DB Local
     */

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

    fun getInventoryHeader(no: String) {
        viewModelScope.launch {
            try {
                io {
                    inventoryRepository.getInventoryHeaderDetail(no).collect {
                        ui {
                            _pickingDetailViewState.value =
                                TransferListViewState.SuccessGetInventoryData(it)
                        }
                    }
                }

            } catch (e: Exception) {
                _pickingDetailViewState.value =
                    TransferListViewState.ErrorGetLocalData(e.localizedMessage)
            }
        }
    }


    /**
     * Insert Data From Scan Automatic
     */

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
                                headerId = data.id!!, box = box
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

    private fun insertTransferInput(qty: String, box: String) {
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

    private fun insertInventoryData(box: String) {
        viewModelScope.launch {
            try {
                inventoryPickHeader?.let { header ->
                    inventoryPickLine?.let { line ->
                        inventoryRepository.insertInputInventory(
                            InventoryInputData(
                                tableID = 2,
                                documentNo = header.no,
                                lineNo = line.lineNo,
                                itemNo = line.itemRefNo,
                                quantity = 1,
                                box = box,
                                locationCode = header.locationCode,
                                userName = getUserName(),
                                insertDateTime = "${getCurrentDate()}T${getCurrentTime()}"
                            )
                        )
                    }
                }
            } catch (e: Exception) {

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

    private fun insertPurchaseInput(qty: String, box: String) {
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
                                insertDateTime = "${getCurrentDate()}T${getCurrentTime()}",
                                box = box
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

    fun getTransferDetailScanQty(no: String) {
        viewModelScope.launch {
            io {
                transferShipmentRepository.getQtyAndScanQtyLiveData(no).collect {
                    ui {
                        _pickingDetailViewState.value =
                            TransferListViewState.SuccessGetQtyTotal(it)
                    }
                }
            }
        }
    }

    fun getTransferReceiptScanQty(no: String) {
        viewModelScope.launch {
            io {
                transferReceiptRepository.getTransferReceiptQtyDetail(no).collect {
                    ui {
                        _pickingDetailViewState.value =
                            TransferListViewState.SuccessGetQtyTotal(it)
                    }
                }
            }
        }
    }

    fun getPurchaseScanQty(no: String) {
        viewModelScope.launch {
            io {
                purchaseOrderRepository.getPurchaseQtyDetail(no).collect {
                    ui {
                        _pickingDetailViewState.value =
                            TransferListViewState.SuccessGetQtyTotal(it)
                    }
                }
            }
        }
    }

    fun getInventoryScanQty(no: String) {
        viewModelScope.launch {
            io {
                inventoryRepository.getInventoryDetailQty(no).collect {
                    ui {
                        _pickingDetailViewState.value =
                            TransferListViewState.SuccessGetQtyTotal(it)
                    }
                }
            }
        }
    }


    /**
    Post Remote Input Data
     */

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


    fun postInventoryData() {
        viewModelScope.launch {
            try {
                var dataPosted = 0
                io {
                    val listEntries =
                        inventoryRepository.getUnpostedInventoryData()
                    ui {
                        _pickingPostViewState.value =
                            PickingDetailPostViewState.GetUnpostedData(listEntries.size)
                        _pickingPostViewState.value =
                            PickingDetailPostViewState.GetSuccessfullyPostedData(dataPosted)
                    }
                    for (data in listEntries) {
                        val param = gson.toJson(data)
                        inventoryRepository.postInventoryData(param).collect {
                            dataPosted++
                            ui {
                                _pickingPostViewState.value =
                                    PickingDetailPostViewState.GetSuccessfullyPostedData(dataPosted)
                            }
                            data.apply {
                                sync_status = true
                            }
                            inventoryRepository.updateInventoryInput(data)
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

    fun postBinReclassData() {
        viewModelScope.launch {
            try {
                var dataPosted = 0
                io {
                    val listEntries =
                        binreclassRepository.getAllUnSyncBinreclassnput()
                    val headerEntries = binreclassRepository.getAllUnsycnHeaderData()
                    ui {
                        _pickingPostViewState.value =
                            PickingDetailPostViewState.GetUnpostedData(listEntries.size)
                        _pickingPostViewState.value =
                            PickingDetailPostViewState.GetSuccessfullyPostedData(dataPosted)
                    }
                    for (header in headerEntries) {
                        val listUnpostedData =
                            binreclassRepository.getAllUnSyncBinreclassnputByHeaderId(headerId = header.id!!)

                        for (data in listUnpostedData) {
                            val param = gson.toJson(data)
                            binreclassRepository.postDataBinreclass(param).collect {
                                dataPosted++
                                ui {
                                    _pickingPostViewState.value =
                                        PickingDetailPostViewState.GetSuccessfullyPostedData(
                                            dataPosted
                                        )
                                }
                                data.postSuccess()
                                binreclassRepository.updateAllBinReclassBin(data)
                            }
                        }
                        header.apply {
                            sync_status = true
                        }
                        binreclassRepository.updateBinReclassHeader(header)
                    }
                    ui { _pickingPostViewState.value = PickingDetailPostViewState.AllDataPosted }
                }
            } catch (e: Exception) {
                _pickingPostViewState.value =
                    PickingDetailPostViewState.ErrorPostData(e.localizedMessage)
            }
        }
    }


    sealed class TransferListViewState {
        class SuccessGetLocalData(val value: TransferShipmentHeader) : TransferListViewState()
        class SuccessGetReceiptLocalData(val values: TransferReceiptHeader) :
            TransferListViewState()

        class SuccessGetInventoryData(val value: InventoryPickHeader) : TransferListViewState()

        class SuccessGetPickingLineData(val values: MutableList<TransferShipmentLine>) :
            TransferListViewState()


        class SuccessGetQtyTotal(val data: ScanQty) : TransferListViewState()
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
