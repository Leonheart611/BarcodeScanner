package dynamia.com.barcodescanner.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dynamia.com.core.util.Event
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {
    val checkLoginUser = MutableLiveData<Event<Boolean>>()

    fun setCheckLoginUser(result: Boolean) {
        checkLoginUser.value = Event(result)
    }
}