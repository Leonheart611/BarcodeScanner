package dynamia.com.core.data.repository

import androidx.lifecycle.LiveData
import com.google.gson.Gson
import dynamia.com.core.data.dao.TransferReceiptDao
import dynamia.com.core.data.dao.TransferShipmentDao
import dynamia.com.core.data.entinty.TransferReceiptHeader
import dynamia.com.core.data.entinty.TransferReceiptInput
import dynamia.com.core.domain.ErrorResponse
import dynamia.com.core.domain.MasariAPI
import dynamia.com.core.domain.ResultWrapper
import dynamia.com.core.util.toReceiptHeaderFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface TransferReceiptRepository {
    /**
     * Local Transfer Receipt
     */
    fun getAllTransferReceiptHeader(page: Int = 20): LiveData<List<TransferReceiptHeader>>
    fun getCountTransferReceiptHeader(): LiveData<Int>
    fun getTransferHeaderDetail(no: String): Flow<TransferReceiptHeader>
    suspend fun insertTransferReceiptHeader(data: TransferReceiptHeader)
    suspend fun deleteAllTransferReceiptHeader()
    suspend fun getTransferReceiptCount(): Int

    fun getTransferReceiptQty(no: String): LiveData<Int>
    fun getTransferReceiptAlreadyScan(no: String): LiveData<Int>
    fun getAllTransferReceiptInput(): LiveData<List<TransferReceiptInput>>
    suspend fun insertTransferReceiptInput(data: TransferReceiptInput): Boolean
    suspend fun updateTransferReceiptInput(data: TransferReceiptInput)
    suspend fun updateTransferReceiptInputQty(id: Int, newQty: Int): Flow<Boolean>
    fun getAllUnsycnTransferReceiptInput(status: Boolean = false): List<TransferReceiptInput>
    fun getTransferReceiptInputHistory(id: Int): TransferReceiptInput
    fun getTransferInputHistoryLiveData(
        no: String,
        accidentlyInput: Boolean
    ): LiveData<List<TransferReceiptInput>>

    fun getTransferReceiptAccidentInput(no: String): LiveData<Int>
    suspend fun getTransferInputDetail(id: Int): Flow<TransferReceiptInput>
    suspend fun deleteTransferInput(id: Int)
    suspend fun clearAllInputData()

    /**
     * Remote Transfer Receipt
     */

    suspend fun getTransferReceiptHeaderAsync(): Flow<ResultWrapper<MutableList<TransferReceiptHeader>>>
    suspend fun postTransferReceiptInput(value: String): Flow<TransferReceiptInput>


}

class TransferReceiptRepositoryImpl @Inject constructor(
    val dao: TransferReceiptDao,
    private val retrofitService: MasariAPI,
    private val lineDao: TransferShipmentDao,
    private val username: String,
) : TransferReceiptRepository {

    override fun getAllTransferReceiptHeader(page: Int): LiveData<List<TransferReceiptHeader>> =
        dao.getAllTransferReceiptHeader(page)

    override fun getCountTransferReceiptHeader(): LiveData<Int> = dao.getCount()

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

    override suspend fun insertTransferReceiptInput(data: TransferReceiptInput): Boolean {
        return try {
            if (data.lineNo == 0) {
                data.apply { accidentalScanned = true }
                dao.insertTransferReceiptInput(data)
            } else {
                val lineData = lineDao.getLineDetail(data.documentNo, data.lineNo)
                lineData.apply {
                    this.alreadyScanedReceipt += data.quantity
                }
                dao.insertTransferReceiptInput(data)
                lineDao.updateTransferLine(lineData)
            }
            true
        } catch (e: Exception) {
            e.stackTrace
            false
        }
    }

    override suspend fun updateTransferReceiptInput(data: TransferReceiptInput) {
        dao.updateTransferReceiptInput(data)
    }

    override suspend fun updateTransferReceiptInputQty(id: Int, newQty: Int): Flow<Boolean> = flow {
        val transferInput = dao.getTransferInputDetail(id)
        val lineData = lineDao.getLineDetail(transferInput.documentNo, transferInput.lineNo)
        val totalQty = lineData.alreadyScanedReceipt - transferInput.quantity + newQty
        if (totalQty <= lineData.qtyInTransit!!) {
            lineData.apply {
                alreadyScanedReceipt = totalQty
            }
            transferInput.apply {
                quantity = newQty
            }
            dao.updateTransferReceiptInput(transferInput)
            lineDao.updateTransferLine(lineData)
            emit(true)
        } else {
            emit(false)
        }
    }

    override fun getAllUnsycnTransferReceiptInput(status: Boolean): List<TransferReceiptInput> =
        dao.getAllUnsycnTransferReceiptInput(status)

    override fun getTransferReceiptInputHistory(id: Int): TransferReceiptInput =
        dao.getTransferReceiptInputHistory(id)

    override fun getTransferInputHistoryLiveData(
        no: String,
        accidentlyInput: Boolean
    ): LiveData<List<TransferReceiptInput>> =
        dao.getTransferInputHistoryLiveData(no, accidentlyInput)

    override suspend fun getTransferInputDetail(id: Int): Flow<TransferReceiptInput> = flow {
        emit(dao.getTransferInputDetail(id))
    }

    override fun getTransferReceiptQty(no: String): LiveData<Int> =
        lineDao.getQtyScanReceiptTotal(no)

    override fun getTransferReceiptAlreadyScan(no: String): LiveData<Int> =
        lineDao.getQtyAlreadyScanReceiptTotal(no)

    override suspend fun deleteTransferInput(id: Int) {
        val transferInput = dao.getTransferInputDetail(id)
        val lineData = lineDao.getLineDetail(transferInput.documentNo, transferInput.lineNo)
        lineData.apply {
            alreadyScanedReceipt -= transferInput.quantity
        }
        dao.deleteTransferInput(id)
        lineDao.updateTransferLine(lineData)
    }

    override suspend fun clearAllInputData() {
        dao.clearAllInputData()
    }

    override suspend fun getTransferReceiptCount(): Int = dao.getTransferReceiptCount()

    override fun getTransferReceiptAccidentInput(no: String): LiveData<Int> =
        dao.getTransferReceiptAccidentInput(no)

    /**
     * Remote Impl
     */

    override suspend fun getTransferReceiptHeaderAsync(): Flow<ResultWrapper<MutableList<TransferReceiptHeader>>> =
        flow {
            try {
                val result = retrofitService.getTransferReceiptHeader(
                    filter = username.toReceiptHeaderFilter()
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

    override suspend fun postTransferReceiptInput(value: String): Flow<TransferReceiptInput> =
        flow { emit(retrofitService.postTransferReceipt(value)) }
}