package dynamia.com.core.data.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import dynamia.com.core.data.model.*
import dynamia.com.core.domain.ErrorResponse
import dynamia.com.core.domain.ResultWrapper
import dynamia.com.core.domain.RetrofitBuilder
import dynamia.com.core.util.Constant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface NetworkRepository {
    suspend fun getPickingListHeaderAsync(): Flow<ResultWrapper<MutableList<PickingListHeaderValue>>>
    suspend fun getPickingListLineAsync(): Flow<MutableList<PickingListLineValue>>
    suspend fun getReceiptImportHeaderAsync(): Flow<MutableList<ReceiptImportHeaderValue>>
    suspend fun getReceiptImportLineAsync(): Flow<MutableList<ReceiptImportLineValue>>
    suspend fun getReceiptLocalHeaderAsync(): Flow<MutableList<ReceiptLocalHeaderValue>>
    suspend fun getReceiptLocalLineAsync(): Flow<MutableList<ReceiptLocalLineValue>>
    suspend fun postReceiptImportEntry(value: String): Flow<ReceiptImportScanEntriesValue>
    suspend fun postReceiptLocalEntry(value: String): Flow<ReceiptLocalScanEntriesValue>
    suspend fun postPickingListEntry(value: String): Flow<PickingListScanEntriesValue>
    suspend fun postStockCountEntry(value: String): Flow<ResultWrapper<StockCount>>

    /**
     * Peminjaman Network Call
     */

    suspend fun getPeminjamListHeaderAsync(): Flow<ResultWrapper<MutableList<PeminjamanHeader>>>
    suspend fun getPeminjamListDetailAsync(): Flow<ResultWrapper<MutableList<PeminjamanDetail>>>
    suspend fun postPeminjamEntryAsync(value: String): Flow<ResultWrapper<PeminjamScanEntries>>

    /**
     * DorPicking Network Call
     */
    suspend fun getDorPickingListHeaderAsync(): Flow<ResultWrapper<MutableList<DorPickingHeader>>>
    suspend fun getDorPickingListDetailAsync(): Flow<ResultWrapper<MutableList<DorPickingDetail>>>
    suspend fun postDorPickingEntryAsync(value: String): Flow<ResultWrapper<DorPickingScanEntries>>


}

class NetworkRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : NetworkRepository {
    private val retrofitService by lazy {
        RetrofitBuilder.getClient(
            serverAddress = sharedPreferences.getString(Constant.HOST_DOMAIN_KEY, "") ?: "",
            password = sharedPreferences.getString(Constant.PASSWORD_KEY, "") ?: "",
            username = sharedPreferences.getString(Constant.USERNAME_KEY, "") ?: ""
        )
    }

    override suspend fun getPickingListHeaderAsync(): Flow<ResultWrapper<MutableList<PickingListHeaderValue>>> =
        flow {
            try {
                val result = retrofitService.getPickingListHeaderAsync()
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
                emit(ResultWrapper.NetworkError)
            }
        }

    override suspend fun getPickingListLineAsync(): Flow<MutableList<PickingListLineValue>> = flow {
        retrofitService.getPickingListLineAsync().value.let {
            emit(it.toMutableList())
        }
    }

    override suspend fun getReceiptImportHeaderAsync(): Flow<MutableList<ReceiptImportHeaderValue>> =
        flow {
            retrofitService.getReceiptImportHeaderAsync().value.let {
                emit(it.toMutableList())
            }
        }

    override suspend fun getReceiptImportLineAsync(): Flow<MutableList<ReceiptImportLineValue>> =
        flow {
            retrofitService.getReceiptImportLineAsync().value.let {
                emit(it.toMutableList())
            }
        }

    override suspend fun getReceiptLocalHeaderAsync(): Flow<MutableList<ReceiptLocalHeaderValue>> =
        flow {
            retrofitService.getReceiptLocalHeaderAsync().value.let {
                emit(it.toMutableList())
            }
        }

    override suspend fun getReceiptLocalLineAsync(): Flow<MutableList<ReceiptLocalLineValue>> =
        flow {
            retrofitService.getReceiptLocalLineAsync().value.let {
                emit(it.toMutableList())
            }
        }

    override suspend fun postReceiptImportEntry(value: String): Flow<ReceiptImportScanEntriesValue> =
        flow {
            emit(retrofitService.postReceiptImportEntry(value))
        }

    override suspend fun postReceiptLocalEntry(value: String): Flow<ReceiptLocalScanEntriesValue> =
        flow {
            emit(retrofitService.postReceiptLocalEntry(value))
        }

    override suspend fun postPickingListEntry(value: String): Flow<PickingListScanEntriesValue> =
        flow {
            emit(retrofitService.postPickingListEntry(value))
        }

    override suspend fun postStockCountEntry(value: String): Flow<ResultWrapper<StockCount>> =
        flow {
            val result = retrofitService.postStockCountEntry(value)
            if (result.isSuccessful) {
                result.body()?.let { emit(ResultWrapper.Success(it)) }
            } else {
                result.errorBody()
                    ?.let {
                        val errorMessage =
                            Gson().fromJson(it.charStream().readText(), ErrorResponse::class.java)
                        emit(ResultWrapper.GenericError(result.code(), errorMessage))
                    }
            }
        }

    /**
     * Peminjaman Network Call
     */
    override suspend fun getPeminjamListHeaderAsync(): Flow<ResultWrapper<MutableList<PeminjamanHeader>>> =
        flow {
            try {
                val result = retrofitService.getPeminjamHeaderAsync()
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
                emit(ResultWrapper.NetworkError)
            }
        }

    override suspend fun getPeminjamListDetailAsync(): Flow<ResultWrapper<MutableList<PeminjamanDetail>>> =
        flow {
            try {
                val result = retrofitService.getPeminjamanDetailAsycn()
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
                emit(ResultWrapper.NetworkError)
            }
        }

    override suspend fun postPeminjamEntryAsync(value: String): Flow<ResultWrapper<PeminjamScanEntries>> =
        flow {
            try {
                val result = retrofitService.postPeminjamEntry(value)
                when (result.isSuccessful) {
                    true -> {
                        result.body()?.let { emit(ResultWrapper.Success(it)) }
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
                emit(ResultWrapper.NetworkError)
            }
        }

    /**
     * DorPicking Network Call
     */
    override suspend fun getDorPickingListHeaderAsync(): Flow<ResultWrapper<MutableList<DorPickingHeader>>> =
        flow {
            try {
                val result = retrofitService.getDorPickingListHeaderAsycn()
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
                emit(ResultWrapper.NetworkError)
            }
        }

    override suspend fun getDorPickingListDetailAsync(): Flow<ResultWrapper<MutableList<DorPickingDetail>>> =
        flow {
            try {
                val result = retrofitService.getDorPickingListDetailAsycn()
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
                emit(ResultWrapper.NetworkError)
            }
        }

    override suspend fun postDorPickingEntryAsync(value: String): Flow<ResultWrapper<DorPickingScanEntries>> =
        flow {
            try {
                val result = retrofitService.postDorPickEntry(value)
                when (result.isSuccessful) {
                    true -> {
                        result.body()?.let { emit(ResultWrapper.Success(it)) }
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
                emit(ResultWrapper.NetworkError)
            }
        }
}




