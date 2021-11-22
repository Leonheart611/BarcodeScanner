package dynamia.com.barcodescanner.ui.peminjaman.detail

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.model.PeminjamanHeader
import dynamia.com.core.data.repository.DorPickingRepository
import dynamia.com.core.data.repository.NetworkRepository
import dynamia.com.core.data.repository.PeminjamRepository
import dynamia.com.core.util.io
import dynamia.com.core.util.ui
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PeminjamDetailViewModel(
	private val peminjamanRepository: PeminjamRepository,
	private val dorPickingRepository: DorPickingRepository,
	sharedPreferences: SharedPreferences,
	private val networkRepository: NetworkRepository
) : ViewModelBase(sharedPreferences) {
	
	private val _pickingPostViewState = MutableLiveData<PostViewState>()
	val pickingPostViewState: LiveData<PostViewState> by lazy { _pickingPostViewState }
	
	private val documentNo = MutableLiveData<String>()
	
	fun updateDocumentNo(string: String) {
		documentNo.value = string
	}
	
	val peminjamanHeader: LiveData<PeminjamanHeader> = Transformations.switchMap(documentNo) {
		peminjamanRepository.getPeminjamHeaderDetail(it)
	}
	
	val peminjamDetailList = Transformations.switchMap(documentNo) {
		peminjamanRepository.getPeminjamanDetailData(it)
	}
	
	val dorHeader = Transformations.switchMap(documentNo) {
		dorPickingRepository.getDorHeaderDetail(it)
	}
	
	val dorDetailList = Transformations.switchMap(documentNo) {
		dorPickingRepository.getAllDorDetail(it)
	}
	
	fun postPeminjamDataNew() {
		viewModelScope.launch {
			try {
				var dataPosted = 0
				io {
					val pickingListEntries =
						peminjamanRepository.getPeminjamanUnpostedList()
					ui {
						_pickingPostViewState.value =
							PostViewState.GetUnpostedData(pickingListEntries.size)
						_pickingPostViewState.value =
							PostViewState.GetSuccessfullyPostedData(dataPosted)
					}
					for (data in pickingListEntries) {
						val param = gson.toJson(data)
						networkRepository.postPeminjamEntryAsync(param).collect {
							dataPosted++
							ui {
								_pickingPostViewState.value =
									PostViewState.GetSuccessfullyPostedData(dataPosted)
							}
							data.apply {
								sycn_status = true
							}
							peminjamanRepository.updatePeminjamScanEntries(data)
						}
					}
					ui { _pickingPostViewState.value = PostViewState.AllDataPosted }
				}
			} catch (e: Exception) {
				_pickingPostViewState.value =
					PostViewState.ErrorPostData(e.localizedMessage)
			}
		}
	}
	
	sealed class PostViewState {
		class GetUnpostedData(val data: Int) : PostViewState()
		class GetSuccessfullyPostedData(val data: Int) : PostViewState()
		class ErrorPostData(val message: String) : PostViewState()
		object AllDataPosted : PostViewState()
	}
	
	
}
