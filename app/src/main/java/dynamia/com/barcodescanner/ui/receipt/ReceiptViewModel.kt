package dynamia.com.barcodescanner.ui.receipt

import android.content.SharedPreferences
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.repository.ReceiptImportRepository
import dynamia.com.core.data.repository.ReceiptLocalRepository

class ReceiptViewModel(
    val receiptImportRepository: ReceiptImportRepository,
    val receiptLocalRepository: ReceiptLocalRepository,
    val sharedPreferences: SharedPreferences
) : ViewModelBase(sharedPreferences)
