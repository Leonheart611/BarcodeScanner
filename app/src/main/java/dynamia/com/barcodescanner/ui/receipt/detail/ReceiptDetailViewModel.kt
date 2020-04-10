package dynamia.com.barcodescanner.ui.receipt.detail

import android.content.SharedPreferences
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.repository.ReceiptImportRepository
import dynamia.com.core.data.repository.ReceiptLocalRepository

class ReceiptDetailViewModel(
    val receiptLocalRepository: ReceiptLocalRepository,
    val receiptImportRepository: ReceiptImportRepository,
    val sharedPreferences: SharedPreferences
) : ViewModelBase(sharedPreferences) {

}
