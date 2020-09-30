package dynamia.com.barcodescanner.ui.pickinglist

import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.repository.PickingListRepository
import dynamia.com.core.data.repository.UserRepository

class PickingListViewModel(
    val pickingListRepository: PickingListRepository,
    sharedPreferences: UserRepository
) : ViewModelBase(sharedPreferences)
