package dynamia.com.core.data.repository

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import dynamia.com.core.data.dao.TransferShipmentDao
import dynamia.com.core.data.entinty.TransferShipmentHeader
import dynamia.com.core.data.entinty.TransferShipmentLine
import dynamia.com.core.domain.ErrorResponse
import dynamia.com.core.domain.MasariRetrofit
import dynamia.com.core.domain.ResultWrapper
import dynamia.com.core.util.Constant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface TransferShipmentRepository {
    /**
     * Local Transfer Header
     */
    suspend fun getAllTransferHeader(): LiveData<List<TransferShipmentHeader>>
    suspend fun insertTransferHeader(data: TransferShipmentHeader)
    suspend fun deleteAllTransferHeader()

    /**
     * Local Transfer Line
     */
    suspend fun getAllTransferLine(): LiveData<List<TransferShipmentLine>>
    suspend fun insertTransferLine(data: TransferShipmentLine)
    suspend fun deleteAllTransferLine()
    suspend fun getLineListFromHeader(no: String): LiveData<List<TransferShipmentLine>>

    /**
     * Remote Transfer
     */

    suspend fun getTransferShipmentHeaderAsync(): Flow<ResultWrapper<MutableList<TransferShipmentHeader>>>
    suspend fun getTransferShipmentLineAsync(): Flow<ResultWrapper<MutableList<TransferShipmentLine>>>
}

class TransferShipmentImpl(
    val dao: TransferShipmentDao,
    private val sharedPreferences: SharedPreferences
) : TransferShipmentRepository {
    private val retrofitService by lazy {
        MasariRetrofit.getClient(
            serverAddress = sharedPreferences.getString(Constant.HOST_DOMAIN_KEY, "") ?: "",
            password = sharedPreferences.getString(Constant.PASSWORD_KEY, "") ?: "",
            username = sharedPreferences.getString(Constant.USERNAME_KEY, "") ?: ""
        )
    }

    /**
     * Local Implementation
     */

    override suspend fun getAllTransferHeader(): LiveData<List<TransferShipmentHeader>> =
        dao.getAllTransferHeader()

    override suspend fun insertTransferHeader(data: TransferShipmentHeader) =
        dao.insertTransferHeader(data)

    override suspend fun deleteAllTransferHeader() = dao.deleteAllTransferHeader()

    override suspend fun getAllTransferLine(): LiveData<List<TransferShipmentLine>> =
        dao.getAllTransferLine()

    override suspend fun insertTransferLine(data: TransferShipmentLine) =
        dao.insertTransferLine(data)

    override suspend fun deleteAllTransferLine() = dao.deleteAllTransferLine()

    override suspend fun getLineListFromHeader(no: String): LiveData<List<TransferShipmentLine>> =
        dao.getLineListFromHeader(no)

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
}

