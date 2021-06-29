package dynamia.com.core.data.repository

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import dynamia.com.core.data.dao.TransferShipmentDao
import dynamia.com.core.data.entinty.TransferInputData
import dynamia.com.core.data.entinty.TransferShipmentHeader
import dynamia.com.core.data.entinty.TransferShipmentLine
import dynamia.com.core.domain.ErrorResponse
import dynamia.com.core.domain.MasariRetrofit
import dynamia.com.core.domain.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

interface TransferShipmentRepository {
    /**
     * Local Transfer Header
     */
    fun getAllTransferHeader(): LiveData<List<TransferShipmentHeader>>
    suspend fun getTransferHeaderDetail(no: String): Flow<TransferShipmentHeader>
    suspend fun insertTransferHeader(data: TransferShipmentHeader)
    suspend fun deleteAllTransferHeader()
    fun getCheckEmptyOrNot(): LiveData<Int>

    /**
     * Local Transfer Line
     */
    suspend fun getAllTransferLine(): LiveData<List<TransferShipmentLine>>
    suspend fun insertTransferLine(data: TransferShipmentLine)
    suspend fun deleteAllTransferLine()
    suspend fun getLineListFromHeader(no: String): Flow<List<TransferShipmentLine>>
    fun getLineListFromHeaderLiveData(no: String): LiveData<List<TransferShipmentLine>>
    suspend fun getLineDetailFromBarcode(no: String, identifier: String): Flow<TransferShipmentLine>

    /**
     * Local Transfer Insert
     */
    fun getAllTransferInput(): LiveData<List<TransferInputData>>
    suspend fun insertTransferInput(data: TransferInputData): Boolean
    suspend fun updateTransferInput(data: TransferInputData)
    fun getAllUnsycnTransferInput(status: Boolean = false): List<TransferInputData>
    suspend fun getTransferInputHistory(no: String): Flow<List<TransferInputData>>

    suspend fun deleteAllTransferInput()

    /**
     * Remote Transfer
     */

    suspend fun getTransferShipmentHeaderAsync(): Flow<ResultWrapper<MutableList<TransferShipmentHeader>>>
    suspend fun getTransferShipmentLineAsync(): Flow<ResultWrapper<MutableList<TransferShipmentLine>>>
    suspend fun postTransferData(value: String): Flow<TransferInputData>
    suspend fun getUser(): Flow<String>
}

class TransferShipmentImpl(
    val dao: TransferShipmentDao,
    private val sharedPreferences: SharedPreferences,
) : TransferShipmentRepository {
    private val retrofitService by lazy { MasariRetrofit().getClient(sharedPreferences) }

    /**
     * Local Implementation
     */

    override fun getAllTransferHeader(): LiveData<List<TransferShipmentHeader>> =
        dao.getAllTransferHeader()

    override suspend fun getTransferHeaderDetail(no: String): Flow<TransferShipmentHeader> = flow {
        emit(dao.getTransferHeaderDetail(no))
    }

    override suspend fun insertTransferHeader(data: TransferShipmentHeader) =
        dao.insertTransferHeader(data)

    override suspend fun deleteAllTransferHeader() = dao.deleteAllTransferHeader()

    override suspend fun getAllTransferLine(): LiveData<List<TransferShipmentLine>> =
        dao.getAllTransferLine()

    override suspend fun insertTransferLine(data: TransferShipmentLine) =
        dao.insertTransferLine(data)

    override suspend fun deleteAllTransferLine() = dao.deleteAllTransferLine()

    override suspend fun getLineListFromHeader(no: String): Flow<List<TransferShipmentLine>> =
        flow {
            emit(dao.getLineListFromHeader(no))
        }

    override fun getLineListFromHeaderLiveData(no: String): LiveData<List<TransferShipmentLine>> =
        dao.getLineListFromHeaderLiveData(no)

    override fun getCheckEmptyOrNot(): LiveData<Int> = dao.getCheckEmptyOrNot()


    override fun getAllTransferInput(): LiveData<List<TransferInputData>> =
        dao.getAllTransferInput()

    override suspend fun insertTransferInput(data: TransferInputData): Boolean = runBlocking(
        Dispatchers.IO
    ) {
        try {
            val lineData = dao.getLineDetail(data.documentNo, data.lineNo)
            if ((lineData.alredyScanned + data.quantity) < lineData.quantity) {
                lineData.apply {
                    this.alredyScanned += data.quantity
                }
                dao.insertTransferInput(data)
                dao.updateTransferLine(lineData)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getLineDetailFromBarcode(
        no: String,
        identifier: String,
    ): Flow<TransferShipmentLine> = flow { emit(dao.getLineDetailFromBarcode(no, identifier)) }

    override suspend fun updateTransferInput(data: TransferInputData) {
        dao.updateTransferInput(data)
    }

    override fun getAllUnsycnTransferInput(status: Boolean): List<TransferInputData> =
        dao.getAllUnsycnTransferInput(status)

    override suspend fun deleteAllTransferInput() {
        dao.deleteAllTransferInput()
    }

    override suspend fun getTransferInputHistory(no: String): Flow<List<TransferInputData>> = flow {
        emit(dao.getTransferInputHistory(no))
    }

    /**
     * Remote Implementation
     */

    override suspend fun getTransferShipmentHeaderAsync(): Flow<ResultWrapper<MutableList<TransferShipmentHeader>>> =
        flow {
            try {
                val result = retrofitService.getTransferShipmentHeader()
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

    override suspend fun getTransferShipmentLineAsync(): Flow<ResultWrapper<MutableList<TransferShipmentLine>>> =
        flow {
            try {
                val result = retrofitService.getTransferShipmentLine()
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

    override suspend fun postTransferData(value: String): Flow<TransferInputData> = flow {
        emit(retrofitService.postTransferData(value))
    }

    override suspend fun getUser(): Flow<String> = flow {
        val result = retrofitService.getCustomer()
        emit(result.raw().toString())
    }
}

