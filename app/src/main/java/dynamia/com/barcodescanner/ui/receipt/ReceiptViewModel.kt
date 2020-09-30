package dynamia.com.barcodescanner.ui.receipt

import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.repository.ReceiptImportRepository
import dynamia.com.core.data.repository.ReceiptLocalRepository
import dynamia.com.core.data.repository.UserRepository

class ReceiptViewModel(
    val receiptImportRepository: ReceiptImportRepository,
    val receiptLocalRepository: ReceiptLocalRepository,
    val sharedPreferences: UserRepository
) : ViewModelBase(sharedPreferences)
