package dynamia.com.barcodescanner.ui.peminjaman

import android.content.SharedPreferences
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.repository.DorPickingRepository
import dynamia.com.core.data.repository.PeminjamRepository

class PeminjamanListViewModel(
	val repository: PeminjamRepository,
	val dorPickingRepository: DorPickingRepository,
	sharedPreferences: SharedPreferences
) : ViewModelBase(sharedPreferences)
