package dynamia.com.barcodescanner.ui.stockopname

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dynamia.com.barcodescanner.ui.transferstore.transferdetail.TransferDetailViewModel
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.repository.StockOpnameRepository
import dynamia.com.core.util.io
import dynamia.com.core.util.ui
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class StockOpnameViewModel(
    val repository: StockOpnameRepository,
    sharedPreferences: SharedPreferences,
) : ViewModelBase(sharedPreferences) {

    private val _stockOpnameViewState = MutableLiveData<StockOpnameViewState>()
    val stockOpnameViewState: LiveData<StockOpnameViewState> by lazy { _stockOpnameViewState }

    fun postStockOpnameData() {
        viewModelScope.launch {
            try {
                var dataPosted = 0
                io {
                    val listEntries = repository.getAllUnsyncStockInput()
                    ui {
                        _stockOpnameViewState.value =
                            StockOpnameViewState.GetUnpostedData(listEntries.size)
                        _stockOpnameViewState.value =
                            StockOpnameViewState.GetSuccessfullyPostedData(dataPosted)
                    }
                    for (data in listEntries) {
                        val param = gson.toJson(data)
                        repository.postStockOpnameData(param).collect {
                            dataPosted++
                            ui {
                                _stockOpnameViewState.value =
                                    StockOpnameViewState.GetSuccessfullyPostedData(
                                        dataPosted)
                            }
                            data.postSuccess()
                            repository.updateInputStockOpname(data)
                        }
                    }
                    ui {
                        _stockOpnameViewState.value = StockOpnameViewState.AllDataPosted
                    }
                }
            } catch (e: Exception) {
                _stockOpnameViewState.value =
                    StockOpnameViewState.ErrorPostData(e.localizedMessage)
            }
        }
    }

    sealed class StockOpnameViewState {
        class GetUnpostedData(val data: Int) : StockOpnameViewState()
        class GetSuccessfullyPostedData(val data: Int) : StockOpnameViewState()
        class ErrorPostData(val message: String) : StockOpnameViewState()
        object AllDataPosted : StockOpnameViewState()
    }
}