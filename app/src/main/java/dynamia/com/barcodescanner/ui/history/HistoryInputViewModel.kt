package dynamia.com.barcodescanner.ui.history

import android.content.SharedPreferences
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.repository.PickingListRepository

class HistoryInputViewModel(
    val pickingListRepository: PickingListRepository,
    sharedPreferences: SharedPreferences
) : ViewModelBase(sharedPreferences) {

    fun deleteScanEntries(id:Int){
        
    }
}
