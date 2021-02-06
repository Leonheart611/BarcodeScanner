package dynamia.com.barcodescanner.ui.history

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.model.PickingListScanEntriesValue
import dynamia.com.core.data.repository.PickingListRepository
import dynamia.com.core.data.repository.ReceiptImportRepository
import dynamia.com.core.data.repository.ReceiptLocalRepository
import dynamia.com.core.util.io
import dynamia.com.core.util.ui
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HistoryInputViewModel(
    val pickingListRepository: PickingListRepository,
    val receiptImportRepository: ReceiptImportRepository,
    val receiptLocalRepository: ReceiptLocalRepository,
    userRepository: SharedPreferences
) : ViewModelBase(userRepository) {

    private val _historyInputViewState = MutableLiveData<HistoryInputViewState>()
    val historyInputViewState: LiveData<HistoryInputViewState> by lazy { _historyInputViewState }


    fun getPickinglistHistory() {
        viewModelScope.launch {
            io {
                pickingListRepository.getAllPickingListScanEntries().collect { data ->
                    ui {
                        _historyInputViewState.value =
                            HistoryInputViewState.SuccessGetPicklistHistory(data.toMutableList())
                    }
                }
            }
        }
    }


    sealed class HistoryInputViewState {
        class SuccessGetPicklistHistory(val data: MutableList<PickingListScanEntriesValue>) :
            HistoryInputViewState()

        class ErrorGetData(val message: String) : HistoryInputViewState()
        class CheckSNResult(val boolean: Boolean) : HistoryInputViewState()
    }
}
