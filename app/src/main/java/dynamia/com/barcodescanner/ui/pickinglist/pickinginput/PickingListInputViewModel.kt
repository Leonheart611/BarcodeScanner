package dynamia.com.barcodescanner.ui.pickinglist.pickinginput

import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.repository.PickingListRepository
import dynamia.com.core.data.repository.UserRepository

class PickingListInputViewModel(
    val pickingListRepository: PickingListRepository,
    sharedPreferences: UserRepository
) : ViewModelBase(sharedPreferences)
