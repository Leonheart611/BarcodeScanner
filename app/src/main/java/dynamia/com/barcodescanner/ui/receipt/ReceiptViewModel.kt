package dynamia.com.barcodescanner.ui.receipt

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dynamia.com.barcodescanner.domain.RetrofitBuilder
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.model.ReceiptImportScanEntriesValue
import dynamia.com.core.data.repository.ReceiptImportRepository
import dynamia.com.core.data.repository.ReceiptLocalRepository
import dynamia.com.core.util.Constant
import dynamia.com.core.util.Event
import kotlinx.coroutines.launch

class ReceiptViewModel(
    val receiptImportRepository: ReceiptImportRepository,
    val receiptLocalRepository: ReceiptLocalRepository,
    val sharedPreferences: SharedPreferences
) : ViewModelBase(sharedPreferences) {

}
