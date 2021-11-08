package dynamia.com.barcodescanner.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dynamia.com.core.data.repository.TransferShipmentRepository
import dynamia.com.core.domain.ResultWrapper
import dynamia.com.core.util.Event
import dynamia.com.core.util.io
import dynamia.com.core.util.ui
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckLoginViewModel @Inject constructor(private val transferShipmentRepository: TransferShipmentRepository) :
    ViewModel() {

    private var _loginViewState = MutableLiveData<Event<LoginViewState>>()
    val loginViewState: LiveData<Event<LoginViewState>> by lazy { _loginViewState }

    fun getTransferData() {
        viewModelScope.launch {
            try {
                io {
                    transferShipmentRepository.checkLoginCredential().collect { data ->
                        when (data) {
                            is ResultWrapper.Success -> {
                                _loginViewState.postValue(Event(LoginViewState.LoginSuccess))
                            }
                            is ResultWrapper.GenericError -> {
                                ui {
                                    _loginViewState.postValue(Event(LoginViewState.LoginFailed("${data.code} ${data.error}")))
                                }
                            }
                            is ResultWrapper.NetworkError -> {
                                _loginViewState.postValue(Event(LoginViewState.LoginFailed(data.error)))

                            }
                            ResultWrapper.SuccessEmptyValue -> {
                                _loginViewState.postValue(Event(LoginViewState.LoginSuccess))
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _loginViewState.postValue(Event(LoginViewState.LoginFailed(e.localizedMessage)))

            }
        }
    }

    fun getTransferDataDummy() {
        _loginViewState.postValue(Event(LoginViewState.LoginSuccess))
    }

    sealed class LoginViewState {
        object LoginSuccess : LoginViewState()
        class LoginFailed(val message: String) : LoginViewState()
    }
}