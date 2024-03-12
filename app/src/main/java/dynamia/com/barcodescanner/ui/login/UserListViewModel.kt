package dynamia.com.barcodescanner.ui.login

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dynamia.com.core.data.entinty.UserData
import dynamia.com.core.data.repository.UserRepository
import dynamia.com.core.util.Event
import dynamia.com.core.util.io
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    val sharedPreferences: SharedPreferences,
    val userRepository: UserRepository
) : ViewModel() {

    val userData = MutableLiveData<List<UserData>>()
    val deleteStatus = MutableLiveData<Event<Boolean>>()

/*    fun getAllUserData() {
        viewModelScope.launch {
            io {
                .collect { userData.postValue(it) }
            }
        }
    }*/

    fun deleteUser(user: UserData) {
        viewModelScope.launch {
            io {
                val status = userRepository.deleteUserData(user)
                deleteStatus.postValue(Event(status))
            }
        }
    }
}