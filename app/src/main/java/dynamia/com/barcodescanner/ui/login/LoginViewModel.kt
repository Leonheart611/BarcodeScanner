package dynamia.com.barcodescanner.ui.login

import androidx.lifecycle.ViewModel
import dynamia.com.barcodescanner.data.repository.PickingListRepository
import dynamia.com.barcodescanner.data.repository.ReceiptImportRepository
import dynamia.com.barcodescanner.data.repository.ReceiptLocalRepository

class LoginViewModel(
    val pickingListRepository: PickingListRepository,
    val receiptImportRepository: ReceiptImportRepository,
    val receiptLocalRepository: ReceiptLocalRepository
) : ViewModel() {

}
