package dynamia.com.barcodescanner.ui.pickinglist.pickinginput

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.model.PickingListLineValue
import dynamia.com.core.data.model.PickingListScanEntriesValue
import dynamia.com.core.data.repository.PickingListRepository
import dynamia.com.core.util.io
import dynamia.com.core.util.ui
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PickingListInputViewModel(
    val pickingListRepository: PickingListRepository,
    sharedPreferences: SharedPreferences
) : ViewModelBase(sharedPreferences) {

    private val _pickingInputViewState = MutableLiveData<PickingInputViewState>()
    val pickingInputViewState: LiveData<PickingInputViewState> by lazy { _pickingInputViewState }

    fun getPickingListLineValue(partNo: String, pickingListNo: String) {
        viewModelScope.launch {
            try {
                _pickingInputViewState.value = PickingInputViewState.LoadingSearchPickingList(true)
                io {
                    val data =
                        pickingListRepository.getAllPickingListLineFromInsert(partNo, pickingListNo)
                    ui {
                        _pickingInputViewState.value =
                            PickingInputViewState.SuccessGetValue(data.toMutableList())
                        _pickingInputViewState.value =
                            PickingInputViewState.LoadingSearchPickingList(false)
                    }
                }
            } catch (e: Exception) {
                _pickingInputViewState.value =
                    PickingInputViewState.ErrorGetData(e.localizedMessage)
            }
        }
    }

    fun checkSn(serialNo: String) {
        viewModelScope.launch {
            try {
                io {
                    val result = pickingListRepository.checkPickingListNoandSN(serialNo)
                    ui {
                        _pickingInputViewState.value = PickingInputViewState.CheckSNResult(result)
                    }
                }
            } catch (e: Exception) {
                _pickingInputViewState.value =
                    PickingInputViewState.ErrorGetData(e.message ?: "")
            }
        }
    }

    fun getPickinglistHistory() {
        viewModelScope.launch {
            io {
                pickingListRepository.getAllPickingListScanEntries().collect { data ->
                    ui {
                        _pickingInputViewState.value =
                            PickingInputViewState.SuccessGetHistoryValue(data.toMutableList())
                    }
                }
            }
        }
    }


    sealed class PickingInputViewState {
        class SuccessGetValue(val data: MutableList<PickingListLineValue>) : PickingInputViewState()
        class SuccessGetHistoryValue(val data: MutableList<PickingListScanEntriesValue>) :
            PickingInputViewState()

        class LoadingSearchPickingList(val status: Boolean) : PickingInputViewState()

        class ErrorGetData(val message: String) : PickingInputViewState()
        class CheckSNResult(val boolean: Boolean) : PickingInputViewState()
    }
}
