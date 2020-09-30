package dynamia.com.barcodescanner.ui.pickinglist.pickingdetail

import android.app.Application
import androidx.lifecycle.MutableLiveData
import dynamia.com.barcodescanner.R
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.repository.PickingListRepository
import dynamia.com.core.data.repository.UserRepository
import dynamia.com.core.domain.RetrofitBuilder
import dynamia.com.core.util.Event
import kotlinx.coroutines.launch

class PickingDetailViewModel(
    val pickingListRepository: PickingListRepository,
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
    val pickingPostMessage = MutableLiveData<Event<String>>()
    val loading = MutableLiveData<Event<Boolean>>()
    fun postPickingData(){
        uiScope.launch {
            loading.postValue(Event(true))
            try {
                val pickingListScanEntries = pickingListRepository.getAllUnscynPickingListScanEntries()
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
                    pickingPostMessage.postValue(Event("Berhasil Post Data :)"))
                    loading.postValue(Event(false))

                } else {
                    pickingPostMessage.postValue(Event(app.resources.getString(R.string.post_all_data_or_no_data)))
                    loading.postValue(Event(false))
                }
            } catch (e: Exception) {
                loading.postValue(Event(false))
                pickingPostMessage.postValue(Event(e.localizedMessage))
            }
        }
    }


}
