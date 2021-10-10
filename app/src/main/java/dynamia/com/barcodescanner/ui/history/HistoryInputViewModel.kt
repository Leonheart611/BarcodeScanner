package dynamia.com.barcodescanner.ui.history

import android.content.SharedPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dynamia.com.barcodescanner.di.ViewModelBase
import dynamia.com.core.data.repository.*
import javax.inject.Inject

@HiltViewModel
class HistoryInputViewModel @Inject constructor(
    val transferShipmentRepository: TransferShipmentRepository,
    val transferReceiptRepository: TransferReceiptRepository,
    val purchaseOrderRepository: PurchaseOrderRepository,
    val stockOpnameRepository: StockOpnameRepository,
    val inventoryRepository: InventoryRepository,
    userRepository: SharedPreferences,
) : ViewModelBase(userRepository)
