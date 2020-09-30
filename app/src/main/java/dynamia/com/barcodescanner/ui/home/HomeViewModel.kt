package dynamia.com.barcodescanner.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import dynamia.com.barcodescanner.R
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.repository.*
import dynamia.com.core.domain.RetrofitBuilder
import dynamia.com.core.util.Constant
import dynamia.com.core.util.Event
import kotlinx.coroutines.launch

class HomeViewModel(
    val pickingListRepository: PickingListRepository,
    val receiptImportRepository: ReceiptImportRepository,
    val receiptLocalRepository: ReceiptLocalRepository,
    val stockCountRepository: StockCountRepository,
    val userRepository: UserRepository,
    val app: Application
) : ViewModelBase(userRepository) {
    private val userData by lazy { userRepository.getUserData() }
    private val retrofitService by lazy {
        RetrofitBuilder.getClient(
            serverAddress = userData.hostName,
            password = userData.password,
            username = userData.username
        )
    }
    val getAllDaataMessage = MutableLiveData<Event<String>>()
    val loading = MutableLiveData<Event<Boolean>>()

    fun getAllDataFromAPI() {
        loading.postValue(Event(true))
        uiScope.launch {
            clearAllDB()
            try {
                val pickingListHeader = retrofitService.getPickingListHeaderAsync()
                val pickingListLine = retrofitService.getPickingListLineAsync()
                val receiptImportHeader = retrofitService.getReceiptImportHeaderAsync()
                val receiptImportLine = retrofitService.getReceiptImportLineAsync()
                val receiptLocalHeader = retrofitService.getReceiptLocalHeaderAsync()
                val receiptLocalLine = retrofitService.getReceiptLocalLineAsync()

                pickingListHeader.value?.let { pickingListHeaders ->
                    for (value in pickingListHeaders) {
                        value?.let {
                            val result = pickingListRepository.insertPickingListHeader(it)
                        }
                    }
                }
                pickingListLine.value.let { pickingListLines ->
                    for (value in pickingListLines) {
                        pickingListRepository.insertPickingListLine(value)
                    }
                }
                receiptImportHeader.value.let { receiptImportHeaders ->
                    for (value in receiptImportHeaders) {
                        receiptImportRepository.insertReceiptImportHeader(value)
                    }
                }
                receiptImportLine.value.let { receiptImportLines ->
                    for (value in receiptImportLines) {
                        receiptImportRepository.insertReceiptImportLine(value)
                    }
                }
                receiptLocalHeader.value.let { receiptLocalheaders ->
                    for (value in receiptLocalheaders) {
                        receiptLocalRepository.insertReceiptLocalHeader(value)
                    }
                }
                receiptLocalLine.value.let { receiptLocalLines ->
                    for (value in receiptLocalLines) {
                        receiptLocalRepository.insertReceiptLocalLine(value)
                    }
                }
                getAllDaataMessage.postValue(Event("Success get all data"))
                loading.postValue(Event(false))
            } catch (e: Exception) {
                Crashlytics.logException(e)
                Log.e("Failed Call API", e.localizedMessage)
                getAllDaataMessage.postValue(Event(e.localizedMessage))
                loading.postValue(Event(false))
            }
        }
    }

    fun checkDBNotNull(): Boolean {
        return pickingListRepository.getCheckEmptyOrNot(getEmployeeName())
    }

    fun clearAllDB() {
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

    fun clearSharedpreference() {
        userRepository.clearUserData()
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
                    pickingPostMessage.postValue(Event(app.resources.getString(R.string.post_all_data_or_no_data,Constant.PICKING_LIST)))
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
                    postImportMessage.postValue(Event(app.resources.getString(R.string.post_all_data_or_no_data,Constant.RECEIPT_IMPORT)))
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
                    postLocalMessage.postValue(Event(app.resources.getString(R.string.post_all_data_or_no_data,Constant.RECEIPT_LOCAL)))
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
                    postStockCountMessage.postValue(Event(app.resources.getString(R.string.post_all_data_or_no_data,Constant.STOCK_COUNT)))
                    loading.postValue(Event(false))
                }
            } catch (e: Exception) {
                loading.postValue(Event(false))
                postStockCountMessage.postValue(Event(e.localizedMessage))
            }
        }
    }
}

