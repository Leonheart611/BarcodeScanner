package dynamia.com.barcodescanner.ui.home

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import dynamia.com.barcodescanner.domain.RetrofitBuilder
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.repository.PickingListRepository
import dynamia.com.core.data.repository.ReceiptImportRepository
import dynamia.com.core.data.repository.ReceiptLocalRepository
import dynamia.com.core.util.Constant.HOST_DOMAIN_SHARED_PREFERENCES
import dynamia.com.core.util.Constant.PASSWORD_SHARED_PREFERENCES
import dynamia.com.core.util.Constant.USERNAME_SHARED_PREFERENCES
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(
    val pickingListRepository: PickingListRepository,
    val receiptImportRepository: ReceiptImportRepository,
    val receiptLocalRepository: ReceiptLocalRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModelBase(sharedPreferences) {
    private val retrofitService by lazy {
        RetrofitBuilder.getClient(
            serverAddress = sharedPreferences.getString(HOST_DOMAIN_SHARED_PREFERENCES, "") ?: "",
            password = sharedPreferences.getString(PASSWORD_SHARED_PREFERENCES, "") ?: "",
            username = sharedPreferences.getString(USERNAME_SHARED_PREFERENCES, "") ?: ""
        )
    }

    val message = MutableLiveData<String>()

    fun getAllDataFromAPI() {
        clearAllDB()
        uiScope.launch {
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
                            pickingListRepository.insertPickingListHeader(it)
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
                for (value in receiptImportLine.value) {
                    receiptImportRepository.insertReceiptImportLine(value)
                }
                for (value in receiptLocalHeader.value) {
                    receiptLocalRepository.insertReceiptLocalHeader(value)
                }
                for (value in receiptLocalLine.value) {
                    receiptLocalRepository.insertReceiptLocalLine(value)
                }
                message.postValue("Success Hit API")
            } catch (e: Exception) {
                Crashlytics.logException(e)
                Log.e("Failed Call API", e.localizedMessage)
                message.postValue(e.localizedMessage)
            }
        }
    }

    fun checkDBNotNull(): Boolean {
        return pickingListRepository.getCountPickingListHeader() == 0
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
    }
}

