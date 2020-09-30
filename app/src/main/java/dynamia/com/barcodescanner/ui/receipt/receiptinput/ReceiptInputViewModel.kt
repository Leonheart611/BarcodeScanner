package dynamia.com.barcodescanner.ui.receipt.receiptinput

import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.repository.ReceiptImportRepository
import dynamia.com.core.data.repository.ReceiptLocalRepository
import dynamia.com.core.data.repository.UserRepository

class ReceiptInputViewModel(
    sharedPreferences: UserRepository,
    val receiptLocalRepository: ReceiptLocalRepository,
    val receiptImportRepository: ReceiptImportRepository
) : ViewModelBase(sharedPreferences)
