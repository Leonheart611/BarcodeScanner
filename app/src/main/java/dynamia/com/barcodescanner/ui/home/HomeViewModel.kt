package dynamia.com.barcodescanner.ui.home

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dynamia.com.barcodescanner.ui.home.HomeViewModel.FunctionDialog.LOGOUT
import dynamia.com.barcodescanner.ui.home.HomeViewModel.FunctionDialog.REFRESH
import dynamia.com.barcodescanner.ui.home.HomeViewModel.HomeGetApiViewState.*
import dynamia.com.barcodescanner.ui.stockopname.StockOpnameViewModel
import dynamia.com.barcodescanner.ui.transferstore.transferdetail.TransferDetailViewModel
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.entinty.*
import dynamia.com.core.data.repository.*
import dynamia.com.core.domain.ResultWrapper.*
import dynamia.com.core.util.Event
import dynamia.com.core.util.io
import dynamia.com.core.util.ui
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel(
    val transferShipmentRepository: TransferShipmentRepository,
    val transferReceiptRepository: TransferReceiptRepository,
    val purchaseOrderRepository: PurchaseOrderRepository,
    val stockOpnameDataRepository: StockOpnameRepository,
    val binreclassRepository: BinreclassRepository,
    private val sharedPreferences: SharedPreferences,
) : ViewModelBase(sharedPreferences) {

    private var _homeViewState = MutableLiveData<Event<HomeViewState>>()
    val homeViewState: LiveData<Event<HomeViewState>> by lazy { _homeViewState }

    private var _homeGetApiViewState = MutableLiveData<HomeGetApiViewState>()
    val homeGetApiViewState: LiveData<HomeGetApiViewState> by lazy { _homeGetApiViewState }

    private var _homePostViewState = MutableLiveData<HomePostViewState>()
    val homePostViewState: LiveData<HomePostViewState> by lazy { _homePostViewState }

    private fun clearAllDB() {
        viewModelScope.launch {
            try {
                io {
                    transferShipmentRepository.deleteAllTransferHeader()
                    transferShipmentRepository.deleteAllTransferLine()
                    transferShipmentRepository.deleteAllTransferInput()
                    transferReceiptRepository.deleteAllTransferReceiptHeader()
                    transferReceiptRepository.clearAllInputData()
                }
            } catch (e: Exception) {
                _homeViewState.value = Event(HomeViewState.Error(e.localizedMessage))
                Log.e("clearAllDb", e.localizedMessage)
            }
        }
    }

    fun logOutSharedPreferences() {
        clearAllDB()
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        _homeViewState.value = Event(HomeViewState.HasSuccessLogout)
    }

    fun getTransferData() {
        viewModelScope.launch {
            try {
                io {
                    transferShipmentRepository.deleteAllTransferHeader()
                    transferShipmentRepository.deleteAllTransferLine()
                    transferShipmentRepository.deleteAllTransferInput()

                    transferShipmentRepository.getTransferShipmentHeaderAsync()
                        .collect { dataHeader ->
                            when (dataHeader) {
                                is Success -> {
                                    dataHeader.value.forEach {
                                        transferShipmentRepository.insertTransferHeader(it)
                                    }
                                }
                                is GenericError -> {
                                    ui {
                                        _homeGetApiViewState.value =
                                            FailedGetShippingData("${dataHeader.code} ${dataHeader.error}")
                                    }
                                }
                                is NetworkError -> {
                                    _homeGetApiViewState.postValue(FailedGetShippingData("Error Network"))

                                }
                            }
                        }
                    transferShipmentRepository.getTransferShipmentLineAsync().collect { data ->
                        when (data) {
                            is Success -> {
                                data.value.forEach {
                                    transferShipmentRepository.insertTransferLine(it)
                                }
                            }
                            is GenericError -> {
                                ui {
                                    _homeGetApiViewState.value =
                                        FailedGetShippingData("${data.code} ${data.error}")
                                }
                            }
                            is NetworkError -> {
                                _homeGetApiViewState.postValue(FailedGetShippingData(data.error))
                            }
                        }
                    }
                }
                ui { _homeGetApiViewState.value = SuccessGetShipingData }
            } catch (e: Exception) {
                _homeGetApiViewState.value = FailedGetShippingData(e.localizedMessage)
            }
        }
    }

    fun checkEmptyData() {
        viewModelScope.launch {
            try {
                io {
                    transferShipmentRepository.getCheckEmptyOrNot().collect {
                        ui { _homeViewState.value = Event(HomeViewState.DBhasEmpty(it)) }
                    }
                }
            } catch (e: Exception) {
                _homeViewState.value = Event(HomeViewState.Error(e.localizedMessage))
            }
        }
    }

    fun getPurchaseDataAsync() {
        viewModelScope.launch {
            try {
                io {
                    with(purchaseOrderRepository) {
                        deleteAllPurchaseOrderLine()
                        deleteAllPurchaseOrderHeader()
                        deleteAllPurchaseInputData()

                        getPurchaseOrderHeaderAsync().collect { value ->
                            when (value) {
                                is GenericError -> ui {
                                    _homeGetApiViewState.value =
                                        FailedGetPurchase("${value.code} ${value.error}")
                                }
                                is NetworkError -> _homeGetApiViewState.postValue(FailedGetPurchase(
                                    value.error))
                                is Success -> {
                                    value.value.forEach {
                                        insertPurchaseOrderHeader(it)
                                    }
                                }
                            }
                        }
                        getPurchaseOrderLineAsync().collect { value ->
                            when (value) {
                                is GenericError -> _homeGetApiViewState.postValue(FailedGetPurchase(
                                    "${value.code} ${value.error}"))
                                is NetworkError -> _homeGetApiViewState.postValue(FailedGetPurchase(
                                    value.error))
                                is Success -> {
                                    value.value.forEach {
                                        insertPurchaseOrderLine(it)
                                    }
                                    ui { _homeGetApiViewState.value = SuccessGetPurchaseData }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _homeGetApiViewState.value =
                    FailedGetPurchase(e.localizedMessage)
            }
        }
    }

    fun getReceiptDataAsync() {
        viewModelScope.launch {
            try {
                io {
                    transferReceiptRepository.deleteAllTransferReceiptHeader()
                    transferReceiptRepository.clearAllInputData()

                    transferReceiptRepository.getTransferReceiptHeaderAsync().collect { value ->
                        when (value) {
                            is GenericError -> ui {
                                _homeGetApiViewState.value =
                                    FailedGetReceipt("${value.code} ${value.error}")
                            }
                            is NetworkError -> _homeGetApiViewState.postValue(FailedGetReceipt(
                                value.error))
                            is Success -> {
                                value.value.forEach {
                                    transferReceiptRepository.insertTransferReceiptHeader(it)
                                }
                                ui { _homeGetApiViewState.value = SuccessGetReceipt }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _homeGetApiViewState.value =
                    FailedGetReceipt(e.localizedMessage)
            }
        }
    }

    fun getStockOpname() {
        viewModelScope.launch {
            try {
                io {
                    with(stockOpnameDataRepository) {
                        deleteAllInputStockOpname()
                        deleteAllStockOpname()

                        getStockOpnameAsync().collect { value ->
                            when (value) {
                                is GenericError -> ui {
                                    _homeGetApiViewState.value =
                                        FailedGetStockOpname("${value.code} ${value.error}")
                                }
                                is NetworkError -> _homeGetApiViewState.postValue(
                                    FailedGetStockOpname(value.error))
                                is Success -> {
                                    value.value.forEach {
                                        insertStockOpnameData(it)
                                    }
                                    ui { _homeGetApiViewState.value = SuccessGetStockOpname }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _homeGetApiViewState.value =
                    FailedGetStockOpname(e.localizedMessage)
            }
        }
    }

    fun checkUnpostedData(param: FunctionDialog) {
        viewModelScope.launch {
            io {
                val listEntries = transferShipmentRepository.getAllUnsycnTransferInput()
                val receiptEntris = transferReceiptRepository.getAllUnsycnTransferReceiptInput()
                val purchaseEntries = purchaseOrderRepository.getAllUnSyncPurchaseInput()
                val rebinClassEntry = binreclassRepository.getAllUnSyncBinreclassnput()
                val stockOpnameEntry = stockOpnameDataRepository.getAllUnsyncStockInput()
                val total =
                    listEntries.size + receiptEntris.size + purchaseEntries.size + rebinClassEntry.size + stockOpnameEntry.size
                ui {
                    when (param) {
                        REFRESH -> _homeViewState.value =
                            Event(HomeViewState.GetUnpostedDataRefresh(total.toString()))
                        LOGOUT -> _homeViewState.value =
                            Event(HomeViewState.GetUnpostedDataLogout(total.toString()))
                    }
                }
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
                        _homePostViewState.value =
                            HomePostViewState.GetUnpostedTransferShipment(listEntries.size)
                        _homePostViewState.value =
                            HomePostViewState.GetSuccessTransferShipment(dataPosted)
                    }
                    for (data in listEntries) {
                        val param = gson.toJson(data)
                        transferShipmentRepository.postTransferData(param).collect {
                            dataPosted++
                            ui {
                                _homePostViewState.value =
                                    HomePostViewState.GetSuccessTransferShipment(dataPosted)
                            }
                            data.apply {
                                sync_status = true
                            }
                            transferShipmentRepository.updateTransferInput(data)
                        }
                    }
                    ui {
                        _homePostViewState.value =
                            HomePostViewState.AllDataPostedTransferShipment
                    }
                }
            } catch (e: Exception) {
                _homePostViewState.value =
                    HomePostViewState.ErrorPostTransferShipment(e.localizedMessage)
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
                        _homePostViewState.value =
                            HomePostViewState.GetUnpostedTransferReceipt(listEntries.size)
                        _homePostViewState.value =
                            HomePostViewState.GetSuccessfulTransferReceipt(dataPosted)
                    }
                    for (data in listEntries) {
                        val param = gson.toJson(data)
                        transferReceiptRepository.postTransferReceiptInput(param).collect {
                            dataPosted++
                            ui {
                                _homePostViewState.value =
                                    HomePostViewState.GetSuccessfulTransferReceipt(dataPosted)
                            }
                            data.apply {
                                sync_status = true
                            }
                            transferReceiptRepository.updateTransferReceiptInput(data)
                        }
                    }
                    ui {
                        _homePostViewState.value =
                            HomePostViewState.SuccessPostallTransferReceipt
                    }
                }
            } catch (e: Exception) {
                _homePostViewState.value =
                    HomePostViewState.ErrorPostTransferReceipt(e.localizedMessage)
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
                        _homePostViewState.value =
                            HomePostViewState.GetUnpostedPurchase(listEntries.size)
                        _homePostViewState.value =
                            HomePostViewState.GetSuccessfulPurchase(dataPosted)
                    }
                    for (data in listEntries) {
                        val param = gson.toJson(data)
                        purchaseOrderRepository.postPurchaseOrderData(param).collect {
                            dataPosted++
                            ui {
                                _homePostViewState.value =
                                    HomePostViewState.GetSuccessfulPurchase(dataPosted)
                            }
                            data.postSuccess()
                            purchaseOrderRepository.updatePurchaseInputData(data)
                        }
                    }
                    ui {
                        _homePostViewState.value = HomePostViewState.SuccessPostallPurchase
                    }
                }
            } catch (e: Exception) {
                _homePostViewState.value = HomePostViewState.ErrorPostPurchase(e.localizedMessage)
            }
        }
    }

    fun postStockOpnameData() {
        viewModelScope.launch {
            try {
                var dataPosted = 0
                io {
                    val listEntries = stockOpnameDataRepository.getAllUnsyncStockInput()
                    ui {
                        _homePostViewState.value =
                            HomePostViewState.GetUnpostedStock(listEntries.size)
                        _homePostViewState.value =
                            HomePostViewState.GetSuccessfulStock(dataPosted)
                    }
                    for (data in listEntries) {
                        val param = gson.toJson(data)
                        stockOpnameDataRepository.postStockOpnameData(param).collect {
                            dataPosted++
                            ui {
                                _homePostViewState.value =
                                    HomePostViewState.GetSuccessfulStock(dataPosted)
                            }
                            data.postSuccess()
                            stockOpnameDataRepository.updateInputStockOpname(data)
                        }
                    }
                    ui { _homePostViewState.value = HomePostViewState.SuccessPostallStock }
                }
            } catch (e: Exception) {
                _homePostViewState.value = HomePostViewState.ErrorPostStock(e.localizedMessage)
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
                        _homePostViewState.value =
                            HomePostViewState.GetUnpostedBinReclass(listEntries.size)
                        _homePostViewState.value =
                            HomePostViewState.GetSuccessfulBinReclass(dataPosted)
                    }
                    for (header in headerEntries) {
                        val listUnpostedData =
                            binreclassRepository.getAllUnSyncBinreclassnputByHeaderId(headerId = header.id!!)

                        for (data in listUnpostedData) {
                            val param = gson.toJson(data)
                            binreclassRepository.postDataBinreclass(param).collect {
                                dataPosted++
                                ui {
                                    _homePostViewState.value =
                                        HomePostViewState.GetSuccessfulBinReclass(dataPosted)
                                }
                                data.postSuccess()
                                binreclassRepository.updateAllBinReclassBin(data)
                            }
                        }
                        header.allDataPosted()
                        binreclassRepository.updateBinReclassHeader(header)
                    }
                    ui { _homePostViewState.value = HomePostViewState.SuccessPostallBinReclass }
                }
            } catch (e: Exception) {
                _homePostViewState.value = HomePostViewState.ErrorPostBinReclass(e.localizedMessage)
            }
        }
    }


    sealed class HomeViewState {
        class Error(val message: String) : HomeViewState()
        class ShowLoading(val boolean: Boolean) : HomeViewState()
        class DBhasEmpty(val value: Int) : HomeViewState()
        object HasSuccessLogout : HomeViewState()
        class GetUnpostedDataRefresh(val unpostedCount: String) : HomeViewState()
        class GetUnpostedDataLogout(val unpostedCount: String) : HomeViewState()
    }

    sealed class HomeGetApiViewState {
        object SuccessGetShipingData : HomeGetApiViewState()
        class FailedGetShippingData(val message: String) : HomeGetApiViewState()
        object SuccessGetReceipt : HomeGetApiViewState()
        class FailedGetReceipt(val message: String) : HomeGetApiViewState()
        object SuccessGetPurchaseData : HomeGetApiViewState()
        class FailedGetPurchase(val message: String) : HomeGetApiViewState()
        object SuccessGetStockOpname : HomeGetApiViewState()
        class FailedGetStockOpname(val message: String) : HomeGetApiViewState()
    }

    sealed class HomePostViewState {
        /**
         * Transfer Shipment Post
         */
        class GetUnpostedTransferShipment(val data: Int) : HomePostViewState()
        class GetSuccessTransferShipment(val data: Int) : HomePostViewState()
        class ErrorPostTransferShipment(val message: String) : HomePostViewState()
        object AllDataPostedTransferShipment : HomePostViewState()

        /**
         * Transfer Receipt Post
         */
        class GetUnpostedTransferReceipt(val data: Int) : HomePostViewState()
        class GetSuccessfulTransferReceipt(val data: Int) : HomePostViewState()
        class ErrorPostTransferReceipt(val message: String) : HomePostViewState()
        object SuccessPostallTransferReceipt : HomePostViewState()

        /**
         * Purchase Order Post
         */
        class GetUnpostedPurchase(val data: Int) : HomePostViewState()
        class GetSuccessfulPurchase(val data: Int) : HomePostViewState()
        class ErrorPostPurchase(val message: String) : HomePostViewState()
        object SuccessPostallPurchase : HomePostViewState()

        /**
         * Stock Opname Post
         */
        class GetUnpostedStock(val data: Int) : HomePostViewState()
        class GetSuccessfulStock(val data: Int) : HomePostViewState()
        class ErrorPostStock(val message: String) : HomePostViewState()
        object SuccessPostallStock : HomePostViewState()

        /**
         * Bin Reclass Post
         */

        class GetUnpostedBinReclass(val data: Int) : HomePostViewState()
        class GetSuccessfulBinReclass(val data: Int) : HomePostViewState()
        class ErrorPostBinReclass(val message: String) : HomePostViewState()
        object SuccessPostallBinReclass : HomePostViewState()

    }


    /**
     * For Get Data From Assets
     */

    fun saveAssetData(
        transferShipmentHeader: TransferShipmentHeaderAsset,
        transferShipmentLine: TransferShipmentLineAsset,
        transferReceiptHeader: TransferReceiptHeaderAssets,
        purchaseOrderHeaderAssets: PurchaseOrderHeaderAssets,
        purchaseOrderLineAsset: PurchaseOrderLineAsset,
        stockOpnameDataAssets: StockOpnameDataAssets,
    ) {
        try {

            viewModelScope.launch {
                io {
                    transferShipmentRepository.deleteAllTransferHeader()
                    transferShipmentRepository.deleteAllTransferLine()
                    transferShipmentRepository.deleteAllTransferInput()
                    transferReceiptRepository.deleteAllTransferReceiptHeader()
                    transferReceiptRepository.clearAllInputData()
                    with(purchaseOrderRepository) {
                        deleteAllPurchaseInputData()
                        deleteAllPurchaseOrderHeader()
                        deleteAllPurchaseOrderLine()
                    }

                    transferShipmentHeader.value?.let {
                        it.forEach { data ->
                            transferShipmentRepository.insertTransferHeader(data)
                        }
                    }
                    transferReceiptHeader.value?.let {
                        it.forEach { transferReceiptHeader ->
                            transferReceiptRepository.insertTransferReceiptHeader(
                                transferReceiptHeader)
                        }
                    }
                    transferShipmentLine.value?.let {
                        it.forEach { data ->
                            transferShipmentRepository.insertTransferLine(data)
                        }
                    }
                    purchaseOrderHeaderAssets.value?.let {
                        it.forEach { data ->
                            purchaseOrderRepository.insertPurchaseOrderHeader(data)
                        }
                    }
                    purchaseOrderLineAsset.value?.let {
                        it.forEach { data ->
                            purchaseOrderRepository.insertPurchaseOrderLine(data)
                        }
                    }
                    stockOpnameDataAssets.value.let {
                        it.forEach { data ->
                            stockOpnameDataRepository.insertStockOpnameData(data)
                        }
                    }
                    ui {
                        _homeGetApiViewState.value = SuccessGetStockOpname
                        _homeGetApiViewState.value = SuccessGetShipingData
                        _homeGetApiViewState.value = SuccessGetReceipt
                        _homeGetApiViewState.value = SuccessGetPurchaseData
                    }
                }
            }
        } catch (e: Exception) {

        }
    }

    enum class FunctionDialog {
        REFRESH, LOGOUT
    }
}

