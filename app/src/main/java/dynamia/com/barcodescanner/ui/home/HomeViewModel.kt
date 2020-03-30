package dynamia.com.barcodescanner.ui.home

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import dynamia.com.barcodescanner.domain.RetrofitBuilder
import dynamia.com.core.base.ViewModelBase
import dynamia.com.core.data.repository.PickingListRepository
import dynamia.com.core.data.repository.ReceiptImportRepository
import dynamia.com.core.data.repository.ReceiptLocalRepository
import dynamia.com.core.util.Constant.EMPLOYEE_SHARED_PREFERENCES
import dynamia.com.core.util.Constant.HOST_DOMAIN_SHARED_PREFERENCES
import dynamia.com.core.util.Constant.PASSWORD_SHARED_PREFERENCES
import dynamia.com.core.util.Constant.USERNAME_SHARED_PREFERENCES
import kotlinx.coroutines.*
import kotlin.reflect.jvm.internal.impl.load.java.Constant

class HomeViewModel(
    val pickingListRepository: PickingListRepository,
    val receiptImportRepository: ReceiptImportRepository,
    val receiptLocalRepository: ReceiptLocalRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModelBase(sharedPreferences) {
    val coroutineJob = Job()
    val coroutineContext = Dispatchers.IO + coroutineJob
    val uiScope = CoroutineScope(coroutineContext)

    val retrofitService by lazy {
        RetrofitBuilder.getClient(
            serverAddress = sharedPreferences.getString(HOST_DOMAIN_SHARED_PREFERENCES,"")?:""
        )
    }

    fun getAllDataFromAPI(){
        uiScope.launch(Dispatchers.IO) {
            try {
                val request = retrofitService.getPickingListHeader()
                withContext(Dispatchers.Main) {
                    if (request.isSuccessful) {
                        if (request.body()?.value?.size != 0) {
                           pickingListRepository.clearPickingListHeader()
                            request.body()?.value?.let {pickingList->
                                with(pickingList){
                                    for (value in this) {
                                        value?.let {
                                            pickingListRepository.insertPickingListHeader(it)
                                        }
                                    }
                                }

                            }
                        } else {
                        }
                    } else {

                    }
                }
            } catch (e: Exception){
                Log.e("ErrorHItAPI",e.message)
            }
        }
    }

    fun clearAllDB(){
        pickingListRepository.clearPickingListHeader()
        pickingListRepository.clearPickingListLine()
        pickingListRepository.clearPickingListScanEntries()

        receiptImportRepository.clearReceiptImportHeader()
        receiptImportRepository.clearReceiptImportLine()
        receiptImportRepository.clearReceiptImportScanEntries()

        receiptLocalRepository.clearReceiptLocalHeader()
        receiptLocalRepository.clearReceiptLocalLine()
        receiptLocalRepository.clearReceiptLocalScanEntries()
    }

}
