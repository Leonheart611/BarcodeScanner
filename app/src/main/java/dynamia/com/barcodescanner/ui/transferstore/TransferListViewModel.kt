package dynamia.com.barcodescanner.ui.transferstore

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dynamia.com.barcodescanner.di.ViewModelBase
import dynamia.com.core.data.repository.InventoryRepository
import dynamia.com.core.data.repository.PurchaseOrderRepository
import dynamia.com.core.data.repository.TransferReceiptRepository
import dynamia.com.core.data.repository.TransferShipmentRepository
import dynamia.com.core.domain.ResultWrapper
import dynamia.com.core.util.io
import dynamia.com.core.util.sendError
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransferListViewModel @Inject constructor(
    val transferShipmentRepository: TransferShipmentRepository,
    val transferReceiptRepository: TransferReceiptRepository,
    val purchaseOrderRepository: PurchaseOrderRepository,
    val inventoryRepository: InventoryRepository,
    sharedPreferences: SharedPreferences,
) : ViewModelBase(sharedPreferences) {

    private var _viewState = MutableLiveData<TransferListViewState>()
    val transferViewState: LiveData<TransferListViewState> by lazy { _viewState }

    private val pager = MutableLiveData<Int>()

    val transferShipmentHeader = Transformations.switchMap(pager) {
        transferShipmentRepository.getAllTransferHeader(it)
    }

    val transferReceiptHeader = Transformations.switchMap(pager) {
        transferReceiptRepository.getAllTransferReceiptHeader(it)
    }

    val purchaseHeaderData = Transformations.switchMap(pager) {
        purchaseOrderRepository.getAllPurchaseOrderHeader(it)
    }

    val inventoryHeaderData = Transformations.switchMap(pager) {
        inventoryRepository.getAllInventoryHeader(it)
    }


    fun updatePager(int: Int) {
        pager.value = int
    }


    fun updateTransferShipment() {
        try {
            viewModelScope.launch {
                io {
                    _viewState.postValue(TransferListViewState.ShowLoading(true))
                    transferShipmentRepository.deleteAllTransferHeader()
                    transferShipmentRepository.getTransferShipmentHeaderAsync().collect {
                        _viewState.postValue(TransferListViewState.ShowLoading(false))
                        when (it) {
                            is ResultWrapper.GenericError -> {
                                _viewState.postValue(TransferListViewState.Error("${it.code} ${it.error}"))
                            }
                            is ResultWrapper.NetworkError -> {
                                _viewState.postValue(TransferListViewState.Error(it.error))
                            }
                            is ResultWrapper.Success -> {
                                it.value.forEach { data ->
                                    transferShipmentRepository.insertTransferHeader(data)
                                }
                                _viewState.postValue(TransferListViewState.SuccessUpdateData)
                            }
                            else -> {}
                        }
                    }
                }
            }
        } catch (e: Exception) {
            crashlytics.sendError(e)
            _viewState.postValue(TransferListViewState.ShowLoading(false))
            e.localizedMessage?.let {
                _viewState.postValue(TransferListViewState.Error(it))
            }
        }
    }

    sealed class TransferListViewState {
        class Error(val message: String) : TransferListViewState()
        class ShowLoading(val boolean: Boolean) : TransferListViewState()
        object SuccessUpdateData : TransferListViewState()
    }


}
