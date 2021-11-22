package dynamia.com.barcodescanner.ui.pickinglist.pickinginput

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import dynamia.com.barcodescanner.ui.pickinglist.pickinginput.InputType.*
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.model.DorPickingDetail
import dynamia.com.core.data.model.PeminjamanDetail
import dynamia.com.core.data.model.PickingListLineValue
import dynamia.com.core.data.repository.DorPickingRepository
import dynamia.com.core.data.repository.PeminjamRepository
import dynamia.com.core.data.repository.PickingListRepository
import dynamia.com.core.util.io
import dynamia.com.core.util.ui
import kotlinx.coroutines.launch

class PickingListInputViewModel(
	val pickingListRepository: PickingListRepository,
	val peminjamanRepository: PeminjamRepository,
	val dorPickingRepository: DorPickingRepository,
	sharedPreferences: SharedPreferences
) : ViewModelBase(sharedPreferences) {
	class Param(val documentNo: String, val partNo: String)
	
	private val param = MutableLiveData<Param>()
	
	private val _pickingInputViewState = MutableLiveData<PickingInputViewState>()
	val pickingInputViewState: LiveData<PickingInputViewState> by lazy { _pickingInputViewState }
	
	private val _peminjamInputViewState = MutableLiveData<PeminjamanInputViewState>()
	val peminjamInputViewState: LiveData<PeminjamanInputViewState> by lazy { _peminjamInputViewState }
	
	private val _dorInputViewState = MutableLiveData<DorInputViewState>()
	val dorInputViewState: LiveData<DorInputViewState> by lazy { _dorInputViewState }
	
	var peminjamanDetailData: PeminjamanDetail? = null
	var pickListValue: PickingListLineValue? = null
	var dorDetailValue: DorPickingDetail? = null
	
	val peminjamInsertHistory = Transformations.switchMap(param) { data ->
		peminjamanRepository.getAllPeminjamScanEntriesHistory(data.documentNo, data.partNo)
	}
	
	fun updateHistoryParam(data: Param) {
		param.value = data
	}
	
	
	fun getPickingListLineValue(partNo: String, pickingListNo: String) {
		viewModelScope.launch {
			try {
				_pickingInputViewState.value = PickingInputViewState.LoadingSearchPickingList(true)
				io {
					val data =
						pickingListRepository.getAllPickingListLineFromInsert(partNo, pickingListNo)
					ui {
						_pickingInputViewState.value =
							PickingInputViewState.SuccessGetValue(data.toMutableList())
						_pickingInputViewState.value =
							PickingInputViewState.LoadingSearchPickingList(false)
					}
				}
			} catch (e: Exception) {
				_pickingInputViewState.postValue(PickingInputViewState.ErrorGetData(e.localizedMessage))
			}
		}
	}
	
	fun getPeminjamListLine(partNo: String, documentId: String) {
		viewModelScope.launch {
			try {
				_peminjamInputViewState.value =
					PeminjamanInputViewState.LoadingSearchPeminjaman(true)
				io {
					val data =
						peminjamanRepository.getPeminjamanDetail(documentId, partNo)
					ui {
						_peminjamInputViewState.value =
							PeminjamanInputViewState.SuccessGetValue(data.toMutableList())
						_peminjamInputViewState.value =
							PeminjamanInputViewState.LoadingSearchPeminjaman(false)
					}
				}
			} catch (e: Exception) {
				_peminjamInputViewState.postValue(PeminjamanInputViewState.ErrorGetData(e.localizedMessage))
			}
		}
	}
	
	fun getDorListLine(partNo: String, documentId: String) {
		viewModelScope.launch {
			try {
				_dorInputViewState.value = DorInputViewState.LoadingSearchDor(true)
				io {
					val data =
						dorPickingRepository.getAllDorDetailData(documentId, partNo)
					ui {
						_dorInputViewState.value =
							DorInputViewState.SuccessGetValue(data.toMutableList())
						_dorInputViewState.value = DorInputViewState.LoadingSearchDor(false)
					}
				}
			} catch (e: Exception) {
				_dorInputViewState.postValue(DorInputViewState.ErrorGetData(e.localizedMessage))
			}
		}
	}
	
	fun checkSn(serialNo: String, inputType: InputType) {
		viewModelScope.launch {
			try {
				when (inputType) {
					PICKING -> {
						io {
							val result = pickingListRepository.checkPickingListNoandSN(serialNo)
							ui {
								_pickingInputViewState.value =
									PickingInputViewState.CheckSNResult(result)
							}
						}
					}
					PEMINJAMAN -> {
						io {
							val result = peminjamanRepository.checkPeminjamSerialNo(serialNo)
							ui {
								_peminjamInputViewState.value =
									PeminjamanInputViewState.CheckSNResult(result)
							}
						}
					}
					DOR -> {
						io {
							val result = dorPickingRepository.checkDorSerialNo(serialNo)
							ui {
								_peminjamInputViewState.value =
									PeminjamanInputViewState.CheckSNResult(result)
							}
						}
					}
				}
				
			} catch (e: Exception) {
				_pickingInputViewState.postValue(
					PickingInputViewState.ErrorGetData(
						e.message ?: ""
					)
				)
			}
		}
	}
	
	sealed class PickingInputViewState {
		class SuccessGetValue(val data: MutableList<PickingListLineValue>) : PickingInputViewState()
		class LoadingSearchPickingList(val status: Boolean) : PickingInputViewState()
		class ErrorGetData(val message: String) : PickingInputViewState()
		class CheckSNResult(val boolean: Boolean) : PickingInputViewState()
	}
	
	sealed class PeminjamanInputViewState {
		class SuccessGetValue(val data: MutableList<PeminjamanDetail>) :
			PeminjamanInputViewState()
		
		class LoadingSearchPeminjaman(val status: Boolean) : PeminjamanInputViewState()
		class ErrorGetData(val message: String) : PeminjamanInputViewState()
		class CheckSNResult(val boolean: Boolean) : PeminjamanInputViewState()
	}
	
	sealed class DorInputViewState {
		class SuccessGetValue(val data: MutableList<DorPickingDetail>) : DorInputViewState()
		class LoadingSearchDor(val status: Boolean) : DorInputViewState()
		class ErrorGetData(val message: String) : DorInputViewState()
		class CheckSNResult(val boolean: Boolean) : DorInputViewState()
	}
}
