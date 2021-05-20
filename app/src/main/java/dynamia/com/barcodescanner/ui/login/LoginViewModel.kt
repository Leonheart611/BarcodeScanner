package dynamia.com.barcodescanner.ui.login

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.model.UserData
import dynamia.com.core.data.repository.UserRepository
import dynamia.com.core.util.Constant
import dynamia.com.core.util.io
import dynamia.com.core.util.ui
import kotlinx.coroutines.launch

class LoginViewModel(
    private val sharedPreferences: SharedPreferences,
    private val userRepository: UserRepository
) : ViewModelBase(sharedPreferences) {

    private var _modelState = MutableLiveData<LoginState>()
    val modelState: LiveData<LoginState> by lazy { _modelState }

    fun saveSharedPreferences(
        hostname: String,
        username: String,
        password: String,
        company: String
    ) {
        viewModelScope.launch {
            val userData = UserData(
                hostName = hostname,
                username = username,
                password = password,
                companyName = company
            )
            try {
                io {
                    val editor = sharedPreferences.edit()
                    editor.putString(Constant.USERNAME_KEY, username)
                    editor.putString(Constant.COMPANY_NAME, company)
                    editor.putString(Constant.HOST_DOMAIN_KEY, hostname)
                    editor.putString(Constant.PASSWORD_KEY, password)
                    editor.apply()
                    userRepository.insertUserData(userData)
                    ui { _modelState.value = LoginState.Success("Success Save Data") }
                }
            } catch (e: Exception) {
                ui { _modelState.value = LoginState.Error(e.localizedMessage) }
            }

        }


    }

    fun checkSharedPreferences() {
        val name = sharedPreferences.getString(Constant.COMPANY_NAME, "")
        if (name.isNullOrEmpty().not())
            _modelState.value = LoginState.UserhasLogin(null)
    }

    sealed class LoginState {
        class Success(val message: String) : LoginState()
        class Error(val message: String) : LoginState()
        class ShowLoading(val boolean: Boolean) : LoginState()
        class UserhasLogin(val userData: UserData?) : LoginState()
    }


}
