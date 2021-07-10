package dynamia.com.barcodescanner.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dynamia.com.core.data.model.PickingListLineValue

class SearchViewModel() : ViewModel() {

    private val _searchViewState = MutableLiveData<SearchViewState>()
    val searchViewState: LiveData<SearchViewState> by lazy { _searchViewState }

    fun getPickingLineData(PoNo: String) {

    }

    sealed class SearchViewState {
        class SuccessGetPickingLine(val data: MutableList<PickingListLineValue>) : SearchViewState()

        class ErrorGetLocalData(val message: String) : SearchViewState()
    }
}