package dynamia.com.barcodescanner.ui.login

import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.repository.PickingListRepository
import dynamia.com.core.data.repository.ReceiptImportRepository
import dynamia.com.core.data.repository.ReceiptLocalRepository
import dynamia.com.core.data.repository.UserRepository

class LoginViewModel(
    val pickingListRepository: PickingListRepository,
    val receiptImportRepository: ReceiptImportRepository,
    val receiptLocalRepository: ReceiptLocalRepository,
    userRepository: UserRepository
) : ViewModelBase(userRepository)
