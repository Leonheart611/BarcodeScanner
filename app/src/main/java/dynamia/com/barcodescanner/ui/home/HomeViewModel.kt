package dynamia.com.barcodescanner.ui.home

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.home.HomeViewModel.HomeGetApiViewState.*
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.model.UserData
import dynamia.com.core.data.repository.*
import dynamia.com.core.domain.RetrofitBuilder
import dynamia.com.core.util.Constant
import dynamia.com.core.util.Event
import dynamia.com.core.util.io
import dynamia.com.core.util.ui
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel(
    val pickingListRepository: PickingListRepository,
    val receiptImportRepository: ReceiptImportRepository,
    val receiptLocalRepository: ReceiptLocalRepository,
    val stockCountRepository: StockCountRepository,
    private val sharedPreferences: SharedPreferences,
    private val networkRepository: NetworkRepository,
    val app: Application
) : ViewModelBase(sharedPreferences) {

    private val newUserData by lazy { getUserData() }

    private val retrofitService by lazy {
        RetrofitBuilder.getClient(
            serverAddress = newUserData.hostName,
            password = newUserData.password,
            username = newUserData.username
        )
    }

    val getAllDaataMessage = MutableLiveData<Event<String>>()
    val loading = MutableLiveData<Event<Boolean>>()

    private var _homeViewState = MutableLiveData<HomeViewState>()
    val homeViewState: LiveData<HomeViewState> by lazy { _homeViewState }

    private var _homeGetApiViewState = MutableLiveData<HomeGetApiViewState>()
    val homeGetApiViewState: LiveData<HomeGetApiViewState> by lazy { _homeGetApiViewState }

    private var _homePostViewState = MutableLiveData<HomePostViewState>()
    val homePostViewState: LiveData<HomePostViewState> by lazy { _homePostViewState }

    fun checkDBNotNull() {
        viewModelScope.launch {
            var result: Boolean? = null
            try {
                io {
                    result = pickingListRepository.getCheckEmptyOrNot(getEmployeeName())
                }
                result?.let { ui { _homeViewState.value = HomeViewState.DBhasEmpty(it) } }
            } catch (e: Exception) {
                _homeViewState.value = HomeViewState.Error(e.localizedMessage)
            }
        }
    }

    fun clearAllDB() {
        viewModelScope.launch {
            try {
                io {
                    pickingListRepository.clearPickingListHeader()
                    pickingListRepository.clearPickingListLine()
                    pickingListRepository.clearPickingListScanEntries()

                    receiptImportRepository.clearReceiptImportHeader()
                    receiptImportRepository.clearReceiptImportLine()
                    receiptImportRepository.clearReceiptImportScanEntries()

                    receiptLocalRepository.clearReceiptLocalHeader()
                    receiptLocalRepository.clearReceiptLocalLine()
                    receiptLocalRepository.clearReceiptLocalScanEntries()

                    stockCountRepository.clearStockCount()
                }
            } catch (e: Exception) {
                _homeViewState.value = HomeViewState.Error(e.localizedMessage)
                Log.e("clearAllDb", e.localizedMessage)
            }

        }

    }

    fun logOutSharedPreferences() {
        clearAllDB()
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        _homeViewState.value = HomeViewState.HasSuccessLogout
    }

    val pickingPostMessage = MutableLiveData<Event<String>>()
    fun postPickingData() {
        uiScope.launch {
            loading.postValue(Event(true))
            try {
                val pickingListScanEntries =
                    pickingListRepository.getAllUnscynPickingListScanEntries()
                if (pickingListScanEntries.isNotEmpty()) {
                    for (data in pickingListScanEntries) {
                        val param = gson.toJson(data)
                        val result = retrofitService.postPickingListEntry(param)
                        result.let {
                            data.apply {
                                sycn_status = true
                            }
                            pickingListRepository.updatePickingScanEntry(data)
                        }
                    }
                    pickingPostMessage.postValue(Event("Success Post Data PickingList"))
                    loading.postValue(Event(false))

                } else {
                    pickingPostMessage.postValue(
                        Event(
                            app.resources.getString(
                                R.string.post_all_data_or_no_data,
                                Constant.PICKING_LIST
                            )
                        )
                    )
                    loading.postValue(Event(false))
                }
            } catch (e: java.lang.Exception) {
                loading.postValue(Event(false))
                pickingPostMessage.postValue(Event(e.localizedMessage))
            }
        }
    }

    val postImportMessage = MutableLiveData<Event<String>>()
    val postLocalMessage = MutableLiveData<Event<String>>()
    fun postReceiptImportData() {
        uiScope.launch {
            try {
                loading.postValue(Event(true))
                val receiptImportData = receiptImportRepository.getAllUnsycnImportScanEntry()
                if (receiptImportData.isNotEmpty()) {
                    for (data in receiptImportData) {
                        if (data.sycn_status.not()) {
                            val param = gson.toJson(data)
                            val result = retrofitService.postReceiptImportEntry(param)
                            result.let {
                                data.apply {
                                    sycn_status = true
                                }
                                receiptImportRepository.updateReceiptImportScanEntry(data)
                            }
                        }
                    }
                    loading.postValue(Event(false))
                    postImportMessage.postValue(Event("Berhasil Post Data Receipt Import"))
                } else {
                    loading.postValue(Event(false))
                    postImportMessage.postValue(
                        Event(
                            app.resources.getString(
                                R.string.post_all_data_or_no_data,
                                Constant.RECEIPT_IMPORT
                            )
                        )
                    )
                }
            } catch (e: Exception) {
                loading.postValue(Event(false))
                postImportMessage.postValue(Event(e.localizedMessage))
            }
        }
    }


    fun postReceiptLocalData() {
        uiScope.launch {
            try {
                loading.postValue(Event(true))
                val receiptLocalData = receiptLocalRepository.getUnsycnReceiptLocalScanEntry()
                if (receiptLocalData.isNotEmpty()) {
                    for (data in receiptLocalData) {
                        if (data.sycn_status.not()) {
                            val param = gson.toJson(data)
                            val result = retrofitService.postReceiptLocalEntry(param)
                            result.let {
                                data.apply {
                                    sycn_status = true
                                }
                                receiptLocalRepository.updateReceiptLocalScanEntry(data)
                            }
                        }
                    }
                    loading.postValue(Event(false))
                    postLocalMessage.postValue(Event("Berhasil Post Data Receipt Local"))
                } else {
                    loading.postValue(Event(false))
                    postLocalMessage.postValue(
                        Event(
                            app.resources.getString(
                                R.string.post_all_data_or_no_data,
                                Constant.RECEIPT_LOCAL
                            )
                        )
                    )
                }

            } catch (e: Exception) {
                loading.postValue(Event(false))
                postLocalMessage.postValue(Event(e.localizedMessage))
            }
        }
    }

    val postStockCountMessage = MutableLiveData<Event<String>>()
    fun postStockCountData() {
        uiScope.launch {
            loading.postValue(Event(true))
            try {
                val stockCounts = stockCountRepository.getAllUnsycnStockCount()
                if (stockCounts.isNotEmpty()) {
                    for (data in stockCounts) {
                        val param = gson.toJson(data)
                        val result = retrofitService.postStockCountEntry(param)
                        result.let {
                            data.apply {
                                sycn_status = true
                            }
                            stockCountRepository.updateStockCount(data)
                        }
                    }
                    postStockCountMessage.postValue(Event("Berhasil Post Data StockCount"))
                    loading.postValue(Event(false))

                } else {
                    postStockCountMessage.postValue(
                        Event(
                            app.resources.getString(
                                R.string.post_all_data_or_no_data,
                                Constant.STOCK_COUNT
                            )
                        )
                    )
                    loading.postValue(Event(false))
                }
            } catch (e: Exception) {
                loading.postValue(Event(false))
                postStockCountMessage.postValue(Event(e.localizedMessage))
            }
        }
    }

    private fun getUserData(): UserData {
        return UserData(
            1,
            hostName = sharedPreferences.getString(Constant.HOST_DOMAIN_KEY, "") ?: "",
            username = sharedPreferences.getString(Constant.USERNAME_KEY, "") ?: "",
            password = sharedPreferences.getString(Constant.PASSWORD_KEY, "") ?: "",
            employeeCode = sharedPreferences.getString(Constant.EMPLOYEE_KEY, "") ?: ""
        )
    }

    fun getPickingListApi() {
        viewModelScope.launch {
            try {
                io {
                    networkRepository.getPickingListHeaderAsync().collect { dataHeader ->
                        dataHeader.forEach {
                            pickingListRepository.insertPickingListHeader(it)
                        }
                    }
                    networkRepository.getPickingListLineAsync().collect { data ->
                        data.forEach {
                            pickingListRepository.insertPickingListLine(it)
                        }
                    }
                }
                ui { _homeGetApiViewState.value = SuccessGetPickingList }
            } catch (e: Exception) {
                _homeGetApiViewState.value = FailedGetPickingList(e.localizedMessage)
            }
        }
    }

    fun getReceiptImportAPI() {
        viewModelScope.launch {
            try {
                io {
                    networkRepository.getReceiptImportHeaderAsync().collect { dataHeader ->
                        dataHeader.forEach {
                            receiptImportRepository.insertReceiptImportHeader(it)
                        }
                    }
                    networkRepository.getReceiptImportLineAsync().collect { data ->
                        data.forEach {
                            receiptImportRepository.insertReceiptImportLine(it)
                        }
                    }
                }
                ui { _homeGetApiViewState.value = SuccessGetReceiptImport }
            } catch (e: Exception) {
                _homeGetApiViewState.value = FailedGetReceiptImport(e.localizedMessage)
            }
        }
    }

    fun getReceiptLocalApi() {
        viewModelScope.launch {
            try {
                io {
                    networkRepository.getReceiptLocalHeaderAsync().collect { dataHeader ->
                        dataHeader.forEach {
                            receiptLocalRepository.insertReceiptLocalHeader(it)
                        }
                    }
                    networkRepository.getReceiptLocalLineAsync().collect { data ->
                        data.forEach {
                            receiptLocalRepository.insertReceiptLocalLine(it)
                        }
                    }
                }
                ui { _homeGetApiViewState.value = SuccessGetReceiptLocal }
            } catch (e: Exception) {
                _homeGetApiViewState.value = FailedGetReceiptLocal(e.localizedMessage)
            }
        }
    }

    fun postPickingDataNew() {
        viewModelScope.launch {
            try {
                var dataPosted = 0
                io {
                    val pickingListEntries =
                        pickingListRepository.getAllUnscynPickingListScanEntries()
                    ui {
                        _homePostViewState.value =
                            HomePostViewState.GetUnpostedPicking(
                                pickingListEntries.size
                            )
                        _homePostViewState.value =
                            HomePostViewState.GetSuccessfullyPicking(
                                dataPosted
                            )
                    }
                    for (data in pickingListEntries) {
                        val param = gson.toJson(data)
                        networkRepository.postPickingListEntry(param).collect {
                            dataPosted++
                            ui {
                                _homePostViewState.value =
                                    HomePostViewState.GetSuccessfullyPicking(dataPosted)
                            }
                            data.apply {
                                sycn_status = true
                            }
                            pickingListRepository.updatePickingScanEntry(data)
                        }
                    }
                    ui { _homePostViewState.value = HomePostViewState.AllDataPostedPicking }
                }
            } catch (e: Exception) {
                _homePostViewState.value =
                    HomePostViewState.ErrorPostPicking(e.localizedMessage)
            }
        }
    }

    fun postReceiptLocalNew() {
        viewModelScope.launch {
            try {
                var dataPosted = 0
                io {
                    val receiptLocalList = receiptLocalRepository.getUnsycnReceiptLocalScanEntry()
                    ui {
                        _homePostViewState.value =
                            HomePostViewState.GetUnpostedLocal(receiptLocalList.size)
                        _homePostViewState.value =
                            HomePostViewState.GetSuccessfulLocal(dataPosted)
                    }
                    for (data in receiptLocalList) {
                        val param = gson.toJson(data)
                        networkRepository.postReceiptLocalEntry(param).collect {
                            dataPosted++
                            ui {
                                _homePostViewState.value =
                                    HomePostViewState.GetSuccessfulLocal(dataPosted)
                            }
                            data.apply {
                                sycn_status = true
                            }
                            receiptLocalRepository.updateReceiptLocalScanEntry(data)
                        }
                    }
                }
                ui { _homePostViewState.value = HomePostViewState.SuccessPostallLocal }
            } catch (e: Exception) {
                _homePostViewState.value =
                    HomePostViewState.ErrorPostLocal(e.localizedMessage)
            }
        }
    }

    fun postReceiptImportNew() {
        viewModelScope.launch {
            try {
                var dataPosted = 0
                io {
                    val receiptImportList = receiptImportRepository.getAllUnsycnImportScanEntry()
                    ui {
                        _homePostViewState.value =
                            HomePostViewState.GetUnpostedImport(
                                receiptImportList.size
                            )
                        _homePostViewState.value =
                            HomePostViewState.GetSuccessfulImport(
                                dataPosted
                            )
                    }
                    for (data in receiptImportList) {
                        val param = gson.toJson(data)
                        networkRepository.postReceiptImportEntry(param).collect {
                            dataPosted++
                            ui {
                                _homePostViewState.value =
                                    HomePostViewState.GetSuccessfulImport(
                                        dataPosted
                                    )
                            }
                            data.apply {
                                sycn_status = true
                            }
                            receiptImportRepository.updateReceiptImportScanEntry(data)
                        }
                    }
                }
                ui { _homePostViewState.value = HomePostViewState.SuccessPostallImport }
            } catch (e: Exception) {
                _homePostViewState.value =
                    HomePostViewState.ErrorPostImport(e.localizedMessage)
            }
        }
    }

    fun postStockCountDataNew() {
        viewModelScope.launch {
            try {
                var dataPosted = 0
                io {
                    val stockCounts = stockCountRepository.getAllUnsycnStockCount()
                    ui {
                        _homePostViewState.value =
                            HomePostViewState.GetUnpostedCount(stockCounts.size)
                        _homePostViewState.value =
                            HomePostViewState.GetSuccessfulCount(dataPosted)
                    }
                    for (data in stockCounts) {
                        val body = gson.toJson(data)
                        networkRepository.postStockCountEntry(body).collect {
                            dataPosted++
                            ui {
                                _homePostViewState.value =
                                    HomePostViewState.GetSuccessfulCount(dataPosted)
                            }
                            data.apply {
                                sycn_status = true
                            }
                            stockCountRepository.updateStockCount(data)
                        }
                    }
                }
                ui { _homePostViewState.value = HomePostViewState.SuccessPostallCount }
            } catch (e: Exception) {
                _homePostViewState.value =
                    HomePostViewState.ErrorPostCount(e.localizedMessage)
            }
        }
    }


    sealed class HomeViewState {
        class Success(val message: String) : HomeViewState()
        class Error(val message: String) : HomeViewState()
        class ShowLoading(val boolean: Boolean) : HomeViewState()
        class DBhasEmpty(val boolean: Boolean) : HomeViewState()
        object HasSuccessLogout : HomeViewState()
    }

    sealed class HomeGetApiViewState {
        object SuccessGetPickingList : HomeGetApiViewState()
        class FailedGetPickingList(val message: String) : HomeGetApiViewState()
        object SuccessGetReceiptImport : HomeGetApiViewState()
        class FailedGetReceiptImport(val message: String) : HomeGetApiViewState()
        object SuccessGetReceiptLocal : HomeGetApiViewState()
        class FailedGetReceiptLocal(val message: String) : HomeGetApiViewState()
    }

    sealed class HomePostViewState {
        class GetUnpostedPicking(val data: Int) : HomePostViewState()
        class GetSuccessfullyPicking(val data: Int) : HomePostViewState()
        class ErrorPostPicking(val message: String) : HomePostViewState()
        object AllDataPostedPicking : HomePostViewState()

        class GetUnpostedLocal(val data: Int) : HomePostViewState()
        class GetSuccessfulLocal(val data: Int) : HomePostViewState()
        class ErrorPostLocal(val message: String) : HomePostViewState()
        object SuccessPostallLocal : HomePostViewState()

        class GetUnpostedImport(val data: Int) : HomePostViewState()
        class GetSuccessfulImport(val data: Int) : HomePostViewState()
        class ErrorPostImport(val message: String) : HomePostViewState()
        object SuccessPostallImport : HomePostViewState()

        class GetUnpostedCount(val data: Int) : HomePostViewState()
        class GetSuccessfulCount(val data: Int) : HomePostViewState()
        class ErrorPostCount(val message: String) : HomePostViewState()
        object SuccessPostallCount : HomePostViewState()
    }
}

