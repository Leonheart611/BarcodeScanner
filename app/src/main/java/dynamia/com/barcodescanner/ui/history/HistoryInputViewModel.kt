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
import dynamia.com.core.data.repository.TransferShipmentRepository
import dynamia.com.core.util.io
import dynamia.com.core.util.ui
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HistoryInputViewModel(
    val transferShipmentRepository: TransferShipmentRepository,
    userRepository: SharedPreferences,
) : ViewModelBase(userRepository)
