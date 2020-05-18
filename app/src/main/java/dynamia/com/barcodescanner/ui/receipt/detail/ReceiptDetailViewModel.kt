package dynamia.com.barcodescanner.ui.receipt.detail

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dynamia.com.barcodescanner.domain.RetrofitBuilder
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.model.ReceiptImportScanEntriesValue
import dynamia.com.core.data.repository.ReceiptImportRepository
import dynamia.com.core.data.repository.ReceiptLocalRepository
import dynamia.com.core.util.Constant
import dynamia.com.core.util.Event
import kotlinx.coroutines.launch

class ReceiptDetailViewModel(
    val receiptLocalRepository: ReceiptLocalRepository,
    val receiptImportRepository: ReceiptImportRepository,
    val sharedPreferences: SharedPreferences
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
    var gson: com.google.gson.Gson = GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .create()

    fun postReceiptImportData(receiptImportData: List<ReceiptImportScanEntriesValue>) {
        uiScope.launch {
            try {
                for (data in receiptImportData) {
                    if (data.sycn_status.not()) {
                        val param = gson.toJson(data)
                        val result = retrofitService.postReceiptImportEntry(param)
                        result.let {
                            data.apply {
                                sycn_status = true
                            }
                            receiptImportRepository.updateReceiptImportScanEntry(data)
                            postImportMessage.postValue(Event("Berhasil Post Data :)"))
                        }
                    }
                }
            } catch (e: Exception) {
                postImportMessage.postValue(Event(e.localizedMessage))

            }
        }
    }
}
