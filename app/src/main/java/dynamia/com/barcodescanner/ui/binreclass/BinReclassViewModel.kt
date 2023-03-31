package dynamia.com.barcodescanner.ui.binreclass

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dynamia.com.barcodescanner.di.ViewModelBase
import dynamia.com.core.data.repository.BinreclassRepository
import dynamia.com.core.util.Event
import dynamia.com.core.util.io
import dynamia.com.core.util.sendError
import dynamia.com.core.util.ui
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BinReclassViewModel @Inject constructor(
    val binreclassRepository: BinreclassRepository,
    sharedPreferences: SharedPreferences,
) : ViewModelBase(sharedPreferences) {
    private val _viewState = MutableLiveData<Event<BinReclassInputState>>()
    val viewState: LiveData<Event<BinReclassInputState>> by lazy { _viewState }

    fun insertBinReclass(binFrom: String, binTo: String) {
        viewModelScope.launch {
            try {
                io {
                    binreclassRepository.checkBinFromAndBinToCode(binFrom, binTo).collect {
                        if (it) {
                            ui {
                                _viewState.value =
                                    Event(BinReclassInputState.OnSuccessSave(binFrom, binTo))
                            }
                        } else {
                            ui {
                                _viewState.value =
                                    Event(BinReclassInputState.OnFailedSave("Bin To and From Code Sudah ada"))
                            }
                        }
                    }

                }
            } catch (e: Exception) {
                crashlytics.sendError(e)
                e.localizedMessage?.let {
                    _viewState.value = Event(BinReclassInputState.OnFailedSave(it))
                }
            }
        }
    }

    sealed class BinReclassInputState {
        class OnSuccessSave(val binFrom: String, val binTo: String) : BinReclassInputState()
        class OnFailedSave(val message: String) : BinReclassInputState()
    }

}