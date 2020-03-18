package dynamia.com.barcodescanner.ui.home

import androidx.lifecycle.ViewModel
import dynamia.com.core.data.repository.PickingListRepository
import dynamia.com.core.data.repository.ReceiptImportRepository
import dynamia.com.core.data.repository.ReceiptLocalRepository

class HomeViewModel(
    val pickingListRepository: PickingListRepository,
    val receiptImportRepository: ReceiptImportRepository,
    val receiptLocalRepository: ReceiptLocalRepository
) : ViewModel() {

    fun clearAllDB(){
        pickingListRepository.clearPickingListHeader()
        pickingListRepository.clearPickingListLine()
        pickingListRepository.clearPickingListScanEntries()

        receiptImportRepository.clearReceiptImportHeader()
        receiptImportRepository.clearReceiptImportLine()
        receiptImportRepository.clearReceiptImportScanEntries()

        receiptLocalRepository.clearReceiptLocalHeader()
        receiptLocalRepository.clearReceiptLocalLine()
        receiptLocalRepository.clearReceiptLocalScanEntries()
    }

}
