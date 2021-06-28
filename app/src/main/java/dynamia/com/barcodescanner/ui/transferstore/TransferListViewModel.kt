package dynamia.com.barcodescanner.ui.transferstore

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.repository.TransferShipmentRepository
import dynamia.com.core.domain.ResultWrapper
import dynamia.com.core.util.io
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TransferListViewModel(
    val transferShipmentRepository: TransferShipmentRepository,
    sharedPreferences: SharedPreferences
) : ViewModelBase(sharedPreferences) {

    private var _viewState = MutableLiveData<TransferListViewState>()
    val transferViewState: LiveData<TransferListViewState> by lazy { _viewState }


    fun updateTransferShipment() {
        try {
            viewModelScope.launch {
                io {
                    _viewState.postValue(TransferListViewState.ShowLoading(true))
                    transferShipmentRepository.deleteAllTransferHeader()
                    transferShipmentRepository.getTransferShipmentHeaderAsync().collect {
                        _viewState.postValue(TransferListViewState.ShowLoading(false))
                        when (it) {
                            is ResultWrapper.GenericError -> {
                                _viewState.postValue(TransferListViewState.Error("${it.code} ${it.error}"))
                            }
                            is ResultWrapper.NetworkError -> {
                                _viewState.postValue(TransferListViewState.Error(it.error))
                            }
                            is ResultWrapper.Success -> {
                                it.value.forEach { data ->
                                    transferShipmentRepository.insertTransferHeader(data)
                                }
                                _viewState.postValue(TransferListViewState.SuccessUpdateData)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            _viewState.postValue(TransferListViewState.ShowLoading(false))
            _viewState.postValue(TransferListViewState.Error(e.localizedMessage))
        }
    }

    sealed class TransferListViewState {
        class Error(val message: String) : TransferListViewState()
        class ShowLoading(val boolean: Boolean) : TransferListViewState()
        object SuccessUpdateData : TransferListViewState()
    }


}
