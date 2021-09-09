package dynamia.com.barcodescanner.ui.checkstock

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dynamia.com.barcodescanner.di.ViewModelBase
import dynamia.com.core.data.entinty.StockCheckingData
import dynamia.com.core.data.repository.StockOpnameRepository
import dynamia.com.core.domain.ResultWrapper
import dynamia.com.core.util.ui
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckStockViewModel @Inject constructor(
    val stockOpnameRepository: StockOpnameRepository,
    sharedPreferences: SharedPreferences,
) : ViewModelBase(sharedPreferences) {

    private var _checkStockVS = MutableLiveData<CheckStockViewState>()
    val checkStockVs: LiveData<CheckStockViewState> by lazy { _checkStockVS }


    fun getStockCheck(value: String) {
        viewModelScope.launch {
            try {
                _checkStockVS.value = CheckStockViewState.Loading(true)
                stockOpnameRepository.getStockCheck(value)
                    .collect { result ->
                        _checkStockVS.value = CheckStockViewState.Loading(false)
                        when (result) {
                            is ResultWrapper.Success -> {
                                ui {
                                    _checkStockVS.value = CheckStockViewState.Success(result.value)
                                }
                            }
                            is ResultWrapper.GenericError -> {
                                ui {
                                    _checkStockVS.value =
                                        CheckStockViewState.Error("${result.code} ${result.error}")
                                }
                            }
                            is ResultWrapper.NetworkError -> {
                                _checkStockVS.postValue(
                                    CheckStockViewState.Error(
                                        "Error Network"
                                    )
                                )

                            }
                        }
                    }
            } catch (e: Exception) {
                _checkStockVS.value = CheckStockViewState.Loading(false)
                _checkStockVS.postValue(CheckStockViewState.Error(e.localizedMessage))
            }
        }
    }

    fun getCheckStockFromAsset(data: MutableList<StockCheckingData>) {
        _checkStockVS.value = CheckStockViewState.Success(data)
    }


    sealed class CheckStockViewState {
        class Loading(val loading: Boolean) : CheckStockViewState()
        class Success(val data: MutableList<StockCheckingData>) : CheckStockViewState()
        class Error(val message: String) : CheckStockViewState()
    }

}