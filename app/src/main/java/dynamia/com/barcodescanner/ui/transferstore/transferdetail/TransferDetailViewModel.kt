package dynamia.com.barcodescanner.ui.transferstore.transferdetail

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

class TransferDetailViewModel(
    val transferShipmentRepository: TransferShipmentRepository,
    val sharedPreferences: SharedPreferences,
) : ViewModelBase(sharedPreferences) {

    private val _pickingDetailViewState = MutableLiveData<TransferListViewState>()
    val transferListViewState: LiveData<TransferListViewState> by lazy { _pickingDetailViewState }

    private val _pickingPostViewState = MutableLiveData<PickingDetailPostViewState>()
    val pickingPostViewState: LiveData<PickingDetailPostViewState> by lazy { _pickingPostViewState }

    private val _transferInputViewState =
        MutableLiveData<TransferDetailInputViewState>()
    val transferInputViewState: LiveData<TransferDetailInputViewState> by lazy { _transferInputViewState }

    private var transferLineData: TransferShipmentLine? = null
    private var transferHeaderData: TransferShipmentHeader? = null


    fun getPickingListLineValue(no: String, identifier: String) {
        viewModelScope.launch {
            try {
                io {
                    transferShipmentRepository.getTransferHeaderDetail(no).collect {
                        transferHeaderData = it
                    }
                    transferShipmentRepository.getLineDetailFromBarcode(no, identifier)
                        .collect { data ->
                            ui {
                                transferLineData = data
                                insertTransferInput("1", "${getCurrentDate()}T${getCurrentTime()}")
                            }
                        }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    TransferDetailInputViewState.ErrorGetData(e.localizedMessage)
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
                                        TransferDetailInputViewState.SuccessSaveData
                                } else {
                                    _transferInputViewState.value =
                                        TransferDetailInputViewState.ErrorSaveData
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
                _transferInputViewState.value =
                    TransferDetailInputViewState.ErrorGetData(e.localizedMessage)
            }

        }
    }


    fun postPickingDataNew() {
        viewModelScope.launch {
            try {
                var dataPosted = 0
                io {
                    val listEntries =
                        transferShipmentRepository.getAllUnsycnTransferInput()
                    ui {
                        _pickingPostViewState.value =
                            PickingDetailPostViewState.GetUnpostedData(listEntries.size)
                        _pickingPostViewState.value =
                            PickingDetailPostViewState.GetSuccessfullyPostedData(dataPosted)
                    }
                    for (data in listEntries) {
                        val param = gson.toJson(data)
                        transferShipmentRepository.postTransferData(param).collect {
                            dataPosted++
                            ui {
                                _pickingPostViewState.value =
                                    PickingDetailPostViewState.GetSuccessfullyPostedData(dataPosted)
                            }
                            data.apply {
                                sycn_status = true
                            }
                            transferShipmentRepository.updateTransferInput(data)
                        }
                    }
                    ui { _pickingPostViewState.value = PickingDetailPostViewState.AllDataPosted }
                }
            } catch (e: Exception) {
                _pickingPostViewState.value =
                    PickingDetailPostViewState.ErrorPostData(e.localizedMessage)
            }
        }
    }


    fun getTransferDetail(no: String) {
        viewModelScope.launch {
            try {
                io {
                    transferShipmentRepository.getTransferHeaderDetail(no).collect { data ->
                        ui {
                            _pickingDetailViewState.value =
                                TransferListViewState.SuccessGetLocalData(data)
                        }
                    }
                }
            } catch (e: Exception) {
                _pickingDetailViewState.value =
                    TransferListViewState.ErrorGetLocalData(e.localizedMessage)
            }
        }

    }

    fun getTransferLine(no: String) {
        viewModelScope.launch {
            try {
                io {
                    transferShipmentRepository.getLineListFromHeader(no).collect {
                        ui {
                            _pickingDetailViewState.value =
                                TransferListViewState.SuccessGetPickingLineData(it.toMutableList())
                        }
                    }
                }
            } catch (e: Exception) {
                _pickingDetailViewState.value =
                    TransferListViewState.ErrorGetLocalData(e.localizedMessage)
            }

        }
    }

    sealed class TransferListViewState {
        class SuccessGetLocalData(val value: TransferShipmentHeader) : TransferListViewState()
        class SuccessGetPickingLineData(val values: MutableList<TransferShipmentLine>) :
            TransferListViewState()

        class ErrorGetLocalData(val message: String) : TransferListViewState()
    }

    sealed class PickingDetailPostViewState {
        class GetUnpostedData(val data: Int) : PickingDetailPostViewState()
        class GetSuccessfullyPostedData(val data: Int) : PickingDetailPostViewState()
        class ErrorPostData(val message: String) : PickingDetailPostViewState()
        object AllDataPosted : PickingDetailPostViewState()
    }

    sealed class TransferDetailInputViewState {
        object SuccessSaveData : TransferDetailInputViewState()
        object ErrorSaveData : TransferDetailInputViewState()
        class ErrorGetData(val message: String) : TransferDetailInputViewState()
    }


}
