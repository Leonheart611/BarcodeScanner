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
import dynamia.com.core.util.toTransferShipmentFilter
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
    suspend fun insertTransferLineAll(data: MutableList<TransferShipmentLine>)
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
    suspend fun getLineDetailFromId(id: Int): Flow<TransferShipmentLine>

    suspend fun checkLoginDummy(): Flow<Boolean>

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
    fun getTransferInputHistoryLiveData(
        no: String,
        accidentlyInput: Boolean
    ): LiveData<List<TransferInputData>>

    fun getTransferShipmentAccidentInput(no: String): LiveData<Int?>

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
    private val username: String
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

    override suspend fun insertTransferLineAll(data: MutableList<TransferShipmentLine>) =
        dao.insertTransferLine(data)

    override suspend fun deleteAllTransferLine() = dao.deleteAllTransferLine()

    override fun getLineListFromHeaderLiveData(
        no: String,
        page: Int
    ): LiveData<List<TransferShipmentLine>> =
        dao.getLineListFromHeaderLiveData(no, page)

    override suspend fun getLineDetailFromId(id: Int): Flow<TransferShipmentLine> = flow {
        val value = dao.getLineDetailFromId(id)
        emit(value)
    }

    override fun getLineListFromReceiptLiveData(
        no: String,
        page: Int
    ): LiveData<List<TransferShipmentLine>> = dao.getLineListFromHeaderReceipt(no, page)

    override suspend fun getCheckEmptyOrNot(): Int = dao.getCheckEmptyOrNot()

    override fun getLineDetailLiveData(id: Int): LiveData<TransferShipmentLine> =
        dao.getLineDetailLiveData(id)

    override fun getAllTransferInput(): LiveData<List<TransferInputData>> =
        dao.getAllTransferInput()

    override fun getTransferInputHistoryLiveData(
        no: String,
        accidentlyInput: Boolean
    ): LiveData<List<TransferInputData>> =
        dao.getTransferInputHistoryLiveData(no, accidentlyInput)

    override suspend fun insertTransferInput(data: TransferInputData): Boolean =
        runBlocking(Dispatchers.IO) {
            try {
                if (data.lineNo == 0) {
                    data.apply { accidentalScanned = true }
                    dao.insertTransferInput(data)
                } else {
                    val lineData = dao.getLineDetail(data.documentNo, data.lineNo)
                    lineData.apply {
                        this.alredyScanned += data.quantity
                    }
                    dao.insertTransferInput(data)
                    dao.updateTransferLine(lineData)
                }
                true
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
            dao.getLineDetailFromRef(no, identifier)?.let { emit(it) } ?: run {
                error("Item name not found")
            }
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

    override suspend fun checkLoginDummy(): Flow<Boolean> = flow {
        emit(true)
    }

    /**
     * Remote Implementation
     */

    override suspend fun getTransferShipmentHeaderAsync(): Flow<ResultWrapper<MutableList<TransferShipmentHeader>>> =
        flow {
            try {
                val result = retrofitService.getTransferShipmentHeader(
                    filter = username.toTransferShipmentFilter()
                )
                when (result.code()) {
                    200 -> result.body()?.value?.let { emit(ResultWrapper.Success(it.toMutableList())) }
                    400 -> emit(ResultWrapper.SuccessEmptyValue)
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
                emit(ResultWrapper.NetworkError(e.localizedMessage.orEmpty()))
            }
        }

    override suspend fun getTransferShipmentLineAsync(): Flow<ResultWrapper<MutableList<TransferShipmentLine>>> =
        flow {
            try {
                val result = retrofitService.getTransferShipmentLine()
                when (result.code()) {
                    200 -> result.body()?.value?.let { emit(ResultWrapper.Success(it.toMutableList())) }
                    400 -> emit(ResultWrapper.SuccessEmptyValue)
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
                emit(ResultWrapper.NetworkError(e.localizedMessage.orEmpty()))
            }
        }


    override suspend fun checkLoginCredential(): Flow<ResultWrapper<Boolean>> =
        flow {
            try {
                val result = retrofitService.getPurchaseOrderHeader()
                when (result.code()) {
                    200 -> emit(ResultWrapper.Success(true))
                    400 -> emit(ResultWrapper.SuccessEmptyValue)
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
                emit(ResultWrapper.NetworkError(e.localizedMessage.orEmpty()))
            }
        }

    override fun getTransferShipmentAccidentInput(no: String): LiveData<Int?> =
        dao.getTransferShipmentAccidentInput(no)

    override suspend fun postTransferData(value: String): Flow<TransferInputData> = flow {
        emit(retrofitService.postTransferShipment(value))
    }
}

