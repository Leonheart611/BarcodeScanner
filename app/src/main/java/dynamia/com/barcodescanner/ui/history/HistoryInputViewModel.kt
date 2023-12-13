package dynamia.com.barcodescanner.ui.history

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import dagger.hilt.android.lifecycle.HiltViewModel
import dynamia.com.barcodescanner.di.ViewModelBase
import dynamia.com.core.data.repository.InventoryRepository
import dynamia.com.core.data.repository.PurchaseOrderRepository
import dynamia.com.core.data.repository.StockOpnameRepository
import dynamia.com.core.data.repository.TransferReceiptRepository
import dynamia.com.core.data.repository.TransferShipmentRepository
import dynamia.com.core.util.addConcatQuery
import javax.inject.Inject

@HiltViewModel
class HistoryInputViewModel @Inject constructor(
    val transferShipmentRepository: TransferShipmentRepository,
    val transferReceiptRepository: TransferReceiptRepository,
    val purchaseOrderRepository: PurchaseOrderRepository,
    val stockOpnameRepository: StockOpnameRepository,
    val inventoryRepository: InventoryRepository,
    userRepository: SharedPreferences,
) : ViewModelBase(userRepository) {
    private val query = MutableLiveData<String>()
    var documentNo: String? = null
    var inputValidate: Boolean = true


    val stockOpnameInputSearch = query.switchMap {
        if (it.isBlank()) {
            documentNo?.let { documentNo ->
                stockOpnameRepository.getAllInputStockOpnameByDocumentNo(documentNo)
            } ?: kotlin.run {
                stockOpnameRepository.getAllInputStockOpname()
            }
        } else {
            stockOpnameRepository.getAllStockOpnameByItemNo(it.addConcatQuery(), documentNo)
        }
    }

    fun updateQuery(itemNo: String) {
        query.value = itemNo
    }

    fun updateViewModelVariable(args: HistoryInputFragmentArgs) {
        query.value = ""
        documentNo = args.documentNo
        inputValidate = args.inputValidate
    }


}
