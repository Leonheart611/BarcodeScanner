package dynamia.com.core.data.repository

import androidx.lifecycle.LiveData
import com.google.gson.Gson
import dynamia.com.core.data.dao.TransferShipmentDao
import dynamia.com.core.data.entinty.TransferInputData
import dynamia.com.core.data.entinty.TransferShipmentHeader
import dynamia.com.core.data.entinty.TransferShipmentLine
import dynamia.com.core.domain.ErrorResponse
import dynamia.com.core.domain.MasariAPI
import dynamia.com.core.domain.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

interface TransferShipmentRepository {
    /**
     * Local Transfer Header
     */
    fun getAllTransferHeader(page: Int = 20): LiveData<List<TransferShipmentHeader>>
    fun getTransferHeaderCount(): LiveData<Int>
    suspend fun getTransferHeaderDetail(no: String): Flow<TransferShipmentHeader>
    suspend fun insertTransferHeader(data: TransferShipmentHeader)
    suspend fun deleteAllTransferHeader()
    suspend fun getCheckEmptyOrNot(): Int

    /**
     * Local Transfer Line
     */
    suspend fun getAllTransferLine(): LiveData<List<TransferShipmentLine>>
    suspend fun insertTransferLineAll(data: List<TransferShipmentLine>)
    suspend fun deleteAllTransferLine()
    fun getLineListFromHeaderLiveData(
        no: String,
        page: Int = 20
    ): LiveData<List<TransferShipmentLine>>

    fun getLineListFromReceiptLiveData(
        no: String,
        page: Int = 20
    ): LiveData<List<TransferShipmentLine>>

    fun getLineDetailLiveData(id: Int): LiveData<TransferShipmentLine>
    fun getQtyliveData(no: String): LiveData<Int>
    fun getQtyAlreadyScanLiveData(no: String): LiveData<Int>
    suspend fun getLineDetailFromBarcode(no: String, identifier: String): Flow<TransferShipmentLine>

    /**
     * Local Transfer Insert
     */
    fun getAllTransferInput(): LiveData<List<TransferInputData>>
    suspend fun insertTransferInput(data: TransferInputData): Boolean
    suspend fun updateTransferInput(data: TransferInputData)
    suspend fun updateTransferShipmentInputQty(id: Int, newQty: Int): Flow<Boolean>
    suspend fun deleteTransferInput(id: Int)
    fun getAllUnsycnTransferInput(status: Boolean = false): List<TransferInputData>
    suspend fun getTransferInputHistory(no: Int): Flow<TransferInputData>
    fun getTransferInputHistoryLiveData(no: String): LiveData<List<TransferInputData>>

    suspend fun deleteAllTransferInput()

    /**
     * Remote Transfer
     */

    suspend fun getTransferShipmentHeaderAsync(): Flow<ResultWrapper<MutableList<TransferShipmentHeader>>>
    suspend fun getTransferShipmentLineAsync(): Flow<ResultWrapper<MutableList<TransferShipmentLine>>>
    suspend fun postTransferData(value: String): Flow<TransferInputData>
    suspend fun checkLoginCredential(): Flow<ResultWrapper<Boolean>>
}

