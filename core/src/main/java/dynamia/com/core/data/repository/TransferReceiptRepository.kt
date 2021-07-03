package dynamia.com.core.data.repository

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import dynamia.com.core.data.dao.TransferReceiptDao
import dynamia.com.core.data.entinty.TransferReceiptHeader
import dynamia.com.core.data.entinty.TransferReceiptInput
import dynamia.com.core.domain.ErrorResponse
import dynamia.com.core.domain.MasariRetrofit
import dynamia.com.core.domain.ResultWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface TransferReceiptRepository {
    /**
     * Local Transfer Receipt
     */
    fun getAllTransferReceiptHeader(): LiveData<List<TransferReceiptHeader>>
    fun getTransferHeaderDetail(no: String): Flow<TransferReceiptHeader>
    suspend fun insertTransferReceiptHeader(data: TransferReceiptHeader)
    suspend fun deleteAllTransferReceiptHeader()

    fun getAllTransferReceiptInput(): LiveData<List<TransferReceiptInput>>
    suspend fun insertTransferReceiptInput(data: TransferReceiptInput)
    suspend fun updateTransferReceiptInput(data: TransferReceiptInput)
    fun getAllUnsycnTransferReceiptInput(status: Boolean = false): List<TransferReceiptInput>
    fun getTransferReceiptInputHistory(id: Int): TransferReceiptInput
    fun getTransferInputHistoryLiveData(no: String): LiveData<List<TransferReceiptInput>>
    suspend fun getTransferInputDetail(id: Int): TransferReceiptInput
    suspend fun deleteTransferInput(id: Int)
    suspend fun clearAllInputData()

    /**
     * Remote Transfer Receipt
     */

    suspend fun getTransferReceiptHeader(): Flow<ResultWrapper<MutableList<TransferReceiptHeader>>>
    suspend fun postTransferReceiptInput(value: String): Flow<TransferReceiptInput>


}

class TransferReceiptRepositoryImpl(
    val dao: TransferReceiptDao,
    private val sharedPreferences: SharedPreferences,
) : TransferReceiptRepository {
    private val retrofitService by lazy { MasariRetrofit().getClient(sharedPreferences) }

    override fun getAllTransferReceiptHeader(): LiveData<List<TransferReceiptHeader>> =
        dao.getAllTransferReceiptHeader()

    override fun getTransferHeaderDetail(no: String): Flow<TransferReceiptHeader> = flow {
        emit(dao.getTransferHeaderDetail(no))
    }

    override suspend fun insertTransferReceiptHeader(data: TransferReceiptHeader) {
        dao.insertTransferReceiptHeader(data)
    }

    override suspend fun deleteAllTransferReceiptHeader() {
        dao.deleteAllTransferReceiptHeader()
    }

    override fun getAllTransferReceiptInput(): LiveData<List<TransferReceiptInput>> =
        dao.getAllTransferReceiptInput()

    override suspend fun insertTransferReceiptInput(data: TransferReceiptInput) {
        dao.insertTransferReceiptInput(data)
    }

    override suspend fun updateTransferReceiptInput(data: TransferReceiptInput) {
        dao.updateTransferReceiptInput(data)
    }

    override fun getAllUnsycnTransferReceiptInput(status: Boolean): List<TransferReceiptInput> =
        dao.getAllUnsycnTransferReceiptInput(status)

    override fun getTransferReceiptInputHistory(id: Int): TransferReceiptInput =
        dao.getTransferReceiptInputHistory(id)

    override fun getTransferInputHistoryLiveData(no: String): LiveData<List<TransferReceiptInput>> =
        dao.getTransferInputHistoryLiveData(no)

    override suspend fun getTransferInputDetail(id: Int): TransferReceiptInput =
        dao.getTransferInputDetail(id)

    override suspend fun deleteTransferInput(id: Int) {
        dao.deleteTransferInput(id)
    }

    override suspend fun clearAllInputData() {
        dao.clearAllInputData()
    }

    /**
     * Remote Impl
     */

    override suspend fun getTransferReceiptHeader(): Flow<ResultWrapper<MutableList<TransferReceiptHeader>>> =
        flow {
            try {
                val result = retrofitService.getTransferReceiptHeader()
                when (result.isSuccessful) {
                    true -> {
                        result.body()?.value?.let { emit(ResultWrapper.Success(it.toMutableList())) }
                    }
                    else -> {
                        result.errorBody()?.let {
                            val errorMessage = Gson().fromJson(
                                it.charStream().readText(),
                                ErrorResponse::class.java
                            )
                            emit(ResultWrapper.GenericError(result.code(), errorMessage))
                        }
                    }
                }
            } catch (e: Exception) {
                emit(ResultWrapper.NetworkError(e.localizedMessage))
            }
        }

    override suspend fun postTransferReceiptInput(value: String): Flow<TransferReceiptInput> =
        flow { emit(retrofitService.postTransferReceipt(value)) }
}