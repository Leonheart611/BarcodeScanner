package dynamia.com.barcodescanner.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dynamia.com.barcodescanner.data.repository.PickingListRepository
import dynamia.com.barcodescanner.data.repository.ReceiptImportRepository
import dynamia.com.barcodescanner.data.repository.ReceiptLocalRepository

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
