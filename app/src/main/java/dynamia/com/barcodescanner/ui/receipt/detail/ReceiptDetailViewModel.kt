package dynamia.com.barcodescanner.ui.receipt.detail

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.repository.NetworkRepository
import dynamia.com.core.data.repository.ReceiptImportRepository
import dynamia.com.core.data.repository.ReceiptLocalRepository
import dynamia.com.core.util.io
import dynamia.com.core.util.ui
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ReceiptDetailViewModel(
    val receiptLocalRepository: ReceiptLocalRepository,
    val receiptImportRepository: ReceiptImportRepository,
    sharedPreferences: SharedPreferences,
    private val networkRepository: NetworkRepository
) : ViewModelBase(sharedPreferences) {

    private val _receiptPostViewState = MutableLiveData<ReceiptPostViewState>()
    val receiptPostViewState: LiveData<ReceiptPostViewState> by lazy { _receiptPostViewState }

    fun postReceiptLocalNew() {
        viewModelScope.launch {
            try {
                var dataPosted = 0
                io {
                    val receiptLocalList = receiptLocalRepository.getUnsycnReceiptLocalScanEntry()
                    ui {
                        _receiptPostViewState.value =
                            ReceiptPostViewState.GetUnpostedReceipt(receiptLocalList.size)
                        _receiptPostViewState.value =
                            ReceiptPostViewState.GetSuccessfulPosted(dataPosted)
                    }
                    for (data in receiptLocalList) {
                        val param = gson.toJson(data)
                        networkRepository.postReceiptLocalEntry(param).collect {
                            dataPosted++
                            ui {
                                _receiptPostViewState.value =
                                    ReceiptPostViewState.GetSuccessfulPosted(dataPosted)
                            }
                            data.apply {
                                sycn_status = true
                            }
                            receiptLocalRepository.updateReceiptLocalScanEntry(data)
                        }
                    }
                }
                ui { _receiptPostViewState.value = ReceiptPostViewState.SuccessPostallData }
            } catch (e: Exception) {
                _receiptPostViewState.value =
                    ReceiptPostViewState.ErrorReceiptPost(e.localizedMessage)
            }
        }
    }

    fun postReceiptImportNew() {
        viewModelScope.launch {
            try {
                var dataPosted = 0
                io {
                    val receiptImportList = receiptImportRepository.getAllUnsycnImportScanEntry()
                    ui {
                        _receiptPostViewState.value =
                            ReceiptPostViewState.GetUnpostedReceipt(receiptImportList.size)
                        _receiptPostViewState.value =
                            ReceiptPostViewState.GetSuccessfulPosted(dataPosted)
                    }
                    for (data in receiptImportList) {
                        val param = gson.toJson(data)
                        networkRepository.postReceiptImportEntry(param).collect {
                            dataPosted++
                            ui {
                                _receiptPostViewState.value =
                                    ReceiptPostViewState.GetSuccessfulPosted(dataPosted)
                            }
                            data.apply {
                                sycn_status = true
                            }
                            receiptImportRepository.updateReceiptImportScanEntry(data)
                        }
                    }
                }
                ui { _receiptPostViewState.value = ReceiptPostViewState.SuccessPostallData }
            } catch (e: Exception) {
                _receiptPostViewState.value =
                    ReceiptPostViewState.ErrorReceiptPost(e.localizedMessage)
            }
        }
    }

    sealed class ReceiptPostViewState {
        class GetUnpostedReceipt(val data: Int) : ReceiptPostViewState()
        class GetSuccessfulPosted(val data: Int) : ReceiptPostViewState()
        class ErrorReceiptPost(val message: String) : ReceiptPostViewState()
        object SuccessPostallData : ReceiptPostViewState()
    }
}
