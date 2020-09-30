package dynamia.com.barcodescanner.ui.stockcounting

import android.app.Application
import androidx.lifecycle.MutableLiveData
import dynamia.com.barcodescanner.R
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.repository.StockCountRepository
import dynamia.com.core.data.repository.UserRepository
import dynamia.com.core.domain.RetrofitBuilder
import dynamia.com.core.util.Event
import kotlinx.coroutines.launch

class StockCountingViewModel(
    val stockCountRepository: StockCountRepository,
    sharedPreferences: UserRepository,
    val app: Application
) : ViewModelBase(sharedPreferences) {
    private val userData by lazy { sharedPreferences.getUserData() }
    private val retrofitService by lazy {
        RetrofitBuilder.getClient(
            serverAddress = userData.hostName,
            password = userData.password,
            username = userData.username
        )
    }
    val postStockCountMessage = MutableLiveData<Event<String>>()
    val loading = MutableLiveData<Event<Boolean>>()
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
                    postStockCountMessage.postValue(Event("Success Post Data Stock Count"))
                    loading.postValue(Event(false))

                } else {
                    postStockCountMessage.postValue(Event(app.resources.getString(R.string.post_all_data_or_no_data)))
                    loading.postValue(Event(false))
                }
            } catch (e: Exception) {
                loading.postValue(Event(false))
                postStockCountMessage.postValue(Event(e.localizedMessage))
            }
        }
    }
}
