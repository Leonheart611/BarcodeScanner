package dynamia.com.barcodescanner.ui.receipt.receiptinput

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.model.ReceiptImportLineValue
import dynamia.com.core.data.model.ReceiptLocalLineValue
import dynamia.com.core.data.repository.ReceiptImportRepository
import dynamia.com.core.data.repository.ReceiptLocalRepository
import dynamia.com.core.util.io
import dynamia.com.core.util.ui
import kotlinx.coroutines.launch

class ReceiptInputViewModel(
    sharedPreferences: SharedPreferences,
    val receiptLocalRepository: ReceiptLocalRepository,
    val receiptImportRepository: ReceiptImportRepository
) : ViewModelBase(sharedPreferences) {

    private val _receiptInputViewState = MutableLiveData<ReceiptInputViewState>()
    val receiptInputViewState: LiveData<ReceiptInputViewState> by lazy { _receiptInputViewState }

    fun getReceiptLocalLine(documentNo: String, partNo: String) {
        viewModelScope.launch {
            try {
                _receiptInputViewState.value = ReceiptInputViewState.ShowLoading(true)
                io {
                    val result =
                        receiptLocalRepository.getReceiptLocalLineDetail(documentNo, partNo)
                    ui {
                        _receiptInputViewState.value =
                            ReceiptInputViewState.SuccessGetLocalLine(result.toMutableList())
                        _receiptInputViewState.value = ReceiptInputViewState.ShowLoading(false)
                    }
                }
            } catch (e: Exception) {
                e.localizedMessage?.let {
                    _receiptInputViewState.value = ReceiptInputViewState.ErrorGetReceiptLine(it)
                }
                _receiptInputViewState.value = ReceiptInputViewState.ShowLoading(false)
            }
        }
    }

    fun getReceiptImportLine(documentNo: String, partNo: String) {
        viewModelScope.launch {
            try {
                _receiptInputViewState.value = ReceiptInputViewState.ShowLoading(true)
                io {
                    val result = receiptImportRepository.getDetailImportLine(documentNo, partNo)
                    ui {
                        _receiptInputViewState.value =
                            ReceiptInputViewState.SuccessGetImportLine(result.toMutableList())
                        _receiptInputViewState.value = ReceiptInputViewState.ShowLoading(false)
                    }
                }
            } catch (e: Exception) {
                e.localizedMessage?.let {
                    _receiptInputViewState.value = ReceiptInputViewState.ErrorGetReceiptLine(it)
                }
                _receiptInputViewState.value = ReceiptInputViewState.ShowLoading(false)

            }
        }
    }

    fun checkLocalSN(serialNo: String) {
        viewModelScope.launch {
            try {
                io {
                    val result = receiptLocalRepository.checkSN(serialNo = serialNo)
                    ui {
                        _receiptInputViewState.value =
                            ReceiptInputViewState.CheckLocalSNResult(result)
                    }
                }
            } catch (e: Exception) {
                e.localizedMessage?.let {
                    _receiptInputViewState.value = ReceiptInputViewState.ErrorGetReceiptLine(it)
                }
                _receiptInputViewState.value = ReceiptInputViewState.ShowLoading(false)
            }
        }
    }

    fun checkImportSn(sn: String) {
        viewModelScope.launch {
            try {
                io {
                    val result = receiptImportRepository.checkSN(sn)
                    ui {
                        _receiptInputViewState.value =
                            ReceiptInputViewState.CheckImportSNResult(result)
                    }
                }
            } catch (e: Exception) {
                e.localizedMessage?.let {
                    _receiptInputViewState.value = ReceiptInputViewState.ErrorGetReceiptLine(it)
                }
            }
        }
    }


    sealed class ReceiptInputViewState {
        class SuccessGetImportLine(val data: MutableList<ReceiptImportLineValue>) :
            ReceiptInputViewState()

        class SuccessGetLocalLine(val data: MutableList<ReceiptLocalLineValue>) :
            ReceiptInputViewState()

        class CheckLocalSNResult(val result: Boolean) : ReceiptInputViewState()
        class CheckImportSNResult(val result: Boolean) : ReceiptInputViewState()
        class ErrorGetReceiptLine(val message: String) : ReceiptInputViewState()
        class ShowLoading(val loading: Boolean) : ReceiptInputViewState()
    }
}
