package dynamia.com.barcodescanner.ui.search

import androidx.lifecycle.ViewModel
import dynamia.com.core.data.repository.PickingListRepository
import dynamia.com.core.data.repository.ReceiptImportRepository
import dynamia.com.core.data.repository.ReceiptLocalRepository

class SearchViewModel(
    val pickingListRepository: PickingListRepository,
    val receiptLocalRepository: ReceiptLocalRepository,
    val receiptImportRepository: ReceiptImportRepository
) : ViewModel() {

}