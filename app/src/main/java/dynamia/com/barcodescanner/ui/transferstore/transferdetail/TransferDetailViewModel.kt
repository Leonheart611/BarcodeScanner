package dynamia.com.barcodescanner.ui.transferstore.transferdetail

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dynamia.com.barcodescanner.BuildConfig
import dynamia.com.barcodescanner.di.ViewModelBase
import dynamia.com.barcodescanner.ui.transferstore.TransferType
import dynamia.com.barcodescanner.ui.transferstore.TransferType.*
import dynamia.com.core.data.entinty.*
import dynamia.com.core.data.repository.*
import dynamia.com.core.util.*
import kotlinx.coroutines.flow.catch
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
    private var inventoryPickLine: InventoryPickLine? = null

    private val lineParam = MutableLiveData<LineParam>()

    /**
     * Transfer Shipment Data
     */
    val transFerShipmentQty: LiveData<Int> = lineParam.switchMap{ param ->
        transferShipmentRepository.getQtyliveData(param.documentNo)
    }

    val transferShipmentAlreadyScan: LiveData<Int> = lineParam.switchMap { param ->
        transferShipmentRepository.getQtyAlreadyScanLiveData(param.documentNo)
    }

    val shipmentLineData: LiveData<List<TransferShipmentLine>> = lineParam.switchMap { param ->
        transferShipmentRepository.getLineListFromHeaderLiveData(
            param.documentNo,
            param.page
        )
    }

    val transferShipmentAccidentalyScan: LiveData<Int?> = lineParam.switchMap {
        transferShipmentRepository.getTransferShipmentAccidentInput(it.documentNo)
    }

    /**
     * Transfer Receipt Data
     */

    val transferReceipt: LiveData<List<TransferShipmentLine>> = lineParam.switchMap { param ->
        transferShipmentRepository.getLineListFromReceiptLiveData(
            param.documentNo,
            param.page
        )
    }

    val transferReceiptQty: LiveData<Int> = lineParam.switchMap { param ->
        transferReceiptRepository.getTransferReceiptQty(param.documentNo)
    }

    val transferReceiptAlreadyScan: LiveData<Int> = lineParam.switchMap {
        transferReceiptRepository.getTransferReceiptAlreadyScan(it.documentNo)
    }

    val transferReceiptAccidentalyScan: LiveData<Int> = lineParam.switchMap {
        transferReceiptRepository.getTransferReceiptAccidentInput(it.documentNo)
    }

    /**
     * Purchase Data
     */

    val purchaseLineLiveData: LiveData<List<PurchaseOrderLine>> =
        lineParam.switchMap { param ->
            purchaseOrderRepository.getPurchaseOrderLineByNo(param.documentNo, param.page)
        }

    val purchaseQty: LiveData<Int> = lineParam.switchMap {
        purchaseOrderRepository.getPurchaseQtyTotal(it.documentNo)
    }

    val purchaseAlreadyScan: LiveData<Int> = lineParam.switchMap {
        purchaseOrderRepository.getPurchaseAlreadyScan(it.documentNo)
    }

    val purchaseOrderAccidentalyScan: LiveData<Int> = lineParam.switchMap {
        purchaseOrderRepository.getPurchaseOrderAccidentInput(it.documentNo)
    }

    /**
     * Inventory Data
     */


    val inventoryLineLiveData: LiveData<List<InventoryPickLine>> =
        lineParam.switchMap { param ->
            inventoryRepository.getAllInventoryPickLine(param.documentNo, param.page)
        }

    val inventoryQty = lineParam.switchMap {
        inventoryRepository.getInventoryQty(it.documentNo)
    }

    val inventoryAlreadyScan = lineParam.switchMap {
        inventoryRepository.getInventoryAlreadyQty(it.documentNo)
    }


    /**
     * Function
     */

    fun setLine(data: LineParam) {
        lineParam.value = data
    }

    fun insertDataValue(
        no: String,
        identifier: String,
        transferType: TransferType,
        binCode: String = "",
        box: String = ""
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
                                .catch { transferLineData = null }
                                .collect { data -> ui { transferLineData = data } }
                            insertTransferInput("1", box, identifier)
                        }

                        RECEIPT -> {
                            transferReceiptRepository.getTransferHeaderDetail(no).collect {
                                transferReceiptHeader = it
                            }
                            transferShipmentRepository.getLineDetailFromBarcode(no, identifier)
                                .catch { transferLineData = null }
                                .collect { data ->
                                    ui { transferLineData = data }
                                }
                            insertTransferReceiptInput("1", box, identifier, binCode)
                        }

                        PURCHASE -> {
                            purchaseOrderRepository.getPurchaseOrderLineByBarcode(no, identifier)
                                .collect { data -> ui { purchaseLineData = data } }
                            insertPurchaseInput("1", box, binCode, identifier, no)
                        }

                        STOCKOPNAME -> {
                            if (BuildConfig.FLAVOR == Constant.APP_WAREHOUSE) {
                                stockOpnameRepository.getStockOpnameDetailByBarcode(
                                    identifier,
                                    binCode
                                )
                                    .collect { data ->
                                        ui {
                                            insertStockOpnameData("1", box, data)
                                        }
                                    }
                            } else {
                                stockOpnameRepository.getStockOpnameDetailByBarcode(identifier)
                                    .collect { data ->
                                        ui {
                                            insertStockOpnameData("1", box, data)
                                        }
                                    }
                            }

                        }

                        INVENTORY -> {
                            inventoryRepository.getInventoryHeaderDetail(no)
                                .collect { inventoryPickHeader = it }
                            inventoryRepository.getDetailInventoryPickLine(no, binCode, identifier)
                                .collect {
                                    inventoryPickLine = it
                                    insertInventoryData(binCode, box)
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

    private fun insertStockOpnameData(qty: String, box: String, data: StockOpnameData) {
        viewModelScope.launch {
            try {
                io {
                    data.let { data ->
                        stockOpnameRepository.insertInputStockOpname(
                            StockOpnameInputData(
                                documentNo = data.documentNo,
                                quantity = qty.toInt(),
                                lineNo = data.lineNo,
                                itemNo = data.itemNo,
                                binCode = data.binCode,
                                userName = sharedPreferences.getUserName(),
                                insertDateTime = "${getCurrentDate()}T${getCurrentTime()}",
                                headerId = data.id!!, box = box,
                                locationCode = data.locationCode
                            )
                        )
                        ui {
                            _transferInputViewState.value =
                                TransferDetailInputViewState.SuccessSaveData
                        }
                    }
                }
            } catch (e: Exception) {

                e.localizedMessage?.let {
                    _transferInputViewState.postValue(TransferDetailInputViewState.ErrorGetData(it))
                }

            }
        }
    }

    private fun insertTransferInput(qty: String, box: String, identifier: String) {
        viewModelScope.launch {
            try {
                io {
                    transferHeaderData?.let { header ->
                        val value = transferShipmentRepository.insertTransferInput(
                            TransferInputData(
                                documentNo = transferLineData?.documentNo ?: header.no,
                                quantity = qty.toInt(),
                                lineNo = transferLineData?.lineNo ?: 0,
                                itemNo = transferLineData?.itemNo ?: "",
                                transferFromBinCode = header.transferFromCode,
                                transferToBinCode = header.transferToCode,
                                userName = sharedPreferences.getUserName(),
                                insertDateTime = "${getCurrentDate()}T${getCurrentTime()}",
                                box = box,
                                itemIdentifier = identifier
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
                            transferLineData = null
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

    private fun insertInventoryData(binCode: String, box: String) {
        viewModelScope.launch {
            try {
                inventoryPickHeader?.let { header ->
                    inventoryPickLine?.let { line ->
                        io {
                            inventoryRepository.insertInputInventory(
                                InventoryInputData(
                                    documentNo = header.no,
                                    lineNo = line.lineNo,
                                    itemNo = line.itemRefNo,
                                    quantity = 1,
                                    binCode = binCode,
                                    locationCode = header.locationCode,
                                    userName = getUserName(),
                                    insertDateTime = "${getCurrentDate()}T${getCurrentTime()}",
                                    box = box
                                ), line.id ?: 0
                            ).collect {
                                ui {
                                    _transferInputViewState.value =
                                        TransferDetailInputViewState.SuccessSaveData
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

    private fun insertTransferReceiptInput(
        qty: String,
        box: String,
        identifier: String,
        binCode: String
    ) {
        viewModelScope.launch {
            try {
                io {
                    transferReceiptHeader?.let { header ->
                        val value =
                            transferReceiptRepository.insertTransferReceiptInput(
                                TransferReceiptInput(
                                    documentNo = transferLineData?.documentNo ?: header.no,
                                    quantity = qty.toInt(),
                                    lineNo = transferLineData?.lineNo ?: 0,
                                    itemNo = transferLineData?.itemNo ?: "",
                                    transferFromBinCode = header.transferFromCode,
                                    transferToBinCode = header.transferToCode,
                                    userName = sharedPreferences.getUserName(),
                                    insertDateTime = "${getCurrentDate()}T${getCurrentTime()}",
                                    box = box,
                                    newBinCode = binCode,
                                    itemIdentifier = identifier
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
                e.localizedMessage?.let {
                    _transferInputViewState.postValue(TransferDetailInputViewState.ErrorGetData(it))
                }
            }
        }
    }

    private fun insertPurchaseInput(
        qty: String,
        box: String,
        binCode: String,
        identifier: String,
        documentNo: String
    ) {
        viewModelScope.launch {
            try {
                io {
                    val value = purchaseOrderRepository.insertPurchaseOrderData(
                        PurchaseInputData(
                            documentNo = purchaseLineData?.documentNo ?: documentNo,
                            quantity = qty.toInt(),
                            lineNo = purchaseLineData?.lineNo ?: 0,
                            itemNo = purchaseLineData?.no ?: "",
                            transferFromBinCode = "",
                            transferToBinCode = "",
                            userName = sharedPreferences.getUserName(),
                            insertDateTime = "${getCurrentDate()}T${getCurrentTime()}",
                            box = box,
                            newBinCode = binCode,
                            itemIdentifier = identifier
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
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    TransferDetailInputViewState.ErrorGetData(e.localizedMessage)
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
                                    PickingDetailPostViewState.GetSuccessfullyPostedData(
                                        dataPosted
                                    )
                            }
                            data.apply {
                                sync_status = true
                            }
                            transferShipmentRepository.updateTransferInput(data)
                        }
                    }
                    ui {
                        _pickingPostViewState.value =
                            PickingDetailPostViewState.AllDataPosted
                    }
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
                                    PickingDetailPostViewState.GetSuccessfullyPostedData(
                                        dataPosted
                                    )
                            }
                            data.apply {
                                sync_status = true
                            }
                            transferReceiptRepository.updateTransferReceiptInput(data)
                        }
                    }
                    ui {
                        _pickingPostViewState.value =
                            PickingDetailPostViewState.AllDataPosted
                    }
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
                                    PickingDetailPostViewState.GetSuccessfullyPostedData(
                                        dataPosted
                                    )
                            }
                            data.postSuccess()
                            purchaseOrderRepository.updatePurchaseInputData(data)
                        }
                    }
                    ui {
                        _pickingPostViewState.value =
                            PickingDetailPostViewState.AllDataPosted
                    }
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
                                    PickingDetailPostViewState.GetSuccessfullyPostedData(
                                        dataPosted
                                    )
                            }
                            data.apply {
                                sync_status = true
                            }
                            inventoryRepository.updateInventoryInput(data)
                        }
                    }
                    ui {
                        _pickingPostViewState.value =
                            PickingDetailPostViewState.AllDataPosted
                    }
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
                            binreclassRepository.getAllUnSyncBinreclassnputByHeaderId(
                                headerId = header.id!!
                            )

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
                    ui {
                        _pickingPostViewState.value =
                            PickingDetailPostViewState.AllDataPosted
                    }
                }
            } catch (e: Exception) {
                _pickingPostViewState.value =
                    PickingDetailPostViewState.ErrorPostData(e.localizedMessage)
            }
        }
    }


    sealed class TransferListViewState {
        class SuccessGetLocalData(val value: TransferShipmentHeader) :
            TransferListViewState()

        class SuccessGetReceiptLocalData(val values: TransferReceiptHeader) :
            TransferListViewState()

        class SuccessGetInventoryData(val value: InventoryPickHeader) :
            TransferListViewState()

        class SuccessGetPickingLineData(val values: MutableList<TransferShipmentLine>) :
            TransferListViewState()

        class ErrorGetLocalData(val message: String) : TransferListViewState()
        class SuccessGetPurchaseData(val value: PurchaseOrderHeader) :
            TransferListViewState()
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
