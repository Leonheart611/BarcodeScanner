package dynamia.com.barcodescanner.ui.transferstore.transferinput

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dynamia.com.barcodescanner.ui.transferstore.TransferType
import dynamia.com.barcodescanner.ui.transferstore.TransferType.RECEIPT
import dynamia.com.barcodescanner.ui.transferstore.TransferType.SHIPMENT
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.entinty.TransferInputData
import dynamia.com.core.data.entinty.TransferReceiptHeader
import dynamia.com.core.data.entinty.TransferShipmentHeader
import dynamia.com.core.data.entinty.TransferShipmentLine
import dynamia.com.core.data.repository.TransferReceiptRepository
import dynamia.com.core.data.repository.TransferShipmentRepository
import dynamia.com.core.util.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TransferInputViewModel(
    private val transferShipmentRepository: TransferShipmentRepository,
    private val transferReceiptRepository: TransferReceiptRepository,
    val sharedPreferences: SharedPreferences,
) : ViewModelBase(sharedPreferences) {

    private val _transferInputViewState = MutableLiveData<TransferInputViewState>()
    val transferInputViewState: LiveData<TransferInputViewState> by lazy { _transferInputViewState }
    private val _inputValidaton = MutableLiveData<InputValidation>()
    val inputValidation: LiveData<InputValidation> by lazy { _inputValidaton }

    private var transferLineData: TransferShipmentLine? = null
    private var transferHeaderData: TransferShipmentHeader? = null
    private var transferReceiptHeader: TransferReceiptHeader? = null

    fun getHistoryValueDetail(no: String) {
        viewModelScope.launch {
            io {
                transferShipmentRepository.getTransferInputHistory(no).collect {
                    ui {
                        _transferInputViewState.value =
                            TransferInputViewState.SuccessGetHistoryValue(it)
                    }
                }
            }
        }
    }

    fun getShipmentListLineValue(no: String, identifier: String) {
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

    fun getReceiptListLineValue(no: String, identifier: String) {
        viewModelScope.launch {
            try {
                _transferInputViewState.value =
                    TransferInputViewState.LoadingSearchPickingList(true)
                io {
                    transferReceiptRepository.getTransferHeaderDetail(no).collect {
                        transferReceiptHeader = it
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


    private fun insertTransferShipmentInput(qty: String, timeStamp: String) {
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
                                        TransferInputViewState.ErrorSaveData("QTY data melebihi batas yang di perbolehkan")
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

    private fun insertTransferReceiptInput(qty: String, timeStamp: String) {
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
                                        TransferInputViewState.ErrorSaveData("QTY data melebihi batas yang di perbolehkan")
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
        typesInput: TransferType,
    ) {
        if (barcode.isEmpty()) {
            _inputValidaton.value = InputValidation.BarcodeEmpty
        }
        if (qty.isEmpty()) {
            _inputValidaton.value = InputValidation.QtyEmpty
        }
        if (barcode.isNotEmpty() && qty.isNotEmpty()) {
            when (typesInput) {
                SHIPMENT -> insertTransferShipmentInput(qty,
                    "${getCurrentDate()}T${getCurrentTime()}")
                RECEIPT -> TODO()
            }


        }
    }

    fun deleteTransferEntry(id: Int) {
        viewModelScope.launch {
            try {
                io {
                    transferShipmentRepository.deleteTransferInput(id)
                    ui {
                        _transferInputViewState.value =
                            TransferInputViewState.SuccessDeleteData
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    TransferInputViewState.ErrorDeleteData(e.localizedMessage)
            }
        }
    }

    fun updateTransferEntry(id: Int, newQty: Int) {
        viewModelScope.launch {
            try {
                io {
                    transferShipmentRepository.updateTransferInputQty(id, newQty)
                        .collect { result ->
                            ui {
                                if (result) {
                                    _transferInputViewState.value =
                                        TransferInputViewState.SuccessUpdateData
                                } else {
                                    _transferInputViewState.value =
                                        TransferInputViewState.ErrorUpdateData("Qty has Reach maximum allowed, please change")
                                }
                            }
                        }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    TransferInputViewState.ErrorUpdateData(e.localizedMessage)
            }
        }
    }


    sealed class TransferInputViewState {
        class SuccessGetValue(val data: TransferShipmentLine) : TransferInputViewState()
        class SuccessGetHistoryValue(val data: TransferInputData) : TransferInputViewState()

        object SuccessSaveData : TransferInputViewState()
        class ErrorSaveData(val message: String) : TransferInputViewState()
        class LoadingSearchPickingList(val status: Boolean) : TransferInputViewState()

        object SuccessDeleteData : TransferInputViewState()
        class ErrorDeleteData(val message: String) : TransferInputViewState()

        object SuccessUpdateData : TransferInputViewState()
        class ErrorUpdateData(val message: String) : TransferInputViewState()

        class ErrorGetData(val message: String) : TransferInputViewState()
    }

    sealed class InputValidation {
        object BarcodeEmpty : InputValidation()
        object QtyEmpty : InputValidation()
    }
}
