package dynamia.com.barcodescanner.ui.pickinglist.pickingdetail

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.model.PickingListHeaderValue
import dynamia.com.core.data.model.PickingListLineValue
import dynamia.com.core.data.repository.NetworkRepository
import dynamia.com.core.data.repository.PickingListRepository
import dynamia.com.core.util.io
import dynamia.com.core.util.ui
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PickingDetailViewModel(
    val pickingListRepository: PickingListRepository,
    sharedPreferences: SharedPreferences,
    private val networkRepository: NetworkRepository,
    val app: Application
) : ViewModelBase(sharedPreferences) {

    private val _pickingDetailViewState = MutableLiveData<PickingDetailViewState>()
    val pickingDetailViewState: LiveData<PickingDetailViewState> by lazy { _pickingDetailViewState }

    private val _pickingPostViewState = MutableLiveData<PickingDetailPostViewState>()
    val pickingPostViewState: LiveData<PickingDetailPostViewState> by lazy { _pickingPostViewState }


    fun postPickingDataNew() {
        viewModelScope.launch {
            try {
                var dataPosted = 0
                io {
                    val pickingListEntries =
                        pickingListRepository.getAllUnscynPickingListScanEntries()
                    ui {
                        _pickingPostViewState.value =
                            PickingDetailPostViewState.GetUnpostedData(pickingListEntries.size)
                        _pickingPostViewState.value =
                            PickingDetailPostViewState.GetSuccessfullyPostedData(dataPosted)
                    }
                    for (data in pickingListEntries) {
                        val param = gson.toJson(data)
                        networkRepository.postPickingListEntry(param).collect {
                            dataPosted++
                            ui {
                                _pickingPostViewState.value =
                                    PickingDetailPostViewState.GetSuccessfullyPostedData(dataPosted)
                            }
                            data.apply {
                                sycn_status = true
                            }
                            pickingListRepository.updatePickingScanEntry(data)
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


    fun getPickingDetail(listNo: String) {
        viewModelScope.launch {
            try {
                io {
                    val data = pickingListRepository.getPickingListHeader(listNo)
                    ui {
                        _pickingDetailViewState.value =
                            PickingDetailViewState.SuccessGetLocalData(data)
                    }
                }
            } catch (e: Exception) {
                _pickingDetailViewState.value =
                    PickingDetailViewState.ErrorGetLocalData(e.localizedMessage)
            }
        }

    }

    fun getPickingListLine(pickingListNo: String) {
        viewModelScope.launch {
            try {
                io {
                    pickingListRepository.getAllPickingListLine(pickingListNo).collect {
                        ui {
                            _pickingDetailViewState.value =
                                PickingDetailViewState.SuccessGetPickingLineData(it.toMutableList())
                        }
                    }
                }
            } catch (e: Exception) {
                _pickingDetailViewState.value =
                    PickingDetailViewState.ErrorGetLocalData(e.localizedMessage)
            }

        }
    }

    sealed class PickingDetailViewState {
        class SuccessGetLocalData(val value: PickingListHeaderValue) : PickingDetailViewState()
        class SuccessGetPickingLineData(val values: MutableList<PickingListLineValue>) :
            PickingDetailViewState()

        class ErrorGetLocalData(val message: String) : PickingDetailViewState()
    }

    sealed class PickingDetailPostViewState {
        class GetUnpostedData(val data: Int) : PickingDetailPostViewState()
        class GetSuccessfullyPostedData(val data: Int) : PickingDetailPostViewState()
        class ErrorPostData(val message: String) : PickingDetailPostViewState()
        object AllDataPosted : PickingDetailPostViewState()
    }


}
