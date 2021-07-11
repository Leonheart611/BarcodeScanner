package dynamia.com.barcodescanner.ui.history

import android.content.SharedPreferences
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.repository.PurchaseOrderRepository
import dynamia.com.core.data.repository.StockOpnameRepository
import dynamia.com.core.data.repository.TransferReceiptRepository
import dynamia.com.core.data.repository.TransferShipmentRepository

class HistoryInputViewModel(
    val transferShipmentRepository: TransferShipmentRepository,
    val transferReceiptRepository: TransferReceiptRepository,
    val purchaseOrderRepository: PurchaseOrderRepository,
    val stockOpnameRepository: StockOpnameRepository,
    userRepository: SharedPreferences,
) : ViewModelBase(userRepository)
