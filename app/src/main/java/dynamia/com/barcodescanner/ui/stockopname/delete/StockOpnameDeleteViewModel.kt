package dynamia.com.barcodescanner.ui.stockopname.delete

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dynamia.com.core.data.repository.StockOpnameRepository
import dynamia.com.core.util.io
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockOpnameDeleteViewModel @Inject constructor(val stockOpnameRepository: StockOpnameRepository) :
    ViewModel() {

    private val query = MutableLiveData<String>()
    private val _deleteViewState = MutableLiveData<DeleteViewState>()
    val deleteViewState: LiveData<DeleteViewState> by lazy { _deleteViewState }

    val stockOpnameInputValue = Transformations.switchMap(query) {
        stockOpnameRepository.getAllInputStockOpnameByBox(it)
    }

    fun updateQuery(box: String) {
        query.value = box
    }

    fun deleteAllBox(box: String) {
        viewModelScope.launch {
            try {
                io {
                    stockOpnameRepository.deleteAllFromBox(box).collect {
                        if (it) {
                            _deleteViewState.postValue(DeleteViewState.Success)
                        }
                    }
                }
            } catch (e: Exception) {
                _deleteViewState.postValue(DeleteViewState.Error(e.localizedMessage))
            }
        }
    }

    sealed class DeleteViewState {
        object Success : DeleteViewState()
        class Error(val message: String) : DeleteViewState()
    }

}