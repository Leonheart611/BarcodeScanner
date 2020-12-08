package dynamia.com.barcodescanner.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dynamia.com.core.data.model.PickingListLineValue
import dynamia.com.core.data.repository.PickingListRepository
import dynamia.com.core.data.repository.ReceiptImportRepository
import dynamia.com.core.data.repository.ReceiptLocalRepository
import dynamia.com.core.util.io
import dynamia.com.core.util.ui
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchViewModel(
    val pickingListRepository: PickingListRepository,
    val receiptLocalRepository: ReceiptLocalRepository,
    val receiptImportRepository: ReceiptImportRepository
) : ViewModel() {

    private val _searchViewState = MutableLiveData<SearchViewState>()
    val searchViewState: LiveData<SearchViewState> by lazy { _searchViewState }

    fun getPickingLineData(PoNo: String) {
        viewModelScope.launch {
            try {
                io {
                    pickingListRepository.getAllPickingListLine(PoNo).collect {
                        ui {
                            _searchViewState.value =
                                SearchViewState.SuccessGetPickingLine(it.toMutableList())
                        }
                    }
                }
            } catch (e: Exception) {
                _searchViewState.value = SearchViewState.ErrorGetLocalData(e.localizedMessage)
            }

        }
    }

    sealed class SearchViewState {
        class SuccessGetPickingLine(val data: MutableList<PickingListLineValue>) : SearchViewState()

        class ErrorGetLocalData(val message: String) : SearchViewState()
    }
}