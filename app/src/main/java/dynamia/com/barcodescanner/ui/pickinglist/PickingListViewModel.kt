package dynamia.com.barcodescanner.ui.pickinglist

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.repository.PickingListRepository

class PickingListViewModel(
    val pickingListRepository: PickingListRepository,
    sharedPreferences: SharedPreferences
) : ViewModelBase(sharedPreferences) {

}
