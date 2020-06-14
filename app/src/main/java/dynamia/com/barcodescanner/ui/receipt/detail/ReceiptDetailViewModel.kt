package dynamia.com.barcodescanner.ui.receipt.detail

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import dynamia.com.barcodescanner.R
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.repository.ReceiptImportRepository
import dynamia.com.core.data.repository.ReceiptLocalRepository
import dynamia.com.core.domain.RetrofitBuilder
import dynamia.com.core.util.Constant
import dynamia.com.core.util.Event
import kotlinx.coroutines.launch

class ReceiptDetailViewModel(
    val receiptLocalRepository: ReceiptLocalRepository,
    val receiptImportRepository: ReceiptImportRepository,
    val sharedPreferences: SharedPreferences,
    val app: Application
) : ViewModelBase(sharedPreferences) {
    private val retrofitService by lazy {
        RetrofitBuilder.getClient(
            serverAddress = sharedPreferences.getString(Constant.HOST_DOMAIN_SHARED_PREFERENCES, "")
                ?: "",
            password = sharedPreferences.getString(Constant.PASSWORD_SHARED_PREFERENCES, "") ?: "",
            username = sharedPreferences.getString(Constant.USERNAME_SHARED_PREFERENCES, "") ?: ""
        )
    }
    val postImportMessage = MutableLiveData<Event<String>>()
    val postLocalMessage = MutableLiveData<Event<String>>()
    val loading = MutableLiveData<Event<Boolean>>()

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
                }else{
                    loading.postValue(Event(false))
                    postImportMessage.postValue(Event(app.resources.getString(R.string.post_all_data_or_no_data)))
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
                if (receiptLocalData.isNotEmpty()){
                    for (data in receiptLocalData) {
                        if (data.sycn_status.not()){
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
                }else{
                    loading.postValue(Event(false))
                    postLocalMessage.postValue(Event(app.resources.getString(R.string.post_all_data_or_no_data)))
                }

            } catch (e: Exception) {
                loading.postValue(Event(false))
                postLocalMessage.postValue(Event(e.localizedMessage))
            }
        }
    }
}
