package dynamia.com.core.data.repository

import androidx.lifecycle.LiveData
import dynamia.com.core.data.dao.ReceiptLocalDao
import dynamia.com.core.data.model.ReceiptLocalHeaderValue
import dynamia.com.core.data.model.ReceiptLocalLineValue
import dynamia.com.core.data.model.ReceiptLocalScanEntriesValue
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

interface ReceiptLocalRepository {
    //ReceiptLocalHeader--------------------------------------------------
    fun getAllReceiptLocalHeader(employeeCode: String): LiveData<List<ReceiptLocalHeaderValue>>
    fun getReceiptLocalHeader(documentNo: String): LiveData<ReceiptLocalHeaderValue>
    fun insertReceiptLocalHeader(receiptLocalHeaderValue: ReceiptLocalHeaderValue): Job
    fun getCountReceiptLocalHeader(employeeCode: String): LiveData<Int>
    suspend fun clearReceiptLocalHeader()

    //ReceiptLocalLineValue--------------------------------------------------
    fun getAllReceiptLocalLine(documentNo: String): LiveData<List<ReceiptLocalLineValue>>
    fun insertReceiptLocalLine(receiptLocalLineValue: ReceiptLocalLineValue): Job
    fun getReceiptLocalLineDetail(documentNo: String, partNo: String): List<ReceiptLocalLineValue>
    suspend fun clearReceiptLocalLine()

    //ReceiptLocalScanEntriesValue--------------------------------------------------
    fun getAllReceiptLocalScanEntries(): LiveData<List<ReceiptLocalScanEntriesValue>>
    fun getReceiptLocalScanEntries(
        localPoNo: String,
        limit: Int? = null
    ): LiveData<List<ReceiptLocalScanEntriesValue>>

    fun insertReceiptLocalScanEntries(receiptLocalScanEntriesValue: ReceiptLocalScanEntriesValue): Boolean
    fun deleteReceiptLocalScanEntry(receiptLocalScanEntriesValue: ReceiptLocalScanEntriesValue)
    fun updateReceiptLocalScanEntry(receiptLocalScanEntriesValue: ReceiptLocalScanEntriesValue)
    suspend fun getUnsycnReceiptLocalScanEntry(): List<ReceiptLocalScanEntriesValue>
    suspend fun clearReceiptLocalScanEntries()
    suspend fun checkSN(serialNo: String): Boolean
}

class ReceiptLocalRepositoryImpl(
    val dao: ReceiptLocalDao
) : ReceiptLocalRepository {
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    override fun getAllReceiptLocalHeader(employeeCode: String): LiveData<List<ReceiptLocalHeaderValue>> {
        return dao.getAllReceiptLocalHeader(employeeCode)
    }

    override fun getReceiptLocalHeader(documentNo: String): LiveData<ReceiptLocalHeaderValue> {
        return dao.getReceiptLocalHeader(documentNo)
    }

    override fun insertReceiptLocalHeader(receiptLocalHeaderValue: ReceiptLocalHeaderValue): Job =
        scope.launch(Dispatchers.IO) {
            dao.insertReceiptLocalHeader(receiptLocalHeaderValue)
        }

    override fun getCountReceiptLocalHeader(employeeCode: String): LiveData<Int> =
        dao.getCountReceiptLocalHeader(employeeCode)


    override suspend fun clearReceiptLocalHeader() {
        dao.clearReceiptLocalHeader()
    }

    override fun getAllReceiptLocalLine(documentNo: String): LiveData<List<ReceiptLocalLineValue>> {
        return dao.getAllReceiptLocalLine(documentNo)
    }

    override fun insertReceiptLocalLine(receiptLocalLineValue: ReceiptLocalLineValue): Job =
        scope.launch(Dispatchers.IO) {
            dao.insertReceiptLocalLine(receiptLocalLineValue)
        }

    override fun getReceiptLocalLineDetail(
        documentNo: String,
        partNo: String
    ): List<ReceiptLocalLineValue> {
        return dao.getReceiptLocalLineDetail(documentNo, partNo)
    }

    override suspend fun clearReceiptLocalLine() {
        dao.clearReceiptLocalLine()
    }

    override fun getAllReceiptLocalScanEntries(): LiveData<List<ReceiptLocalScanEntriesValue>> =
        runBlocking(Dispatchers.IO) {
            dao.getAllReceiptLocalScanEntries()
        }

    override fun insertReceiptLocalScanEntries(receiptLocalScanEntriesValue: ReceiptLocalScanEntriesValue): Boolean =
        runBlocking(Dispatchers.IO) {
            try {
                val localLineData = dao.getDetailReceiptLocalLineData(
                    receiptLocalScanEntriesValue.lineNo,
                    receiptLocalScanEntriesValue.partNo
                )
                if (localLineData.alredyScanned < localLineData.outstandingQuantity.toInt()) {
                    localLineData.apply {
                        this.alredyScanned = ++alredyScanned
                    }
                    dao.insertReceiptLocalScanEntries(receiptLocalScanEntriesValue)
                    dao.updateReceiptLocalLine(localLineData)
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                false
            }
        }


    override fun deleteReceiptLocalScanEntry(receiptLocalScanEntriesValue: ReceiptLocalScanEntriesValue) {
        scope.launch(Dispatchers.IO) {
            val localLineData = dao.getDetailReceiptLocalLineData(
                receiptLocalScanEntriesValue.lineNo,
                receiptLocalScanEntriesValue.partNo
            )
            localLineData.apply {
                this.alredyScanned = --alredyScanned
            }
            dao.deleteReceiptLocalScanEntry(receiptLocalScanEntriesValue)
            dao.updateReceiptLocalLine(localLineData)
        }

    }

    override fun updateReceiptLocalScanEntry(receiptLocalScanEntriesValue: ReceiptLocalScanEntriesValue) {
        dao.updateReceiptLocalScanEntry(receiptLocalScanEntriesValue)
    }

    override fun getReceiptLocalScanEntries(
        localPoNo: String,
        limit: Int?
    ): LiveData<List<ReceiptLocalScanEntriesValue>> =
        runBlocking {
            limit?.let {
                dao.getReceiptLocalScanEntries(localPoNo, it)
            } ?: dao.getReceiptLocalScanEntriesNoLimit(localPoNo)
        }

    override suspend fun getUnsycnReceiptLocalScanEntry(): List<ReceiptLocalScanEntriesValue> =
        dao.getUnsycnReceiptLocalScanEntry()

    override suspend fun clearReceiptLocalScanEntries() {
        dao.clearReceiptLocalScanEntries()
    }

    override suspend fun checkSN(serialNo: String): Boolean = dao.checkSN(serialNo).isEmpty()
}