package dynamia.com.barcodescanner.ui.home

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dynamia.com.barcodescanner.ui.home.HomeViewModel.HomeGetApiViewState.FailedGetShippingData
import dynamia.com.barcodescanner.ui.home.HomeViewModel.HomeGetApiViewState.SuccessGetShipingData
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.entinty.TransferShipmentHeaderAsset
import dynamia.com.core.data.entinty.TransferShipmentLineAsset
import dynamia.com.core.data.repository.TransferShipmentRepository
import dynamia.com.core.domain.ResultWrapper.*
import dynamia.com.core.util.io
import dynamia.com.core.util.ui
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel(
    val transferShipmentRepository: TransferShipmentRepository,
    private val sharedPreferences: SharedPreferences,
) : ViewModelBase(sharedPreferences) {

    private var _homeViewState = MutableLiveData<HomeViewState>()
    val homeViewState: LiveData<HomeViewState> by lazy { _homeViewState }

    private var _homeGetApiViewState = MutableLiveData<HomeGetApiViewState>()
    val homeGetApiViewState: LiveData<HomeGetApiViewState> by lazy { _homeGetApiViewState }

    private var _homePostViewState = MutableLiveData<HomePostViewState>()
    val homePostViewState: LiveData<HomePostViewState> by lazy { _homePostViewState }
/*
    fun checkDBNotNull() {
        viewModelScope.launch {
            try {
                io {
                    transferShipmentRepository.getCheckEmptyOrNot().collect {
                        ui { _homeViewState.value = HomeViewState.DBhasEmpty(it) }
                    }
                }
            } catch (e: Exception) {
                _homeViewState.value = HomeViewState.Error(e.localizedMessage)
            }
        }
    }*/

    fun clearAllDB() {
        viewModelScope.launch {
            try {
                io {
                    transferShipmentRepository.deleteAllTransferHeader()
                    transferShipmentRepository.deleteAllTransferLine()
                }
            } catch (e: Exception) {
                _homeViewState.value = HomeViewState.Error(e.localizedMessage)
                Log.e("clearAllDb", e.localizedMessage)
            }
        }
    }

    fun logOutSharedPreferences() {
        clearAllDB()
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        _homeViewState.value = HomeViewState.HasSuccessLogout
    }

    fun getTransferData() {
        viewModelScope.launch {
            try {
                io {
                    transferShipmentRepository.getTransferShipmentHeaderAsync()
                        .collect { dataHeader ->
                            when (dataHeader) {
                                is Success -> dataHeader.value.forEach {
                                    transferShipmentRepository.insertTransferHeader(it)
                                }
                                is GenericError -> {
                                    ui {
                                        _homeGetApiViewState.value =
                                            FailedGetShippingData("${dataHeader.code} ${dataHeader.error}")
                                    }
                                }
                                is NetworkError -> {
                                    _homeGetApiViewState.postValue(FailedGetShippingData("Error Network"))

                                }
                            }
                        }
                    transferShipmentRepository.getTransferShipmentLineAsync().collect { data ->
                        when (data) {
                            is Success -> data.value.forEach {
                                transferShipmentRepository.insertTransferLine(it)
                            }
                            is GenericError -> {
                                ui {
                                    _homeGetApiViewState.value =
                                        FailedGetShippingData("${data.code} ${data.error}")
                                }
                            }
                            is NetworkError -> {
                                _homeGetApiViewState.postValue(FailedGetShippingData(data.error))
                            }
                        }
                    }
                }
                ui { _homeGetApiViewState.value = SuccessGetShipingData }
            } catch (e: Exception) {
                _homeGetApiViewState.value = FailedGetShippingData(e.localizedMessage)
            }
        }
    }

    sealed class HomeViewState {
        class Error(val message: String) : HomeViewState()
        class ShowLoading(val boolean: Boolean) : HomeViewState()
        class DBhasEmpty(val value: Int) : HomeViewState()
        object HasSuccessLogout : HomeViewState()
    }

    sealed class HomeGetApiViewState {
        object SuccessGetShipingData : HomeGetApiViewState()
        class FailedGetShippingData(val message: String) : HomeGetApiViewState()
        object SuccessGetReceiptImport : HomeGetApiViewState()
        class FailedGetReceiptImport(val message: String) : HomeGetApiViewState()
        object SuccessGetReceiptLocal : HomeGetApiViewState()
        class FailedGetReceiptLocal(val message: String) : HomeGetApiViewState()
    }

    sealed class HomePostViewState {
        class GetUnpostedPicking(val data: Int) : HomePostViewState()
        class GetSuccessfullyPicking(val data: Int) : HomePostViewState()
        class ErrorPostPicking(val message: String) : HomePostViewState()
        object AllDataPostedPicking : HomePostViewState()

        class GetUnpostedLocal(val data: Int) : HomePostViewState()
        class GetSuccessfulLocal(val data: Int) : HomePostViewState()
        class ErrorPostLocal(val message: String) : HomePostViewState()
        object SuccessPostallLocal : HomePostViewState()

        class GetUnpostedImport(val data: Int) : HomePostViewState()
        class GetSuccessfulImport(val data: Int) : HomePostViewState()
        class ErrorPostImport(val message: String) : HomePostViewState()
        object SuccessPostallImport : HomePostViewState()

        class GetUnpostedCount(val data: Int) : HomePostViewState()
        class GetSuccessfulCount(val data: Int) : HomePostViewState()
        class ErrorPostCount(val message: String) : HomePostViewState()
        object SuccessPostallCount : HomePostViewState()
    }


    /**
     * For Get Data From Assets
     */

    fun saveAssetData(
        transferShipmentHeader: TransferShipmentHeaderAsset,
        transferShipmentLine: TransferShipmentLineAsset
    ) {
        try {

            viewModelScope.launch {
                io {
                    transferShipmentHeader.value?.let {
                        it.forEach { data ->
                            transferShipmentRepository.insertTransferHeader(data)
                        }
                    }

                    transferShipmentLine.value?.let {
                        it.forEach { data ->
                            transferShipmentRepository.insertTransferLine(data)
                        }
                        ui { _homeGetApiViewState.value = SuccessGetShipingData }
                    }
                }
            }
        } catch (e: Exception) {

        }
    }

    fun getUser() {
        viewModelScope.launch {
            try {
                io {
                    transferShipmentRepository.getUser()
                        .collect { dataHeader ->
                            ui { _homeGetApiViewState.value = FailedGetShippingData(dataHeader) }
                        }
                }
                ui { _homeGetApiViewState.value = SuccessGetShipingData }
            } catch (e: Exception) {
                _homeGetApiViewState.value = FailedGetShippingData(e.localizedMessage)
            }
        }

    }
}

