package dynamia.com.barcodescanner.ui.transferstore.transferinput

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.entinty.TransferInputData
import dynamia.com.core.data.entinty.TransferShipmentHeader
import dynamia.com.core.data.entinty.TransferShipmentLine
import dynamia.com.core.data.repository.TransferShipmentRepository
import dynamia.com.core.util.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TransferInputViewModel(
    private val transferShipmentRepository: TransferShipmentRepository,
    val sharedPreferences: SharedPreferences,
) : ViewModelBase(sharedPreferences) {

    private val _transferInputViewState = MutableLiveData<TransferInputViewState>()
    val transferInputViewState: LiveData<TransferInputViewState> by lazy { _transferInputViewState }
    private val _inputValidaton = MutableLiveData<InputValidation>()
    val inputValidation: LiveData<InputValidation> by lazy { _inputValidaton }

    private var transferLineData: TransferShipmentLine? = null
    private var transferHeaderData: TransferShipmentHeader? = null

    fun getHistoryValue(no: String) {
        viewModelScope.launch {
            io {
                transferShipmentRepository.getTransferInputHistory(no).collect {
                    ui {
                        _transferInputViewState.value =
                            TransferInputViewState.SuccessGetHistoryValue(it.toMutableList())
                    }
                }
            }

        }
    }

    fun getPickingListLineValue(no: String, identifier: String) {
        viewModelScope.launch {
            try {
                _transferInputViewState.value =
                    TransferInputViewState.LoadingSearchPickingList(true)
                io {
                    transferShipmentRepository.getTransferHeaderDetail(no).collect {
                        transferHeaderData = it
                    }
                    transferShipmentRepository.getLineDetailFromBarcode(no, identifier)
                        .collect { data ->
                            ui {
                                transferLineData = data
                                _transferInputViewState.value =
                                    TransferInputViewState.SuccessGetValue(data)
                                _transferInputViewState.value =
                                    TransferInputViewState.LoadingSearchPickingList(false)
                            }
                        }

                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    TransferInputViewState.LoadingSearchPickingList(false)
                _transferInputViewState.value =
                    TransferInputViewState.ErrorGetData(e.localizedMessage)
            }
        }
    }

    private fun insertTransferInput(qty: String, timeStamp: String) {
        viewModelScope.launch {
            try {
                io {
                    transferHeaderData?.let { header ->
                        transferLineData?.let { line ->
                            val value = transferShipmentRepository.insertTransferInput(
                                TransferInputData(
                                    documentNo = line.documentNo,
                                    quantity = qty.toInt(),
                                    lineNo = line.lineNo,
                                    itemNo = line.no,
                                    transferFromBinCode = header.transferFromCode,
                                    transferToBinCode = header.transferToCode,
                                    userName = sharedPreferences.getUserName(),
                                    insertDateTime = timeStamp
                                )
                            )
                            ui {
                                if (value) {
                                    _transferInputViewState.value =
                                        TransferInputViewState.SuccessSaveData
                                } else {
                                    _transferInputViewState.value =
                                        TransferInputViewState.ErrorSaveData("Error Save Data")
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    TransferInputViewState.ErrorGetData(e.localizedMessage)
            }

        }
    }

    fun checkUserInputValidation(
        barcode: String,
        qty: String,
    ) {
        if (barcode.isEmpty()) {
            _inputValidaton.value = InputValidation.BarcodeEmpty
        }
        if (qty.isEmpty()) {
            _inputValidaton.value = InputValidation.QtyEmpty
        }
        if (barcode.isNotEmpty() && qty.isNotEmpty()) {
            _inputValidaton.value = InputValidation.AllValidationCorrect
            insertTransferInput(qty, "${getCurrentDate()}T${getCurrentTime()}")
        }
    }


    sealed class TransferInputViewState {
        class SuccessGetValue(val data: TransferShipmentLine) : TransferInputViewState()
        class SuccessGetHistoryValue(val data: MutableList<TransferInputData>) :
            TransferInputViewState()

        object SuccessSaveData : TransferInputViewState()
        class ErrorSaveData(val message: String) : TransferInputViewState()
        class LoadingSearchPickingList(val status: Boolean) : TransferInputViewState()

        class ErrorGetData(val message: String) : TransferInputViewState()
    }

    sealed class InputValidation {
        object BarcodeEmpty : InputValidation()
        object QtyEmpty : InputValidation()
        object AllValidationCorrect : InputValidation()
    }
}
