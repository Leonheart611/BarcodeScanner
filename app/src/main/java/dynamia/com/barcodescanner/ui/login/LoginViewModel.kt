package dynamia.com.barcodescanner.ui.login

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.model.UserData
import dynamia.com.core.data.repository.PickingListRepository
import dynamia.com.core.data.repository.ReceiptImportRepository
import dynamia.com.core.data.repository.ReceiptLocalRepository
import dynamia.com.core.data.repository.UserRepository
import dynamia.com.core.util.Constant
import dynamia.com.core.util.io
import kotlinx.coroutines.launch

class LoginViewModel(
	val pickingListRepository: PickingListRepository,
	val receiptImportRepository: ReceiptImportRepository,
	val receiptLocalRepository: ReceiptLocalRepository,
	private val sharedPreferences: SharedPreferences,
	val userRepository: UserRepository
) : ViewModelBase(sharedPreferences) {
	
	private var _modelState = MutableLiveData<LoginState>()
	val modelState: LiveData<LoginState> by lazy { _modelState }
	
	fun saveSharedPreferences(
		hostname: String,
		username: String,
		password: String,
		employee: String
	) {
		viewModelScope.launch {
			val editor = sharedPreferences.edit()
			editor.putString(Constant.USERNAME_KEY, username)
			editor.putString(Constant.EMPLOYEE_KEY, employee)
			editor.putString(Constant.HOST_DOMAIN_KEY, hostname)
			editor.putString(Constant.PASSWORD_KEY, password)
			editor.apply()
			io {
				userRepository.insertUserData(
					UserData(
						1, hostname, username, password, employee
					)
				)
				_modelState.postValue(LoginState.Success("Success Save Data"))
			}
		}
	}
	
	fun checkSharedPreferences() {
		val name = sharedPreferences.getString(Constant.EMPLOYEE_KEY, "")
		if (name.isNullOrEmpty().not())
			_modelState.value = LoginState.UserhasLogin
	}
	
	
	sealed class LoginState {
		class Success(val message: String) : LoginState()
		class Error(val message: String) : LoginState()
		class ShowLoading(val boolean: Boolean) : LoginState()
		object UserhasLogin : LoginState()
	}
	
	
}
