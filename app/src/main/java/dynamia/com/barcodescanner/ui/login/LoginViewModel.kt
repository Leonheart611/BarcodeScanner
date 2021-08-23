package dynamia.com.barcodescanner.ui.login

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dynamia.com.barcodescanner.di.ViewModelBase
import dynamia.com.core.data.entinty.UserData
import dynamia.com.core.data.repository.UserRepository
import dynamia.com.core.util.Constant
import dynamia.com.core.util.io
import dynamia.com.core.util.ui
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val userRepository: UserRepository,
) : ViewModelBase(sharedPreferences) {

    private var _modelState = MutableLiveData<LoginState>()
    val modelState: LiveData<LoginState> by lazy { _modelState }

    fun saveSharedPreferences(
        baseUrl: String,
        username: String,
        password: String,
        domain: String,
        companyName: String,
    ) {
        viewModelScope.launch {
            val userData = UserData(
                hostName = baseUrl,
                username = username,
                password = password,
                companyName = companyName,
                domainName = domain
            )
            try {
                io {
                    val editor = sharedPreferences.edit()
                    editor.putString(Constant.USERNAME_KEY, username)
                    editor.putString(Constant.DOMAIN_KEY, domain)
                    editor.putString(Constant.BASEURL_KEY, baseUrl)
                    editor.putString(Constant.PASSWORD_KEY, password)
                    val result = editor.commit()
                    userRepository.insertUserData(userData)
                    if (result)
                        ui { _modelState.value = LoginState.Success("Success Save Data") }
                }
            } catch (e: Exception) {
                ui { _modelState.value = LoginState.Error(e.localizedMessage) }
            }

        }


    }

    fun checkSharedPreferences() {
        val name = sharedPreferences.getString(Constant.USERNAME_KEY, "")
        if (name.isNullOrEmpty().not())
            _modelState.value = LoginState.UserhasLogin(null)

        viewModelScope.launch {
            try {
                io {
                    userRepository.getUserData().collect {
                        ui { _modelState.value = it?.let { data -> LoginState.UserHaveData(data) } }
                    }
                }
            } catch (e: Exception) {
                _modelState.value = LoginState.UserhasLogin(null)
            }
        }
    }

    sealed class LoginState {
        class Success(val message: String) : LoginState()
        class Error(val message: String) : LoginState()
        class ShowLoading(val boolean: Boolean) : LoginState()
        class UserhasLogin(val userData: UserData?) : LoginState()
        class UserHaveData(val userData: UserData) : LoginState()
    }


}
