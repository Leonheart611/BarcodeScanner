package dynamia.com.barcodescanner.ui.pickinglist.pickinginput

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.repository.PickingListRepository

class PickingListInputViewModel(
    val pickingListRepository: PickingListRepository,
    sharedPreferences: SharedPreferences
) : ViewModelBase(sharedPreferences) {



}
