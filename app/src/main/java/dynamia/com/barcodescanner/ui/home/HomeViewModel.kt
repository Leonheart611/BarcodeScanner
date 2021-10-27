package dynamia.com.barcodescanner.ui.home

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dynamia.com.barcodescanner.ui.home.HomeViewModel.HomeGetApiViewState.*
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.model.*
import dynamia.com.core.data.repository.*
import dynamia.com.core.domain.ResultWrapper.*
import dynamia.com.core.util.Constant
import dynamia.com.core.util.io
import dynamia.com.core.util.ui
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel(
    val pickingListRepository: PickingListRepository,
    val receiptImportRepository: ReceiptImportRepository,
    val receiptLocalRepository: ReceiptLocalRepository,
    private val stockCountRepository: StockCountRepository,
    private val sharedPreferences: SharedPreferences,
    private val networkRepository: NetworkRepository,
    val app: Application
) : ViewModelBase(sharedPreferences) {

    private var _homeViewState = MutableLiveData<HomeViewState>()
    val homeViewState: LiveData<HomeViewState> by lazy { _homeViewState }

    private var _homeGetApiViewState = MutableLiveData<HomeGetApiViewState>()
    val homeGetApiViewState: LiveData<HomeGetApiViewState> by lazy { _homeGetApiViewState }

    private var _homePostViewState = MutableLiveData<HomePostViewState>()
    val homePostViewState: LiveData<HomePostViewState> by lazy { _homePostViewState }

    fun checkDBNotNull() {
        viewModelScope.launch {
            try {
                io {
                    pickingListRepository.getCheckEmptyOrNot().collect { data ->
                        ui { _homeViewState.value = HomeViewState.DBhasEmpty(data) }
                    }
                }
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
                        when (dataHeader) {
                            is Success -> dataHeader.value.forEach {
                                pickingListRepository.insertPickingListHeader(it)
                            }
                            is GenericError -> {
                                ui {
                                    _homeGetApiViewState.value =
                                        FailedGetPickingList("${dataHeader.code} ${dataHeader.error}")
                                }
                            }
                            NetworkError -> {

                            }
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

    fun savePickingHeader(
        pickingListHeader: PickingListHeader,
        pickingListLine: PickingListLine,
        receiptImportHeader: ReceiptImportHeader,
        receiptImportLine: ReceiptImportLine,
        receiptLocalHeader: ReceiptLocalHeader,
        receiptLocalLine: ReceiptLocalLine
    ) {
        viewModelScope.launch {
            io {
                pickingListHeader.value?.let {
                    it.forEach {
                        pickingListRepository.insertPickingListHeader(it)
                    }
                }

                pickingListLine.value.let {
                    it.forEach { data ->
                        pickingListRepository.insertPickingListLine(data)
                    }
                    ui { _homeGetApiViewState.value = SuccessGetPickingList }
                }

                receiptImportHeader.value.let {
                    it.forEach { data ->
                        receiptImportRepository.insertReceiptImportHeader(data)
                    }
                }
                receiptImportLine.value.let {
                    it.forEach { data ->
                        receiptImportRepository.insertReceiptImportLine(data)
                    }
                    ui { _homeGetApiViewState.value = SuccessGetReceiptImport }
                }

                receiptLocalHeader.value.let {
                    it.forEach { data ->
                        receiptLocalRepository.insertReceiptLocalHeader(data)
                    }
                }
                receiptLocalLine.value.let {
                    it.forEach { data ->
                        receiptLocalRepository.insertReceiptLocalLine(data)
                    }
                    ui { _homeGetApiViewState.value = SuccessGetReceiptLocal }
                }

            }
        }
    }


    sealed class HomeViewState {
        class Error(val message: String) : HomeViewState()
        class ShowLoading(val boolean: Boolean) : HomeViewState()
        class DBhasEmpty(val value: Int) : HomeViewState()
        object HasSuccessLogout : HomeViewState()
    }

    sealed class HomeGetApiViewState {
        object SuccessGetPickingList : HomeGetApiViewState()
        class FailedGetPickingList(val message: String) : HomeGetApiViewState()
        object SuccessGetReceiptImport : HomeGetApiViewState()
        class FailedGetReceiptImport(val message: String) : HomeGetApiViewState()
        object SuccessGetReceiptLocal : HomeGetApiViewState()
        class FailedGetReceiptLocal(val message: String) : HomeGetApiViewState()

        /**
         * Peminjam List ViewState
         */
        object SuccessGetPeminjamList : HomeGetApiViewState()
        class FailedGetPeminjam(val message: String) : HomeGetApiViewState()

        /**
         * Dor Picking ViewState
         */
        object SuccessGetDorList : HomeGetApiViewState()
        class FailedGetDorList(val message: String) : HomeGetApiViewState()
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

        /**
         *Peminjaman Get State
         */
    }
}

