package dynamia.com.barcodescanner.ui.binreclass

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.repository.BinreclassRepository
import dynamia.com.core.util.io
import dynamia.com.core.util.ui
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class BinReclassViewModel(
    val binreclassRepository: BinreclassRepository,
    sharedPreferences: SharedPreferences,
) : ViewModelBase(sharedPreferences) {
    private val _viewState = MutableLiveData<BinReclassInputState>()
    val viewState: LiveData<BinReclassInputState> by lazy { _viewState }

    fun insertBinReclass(binFrom: String, binTo: String) {
        viewModelScope.launch {
            try {
                io {
                    binreclassRepository.checkBinFromAndBinToCode(binFrom, binTo).collect {
                        if (it) {
                            ui {
                                _viewState.value =
                                    BinReclassInputState.OnSuccessSave(binFrom, binTo)
                            }
                        } else {
                            ui {
                                _viewState.value =
                                    BinReclassInputState.OnFailedSave("Bin To and From Code Sudah ada")
                            }
                        }
                    }

                }
            } catch (e: Exception) {
                _viewState.value = BinReclassInputState.OnFailedSave(e.localizedMessage)
            }
        }
    }

    sealed class BinReclassInputState {
        class OnSuccessSave(val binFrom: String, val binTo: String) : BinReclassInputState()
        class OnFailedSave(val message: String) : BinReclassInputState()
    }

}