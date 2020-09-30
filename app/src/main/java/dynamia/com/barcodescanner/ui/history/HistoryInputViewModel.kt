package dynamia.com.barcodescanner.ui.history

import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.repository.PickingListRepository
import dynamia.com.core.data.repository.ReceiptImportRepository
import dynamia.com.core.data.repository.ReceiptLocalRepository
import dynamia.com.core.data.repository.UserRepository

class HistoryInputViewModel(
    val pickingListRepository: PickingListRepository,
    val receiptImportRepository: ReceiptImportRepository,
    val receiptLocalRepository: ReceiptLocalRepository,
    userRepository: UserRepository
) : ViewModelBase(userRepository)
