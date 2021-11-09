package dynamia.com.barcodescanner.ui.stockopname.input

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dynamia.com.barcodescanner.BuildConfig
import dynamia.com.barcodescanner.di.ViewModelBase
import dynamia.com.core.data.entinty.StockOpnameData
import dynamia.com.core.data.entinty.StockOpnameInputData
import dynamia.com.core.data.repository.StockOpnameRepository
import dynamia.com.core.util.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockOpnameInputViewModel @Inject constructor(
    val repository: StockOpnameRepository, val sharedPreferences: SharedPreferences,
) : ViewModelBase(sharedPreferences) {

    private var stockOpnameData: StockOpnameData? = null
    private val _viewState = MutableLiveData<StockOpnameViewState>()
    val viewState: LiveData<StockOpnameViewState> by lazy { _viewState }

    private val _inputValidaton = MutableLiveData<InputValidation>()
    val inputValidation: LiveData<InputValidation> by lazy { _inputValidaton }

    private val stockId = MutableLiveData<Int>()

    val stockOpnameResultQty: LiveData<Int> = Transformations.switchMap(stockId) {
        repository.getCountQtyInput(it)
    }


    fun getStockOpnameValue(identifier: String, id: Int, bincode: String) {
        viewModelScope.launch {
            try {
                _viewState.value = StockOpnameViewState.Loading(true)
                io {
                    if (id == 0) {
                        when (BuildConfig.FLAVOR) {
                            Constant.APP_STORE -> {
                                repository.getStockOpnameDetailStore(identifier)
                                    .collect { data ->
                                        ui {
                                            stockOpnameData = data
                                            _viewState.value =
                                                StockOpnameViewState.SuccessGetValue(data)
                                            stockId.value = data.id!!
                                            _viewState.value = StockOpnameViewState.Loading(false)
                                        }
                                    }
                            }
                            else -> {
                                repository.getStockOpnameDetailByBarcode(identifier, bincode)
                                    .collect { data ->
                                        ui {
                                            stockOpnameData = data
                                            _viewState.value =
                                                StockOpnameViewState.SuccessGetValue(data)
                                            stockId.value = data.id!!
                                            _viewState.value = StockOpnameViewState.Loading(false)
                                        }
                                    }
                            }
                        }
                    } else {
                        repository.getStockOpnameDetailByBarcode(identifier, id)
                            .collect { data ->
                                ui {
                                    stockOpnameData = data
                                    _viewState.value = StockOpnameViewState.SuccessGetValue(data)
                                    stockId.value = data.id!!
                                    _viewState.value = StockOpnameViewState.Loading(false)
                                }
                            }
                    }


                }
            } catch (e: Exception) {
                e.stackTrace
                _viewState.value = StockOpnameViewState.Loading(false)
                _viewState.value = StockOpnameViewState.Error(e.localizedMessage)
            }
        }
    }

    private fun insertStockOpnameData(qty: String, box: String, bin: String) {
        viewModelScope.launch {
            try {
                io {
                    stockOpnameData?.let { data ->
                        repository.insertInputStockOpname(
                            StockOpnameInputData(
                                documentNo = data.documentNo,
                                quantity = qty.toInt(),
                                lineNo = data.lineNo,
                                itemNo = data.itemNo,
                                binCode = bin,
                                userName = sharedPreferences.getUserName(),
                                insertDateTime = "${getCurrentDate()}T${getCurrentTime()}",
                                locationCode = data.locationCode,
                                headerId = data.id!!,
                                box = box
                            )
                        )
                        ui {
                            _viewState.value = StockOpnameViewState.SuccessSaveValue
                        }
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _viewState.postValue(StockOpnameViewState.Error(e.localizedMessage))
            }
        }
    }

    fun checkUserInputValidation(
        barcode: String,
        qty: String,
        box: String,
        bin: String
    ) {
        if (barcode.isEmpty()) {
            _inputValidaton.value = InputValidation.BarcodeEmpty
        }
        if (qty.isEmpty()) {
            _inputValidaton.value = InputValidation.QtyEmpty
        }
        if (barcode.isNotEmpty() && qty.isNotEmpty()) {
            insertStockOpnameData(qty, box, bin)
        }
    }

    sealed class StockOpnameViewState {
        class Loading(val boolean: Boolean) : StockOpnameViewState()
        class SuccessGetValue(val data: StockOpnameData) : StockOpnameViewState()
        object SuccessSaveValue : StockOpnameViewState()
        class Error(val message: String) : StockOpnameViewState()
    }

    sealed class InputValidation {
        object BarcodeEmpty : InputValidation()
        object QtyEmpty : InputValidation()
    }
}