class TransferShipmentImpl @Inject constructor(
    val dao: TransferShipmentDao,
    private val retrofitService: MasariAPI,
) : TransferShipmentRepository {

    /**
     * Local Implementation
     */

    override fun getAllTransferHeader(page: Int): LiveData<List<TransferShipmentHeader>> =
        dao.getAllTransferHeader(page)

    override suspend fun getTransferHeaderDetail(no: String): Flow<TransferShipmentHeader> = flow {
        emit(dao.getTransferHeaderDetail(no))
    }

    override fun getTransferHeaderCount(): LiveData<Int> = dao.getCount()

    override suspend fun insertTransferHeader(data: TransferShipmentHeader) =
        dao.insertTransferHeader(data)

    override suspend fun deleteAllTransferHeader() = dao.deleteAllTransferHeader()

    override suspend fun getAllTransferLine(): LiveData<List<TransferShipmentLine>> =
        dao.getAllTransferLine()

    override suspend fun insertTransferLineAll(data: List<TransferShipmentLine>) =
        dao.insertTransferLine(data)

    override suspend fun deleteAllTransferLine() = dao.deleteAllTransferLine()

    override fun getLineListFromHeaderLiveData(
        no: String,
        page: Int
    ): LiveData<List<TransferShipmentLine>> =
        dao.getLineListFromHeaderLiveData(no, page)

    override fun getLineListFromReceiptLiveData(
        no: String,
        page: Int
    ): LiveData<List<TransferShipmentLine>> = dao.getLineListFromHeaderReceipt(no, page)

    override suspend fun getCheckEmptyOrNot(): Int = dao.getCheckEmptyOrNot()

    override fun getLineDetailLiveData(id: Int): LiveData<TransferShipmentLine> =
        dao.getLineDetailLiveData(id)

    override fun getAllTransferInput(): LiveData<List<TransferInputData>> =
        dao.getAllTransferInput()

    override fun getTransferInputHistoryLiveData(no: String): LiveData<List<TransferInputData>> =
        dao.getTransferInputHistoryLiveData(no)

    override suspend fun insertTransferInput(data: TransferInputData): Boolean =
        runBlocking(Dispatchers.IO) {
            try {
                val lineData = dao.getLineDetail(data.documentNo, data.lineNo)
                if ((lineData.alredyScanned + data.quantity) <= lineData.quantity) {
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

    override fun getQtyliveData(no: String): LiveData<Int> = dao.getQtyScanTotal(no)

    override fun getQtyAlreadyScanLiveData(no: String): LiveData<Int> =
        dao.getAlreadyScanTotal(no)

    override suspend fun getLineDetailFromBarcode(
        no: String,
        identifier: String,
    ): Flow<TransferShipmentLine> = flow {
        val result = dao.getLineDetailFromBarcode(no, identifier)
        if (result != null) {
            emit(result)
        } else {
            dao.getLineDetailFromRef(no, identifier)?.let { emit(it) }
                ?: kotlin.run { error("Data Not Found") }
        }

    }

    override suspend fun updateTransferInput(data: TransferInputData) {
        dao.updateTransferInput(data)
    }

    override suspend fun updateTransferShipmentInputQty(
        id: Int,
        newQty: Int,
    ): Flow<Boolean> =
        flow {
            val transferInput = dao.getTransferInputDetail(id)
            val lineData = dao.getLineDetail(transferInput.documentNo, transferInput.lineNo)
            val totalQty = lineData.alredyScanned - transferInput.quantity + newQty
            if (totalQty <= lineData.quantity) {
                lineData.apply {
                    alredyScanned = totalQty
                }
                transferInput.apply {
                    quantity = newQty
                }
                dao.updateTransferInput(transferInput)
                dao.updateTransferLine(lineData)
                emit(true)
            } else {
                emit(false)
            }
        }

    override suspend fun deleteTransferInput(id: Int) {
        val transferInput = dao.getTransferInputDetail(id)
        val lineData = dao.getLineDetail(transferInput.documentNo, transferInput.lineNo)
        lineData.apply {
            alredyScanned -= transferInput.quantity
        }
        dao.deleteTransferInput(id)
        dao.updateTransferLine(lineData)
    }

    override fun getAllUnsycnTransferInput(status: Boolean): List<TransferInputData> =
        dao.getAllUnsycnTransferInput(status)

    override suspend fun deleteAllTransferInput() {
        dao.clearAllInputData()
    }

    override suspend fun getTransferInputHistory(no: Int): Flow<TransferInputData> = flow {
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


    override suspend fun checkLoginCredential(): Flow<ResultWrapper<Boolean>> =
        flow {
            try {
                val result = retrofitService.getTransferShipmentLine()
                when (result.isSuccessful) {
                    true -> emit(ResultWrapper.Success(true))
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
        emit(retrofitService.postTransferShipment(value))
    }
}

