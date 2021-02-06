package dynamia.com.barcodescanner.ui.stockcounting

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.model.StockCount
import dynamia.com.core.data.repository.NetworkRepository
import dynamia.com.core.data.repository.StockCountRepository
import dynamia.com.core.domain.ResultWrapper
import dynamia.com.core.util.io
import dynamia.com.core.util.ui
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class StockCountingViewModel(
    val stockCountRepository: StockCountRepository,
    sharedPreferences: SharedPreferences,
    private val networkRepository: NetworkRepository
) : ViewModelBase(sharedPreferences) {
    private val _stockCountViewState = MutableLiveData<StockCountingViewState>()
    val stockCountViewState: LiveData<StockCountingViewState> by lazy { _stockCountViewState }

    private val _stockCountPostViewState = MutableLiveData<StockCountPostViewState>()
    val stockCountPostViewState: LiveData<StockCountPostViewState> by lazy { _stockCountPostViewState }

    fun postStockCountDataNew() {
        viewModelScope.launch {
            try {
                var dataPosted = 0
                io {
                    val stockCounts = stockCountRepository.getAllUnsycnStockCount()
                    ui {
                        _stockCountPostViewState.value =
                            StockCountPostViewState.GetUnpostedData(stockCounts.size)
                        _stockCountPostViewState.value =
                            StockCountPostViewState.UpdateSuccessPosted(dataPosted)
                    }
                    for (data in stockCounts) {
                        val body = gson.toJson(data)
                        networkRepository.postStockCountEntry(body).collect {
                            when (it) {
                                is ResultWrapper.Success -> {
                                    dataPosted++
                                    ui {
                                        _stockCountPostViewState.value =
                                            StockCountPostViewState.UpdateSuccessPosted(dataPosted)
                                    }
                                    data.apply {
                                        sycn_status = true
                                    }
                                    stockCountRepository.updateStockCount(data)
                                }
                                is ResultWrapper.GenericError -> {
                                    ui {
                                        _stockCountPostViewState.value =
                                            StockCountPostViewState.ErrorPostData("${it.code} ${it.error?.odataError?.message?.value}")
                                    }
                                }
                                ResultWrapper.NetworkError -> {

                                }
                            }
                        }
                    }
                }
                ui { _stockCountPostViewState.value = StockCountPostViewState.SuccessPostedAllData }
            } catch (e: Exception) {
                _stockCountPostViewState.value =
                    StockCountPostViewState.ErrorPostData(e.localizedMessage)
            }
        }
    }

    fun checkSnNo(serialNo: String) {
        viewModelScope.launch {
            try {
                io {
                    val result = stockCountRepository.checkSN(serialNo)
                    ui { _stockCountViewState.value = StockCountingViewState.CheckedSnNo(result) }
                }
            } catch (e: Exception) {
                _stockCountViewState.value = StockCountingViewState.Error(e.localizedMessage)
            }
        }
    }

    fun deleteSnNo(data: StockCount) {
        viewModelScope.launch {
            try {
                io {
                    stockCountRepository.deleteSn(data)
                }
            } catch (e: Exception) {

            }
        }
    }

    sealed class StockCountingViewState {
        class CheckedSnNo(val isEmpty: Boolean) : StockCountingViewState()
        class Error(val message: String) : StockCountingViewState()
    }

    sealed class StockCountPostViewState {
        class GetUnpostedData(val data: Int) : StockCountPostViewState()
        class UpdateSuccessPosted(val data: Int) : StockCountPostViewState()
        class ErrorPostData(val message: String) : StockCountPostViewState()
        object SuccessPostedAllData : StockCountPostViewState()
    }
}
