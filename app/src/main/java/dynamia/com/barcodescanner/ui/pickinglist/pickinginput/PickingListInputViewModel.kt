package dynamia.com.barcodescanner.ui.pickinglist.pickinginput

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.model.PickingListLineValue
import dynamia.com.core.data.repository.PickingListRepository
import dynamia.com.core.util.io
import dynamia.com.core.util.ui
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
                io {
                    val data =
                        pickingListRepository.getAllPickingListLineFromInsert(partNo, pickingListNo)
                    ui {
                        _pickingInputViewState.value =
                            PickingInputViewState.SuccessGetValue(data.toMutableList())
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
                    PickingInputViewState.ErrorGetData(e.localizedMessage)
            }
        }
    }


    sealed class PickingInputViewState {
        class SuccessGetValue(val data: MutableList<PickingListLineValue>) : PickingInputViewState()
        class ErrorGetData(val message: String) : PickingInputViewState()
        class CheckSNResult(val boolean: Boolean) : PickingInputViewState()
    }
}
