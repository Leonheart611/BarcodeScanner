package dynamia.com.barcodescanner.ui.receipt.receiptinput

import android.content.SharedPreferences
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.repository.ReceiptImportRepository
import dynamia.com.core.data.repository.ReceiptLocalRepository

class ReceiptInputViewModel(
    sharedPreferences: SharedPreferences,
    val receiptLocalRepository: ReceiptLocalRepository,
    val receiptImportRepository: ReceiptImportRepository
) : ViewModelBase(sharedPreferences) {

}
