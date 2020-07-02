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
    fun clearReceiptLocalHeader()

    //ReceiptLocalLineValue--------------------------------------------------
    fun getAllReceiptLocalLine(documentNo: String): LiveData<List<ReceiptLocalLineValue>>
    fun insertReceiptLocalLine(receiptLocalLineValue: ReceiptLocalLineValue): Job
    fun getReceiptLocalLineDetail(documentNo: String, partNo: String): List<ReceiptLocalLineValue>
    fun clearReceiptLocalLine()

    //ReceiptLocalScanEntriesValue--------------------------------------------------
    fun getAllReceiptLocalScanEntries(): LiveData<List<ReceiptLocalScanEntriesValue>>
    fun insertReceiptLocalScanEntries(receiptLocalScanEntriesValue: ReceiptLocalScanEntriesValue): Job
    fun deleteReceiptLocalScanEntry(receiptLocalScanEntriesValue: ReceiptLocalScanEntriesValue)
    fun updateReceiptLocalScanEntry(receiptLocalScanEntriesValue: ReceiptLocalScanEntriesValue)
    fun getUnsycnReceiptLocalScanEntry(): List<ReceiptLocalScanEntriesValue>
    fun clearReceiptLocalScanEntries()
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


    override fun clearReceiptLocalHeader() {
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

    override fun clearReceiptLocalLine() {
        dao.clearReceiptLocalLine()
    }

    override fun getAllReceiptLocalScanEntries(): LiveData<List<ReceiptLocalScanEntriesValue>> =
        runBlocking {
            dao.getAllReceiptLocalScanEntries()
        }

    override fun insertReceiptLocalScanEntries(receiptLocalScanEntriesValue: ReceiptLocalScanEntriesValue): Job =
        scope.launch(Dispatchers.IO) {
            dao.insertReceiptLocalScanEntries(receiptLocalScanEntriesValue)
        }

    override fun deleteReceiptLocalScanEntry(receiptLocalScanEntriesValue: ReceiptLocalScanEntriesValue) {
        dao.deleteReceiptLocalScanEntry(receiptLocalScanEntriesValue)
    }

    override fun updateReceiptLocalScanEntry(receiptLocalScanEntriesValue: ReceiptLocalScanEntriesValue) {
        dao.updateReceiptLocalScanEntry(receiptLocalScanEntriesValue)
    }

    override fun getUnsycnReceiptLocalScanEntry(): List<ReceiptLocalScanEntriesValue> =
        runBlocking {
            dao.getUnsycnReceiptLocalScanEntry()
        }

    override fun clearReceiptLocalScanEntries() {
        dao.clearReceiptLocalScanEntries()
    }
}