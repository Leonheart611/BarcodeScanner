package dynamia.com.barcodescanner.ui.receipt

import androidx.lifecycle.ViewModel
import dynamia.com.core.data.repository.ReceiptImportRepository
import dynamia.com.core.data.repository.ReceiptLocalRepository

class ReceiptViewModel(
    val receiptImportRepository: ReceiptImportRepository,
    val receiptLocalRepository: ReceiptLocalRepository
) : ViewModel() {

}
