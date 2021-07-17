package dynamia.com.barcodescanner.ui.binreclass.detail

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.entinty.BinreclassHeader
import dynamia.com.core.data.entinty.BinreclassInputData
import dynamia.com.core.data.repository.BinreclassRepository
import dynamia.com.core.util.Event
import dynamia.com.core.util.getCurrentDate
import dynamia.com.core.util.io
import dynamia.com.core.util.ui
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class BinreclassDetailViewModel(
    val repository: BinreclassRepository,
    sharedPreferences: SharedPreferences,
) : ViewModelBase(sharedPreferences) {

    private val _viewState = MutableLiveData<BinReclassViewState>()
    val viewState: LiveData<BinReclassViewState> by lazy { _viewState }

    private val _inputValidaton = MutableLiveData<Event<InputValidation>>()
    val inputValidation: LiveData<Event<InputValidation>> by lazy { _inputValidaton }

    private val _inputRebinData = MutableLiveData<Event<InputReclassViewState>>()
    val inputRebinData: LiveData<Event<InputReclassViewState>> by lazy { _inputRebinData }

    var inputHeader: BinreclassHeader? = null
    var inputData: BinreclassInputData? = null

    fun getLocalDataHeader(binFrom: String, binTo: String) {
        viewModelScope.launch {
            try {
                io {
                    val data = repository.getBinReclassHeaderDetail(binFrom, binTo)
                    inputHeader = data
                    ui { _viewState.value = BinReclassViewState.SuccessGetLocalData(data) }
                }
            } catch (e: Exception) {
                _viewState.value = BinReclassViewState.OnErrorGetLocalData(e.localizedMessage)
            }
        }
    }

    fun getLocalInputdata(id: Int) {
        viewModelScope.launch {
            try {
                io {
                    repository.getBinReclassById(id).collect {
                        inputData = it
                        ui {
                            _inputRebinData.value =
                                Event(InputReclassViewState.SuccessGetHistoryData(it))
                        }
                    }

                }
            } catch (e: Exception) {
                _inputRebinData.value =
                    Event(InputReclassViewState.OnErrorSaveData(e.localizedMessage))
            }
        }
    }

    private fun insertDataBin(data: BinreclassInputData) {
        viewModelScope.launch {
            try {
                io {
                    repository.insertBinReclassInputData(data)
                    ui { _inputRebinData.value = Event(InputReclassViewState.SuccessSaveData) }
                }
            } catch (e: Exception) {
                _inputRebinData.value =
                    Event(InputReclassViewState.OnErrorSaveData(e.localizedMessage))
            }
        }
    }

    fun deleteDataBin(id: Int) {
        viewModelScope.launch {
            try {
                io {
                    repository.deleteBinReclassInputData(id)
                    ui { _inputRebinData.value = Event(InputReclassViewState.SuccessDeleteData) }
                }
            } catch (e: Exception) {
                _inputRebinData.value =
                    Event(InputReclassViewState.OnErrorSaveData(e.localizedMessage))
            }
        }
    }

    fun updateDataBin(id: Int, qty: Int) {
        viewModelScope.launch {
            try {
                io {
                    repository.updateBinReclassInputQty(id, qty)
                    ui { _inputRebinData.value = Event(InputReclassViewState.SuccessUpdateData) }
                }
            } catch (e: Exception) {
                _inputRebinData.value =
                    Event(InputReclassViewState.OnErrorSaveData(e.localizedMessage))
            }
        }
    }

    fun checkUserInputValidation(
        barcode: String,
        qty: String,
        fromBin: String,
        toBin: String,
    ) {
        if (barcode.isEmpty()) {
            _inputValidaton.value = Event(InputValidation.BarcodeEmpty)
        }
        if (qty.isEmpty()) {
            _inputValidaton.value = Event(InputValidation.QtyEmpty)
        }
        if (barcode.isNotEmpty() && qty.isNotEmpty()) {
            viewModelScope.launch {
                io {
                    val data = repository.getBinReclassHeaderDetail(fromBin, toBin)
                    data.let {
                        insertDataBin(BinreclassInputData(
                            documentNo = it.documentNo,
                            lineNo = 0,
                            itemIdentifier = barcode,
                            binCode = it.transferFromBinCode,
                            newBinCode = it.transferToBinCode,
                            userName = getUserName(),
                            insertDateTime = getCurrentDate(),
                            quantity = qty.toInt(),
                            headerId = data.id!!
                        ))
                    }
                }
            }
        }
    }

    fun updateRebinClassHeader(fromBin: String, toBin: String) {
        viewModelScope.launch {
            try {
                io {
                    inputHeader?.let {
                        repository.updateBinFromAndBinToCode(id = it.id!!,
                            binFrom = fromBin,
                            binTo = toBin).collect { data ->
                            if (data) {
                                ui {
                                    _viewState.value =
                                        BinReclassViewState.SuccessUpdateHeaderData(fromBin, toBin)
                                }
                            } else {
                                ui {
                                    _viewState.value =
                                        BinReclassViewState.OnErrorGetLocalData("Bin To and From Code Sudah ada")
                                }
                            }
                        }
                    }

                }
            } catch (e: Exception) {
                e.stackTrace
                _viewState.value = BinReclassViewState.OnErrorGetLocalData(e.localizedMessage)
            }
        }
    }


    sealed class BinReclassViewState {
        class SuccessGetLocalData(val data: BinreclassHeader) : BinReclassViewState()
        class SuccessUpdateHeaderData(val fromBin: String, val toBin: String) :
            BinReclassViewState()

        class OnErrorGetLocalData(val error: String) : BinReclassViewState()
    }

    sealed class InputReclassViewState {
        class SuccessGetHistoryData(val data: BinreclassInputData) : InputReclassViewState()
        object SuccessSaveData : InputReclassViewState()
        object SuccessUpdateData : InputReclassViewState()
        object SuccessDeleteData : InputReclassViewState()
        class OnErrorSaveData(val error: String) : InputReclassViewState()
    }

    sealed class InputValidation {
        object BarcodeEmpty : InputValidation()
        object QtyEmpty : InputValidation()
    }
}