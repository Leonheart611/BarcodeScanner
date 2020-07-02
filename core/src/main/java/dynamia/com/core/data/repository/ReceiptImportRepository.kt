package dynamia.com.core.data.repository

import androidx.lifecycle.LiveData
import dynamia.com.core.data.dao.ReceiptImportDao
import dynamia.com.core.data.model.ReceiptImportHeaderValue
import dynamia.com.core.data.model.ReceiptImportLineValue
import dynamia.com.core.data.model.ReceiptImportScanEntriesValue
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

interface ReceiptImportRepository {
    //ReceiptImportHeader------------------------------------------------------
    fun getAllReceiptImportHeader(employeeCode: String): LiveData<List<ReceiptImportHeaderValue>>
    fun insertReceiptImportHeader(receiptImportHeaderValue: ReceiptImportHeaderValue): Job
    fun getReceiptImportHeader(documentNo: String): LiveData<ReceiptImportHeaderValue>
    fun getCountReceiptImportHeader(employeeCode: String): LiveData<Int>
    fun clearReceiptImportHeader()

    //ReceiptImportLine------------------------------------------------------
    fun getAllReceiptImportLine(documentNo: String): LiveData<List<ReceiptImportLineValue>>
    fun insertReceiptImportLine(receiptImportLineValue: ReceiptImportLineValue): Job
    fun clearReceiptImportLine()
    fun getDetailImportLine(documentNo: String, partNo: String): List<ReceiptImportLineValue>

    //ReceiptImportScanEntries------------------------------------------------------
    fun getAllReceiptImportScanEntries(): LiveData<List<ReceiptImportScanEntriesValue>>
    fun insertReceiptImportScanEntries(receiptImportScanEntries: ReceiptImportScanEntriesValue): Job
    fun deleteReceiptImportScanEntry(receiptImportScanEntries: ReceiptImportScanEntriesValue)
    fun updateReceiptImportScanEntry(receiptImportScanEntries: ReceiptImportScanEntriesValue)
    fun getAllUnsycnImportScanEntry(): List<ReceiptImportScanEntriesValue>
    fun clearReceiptImportScanEntries()
}

class ReceiptImportRepositoryImpl(
    private val dao: ReceiptImportDao
) : ReceiptImportRepository {
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    override fun getAllReceiptImportHeader(employeeCode: String): LiveData<List<ReceiptImportHeaderValue>> =
        runBlocking {
            dao.getAllReceiptImportHeader(employeeCode)
        }

    override fun getReceiptImportHeader(documentNo: String): LiveData<ReceiptImportHeaderValue> {
        return dao.getReceiptImportHeader(documentNo)
    }

    override fun insertReceiptImportHeader(receiptImportHeaderValue: ReceiptImportHeaderValue): Job =
        scope.launch(Dispatchers.IO) {
            dao.insertReceiptImportHeader(receiptImportHeaderValue)
        }

    override fun getCountReceiptImportHeader(employeeCode: String): LiveData<Int> =
        dao.getCountReceiptImportHeader(employeeCode)

    override fun clearReceiptImportHeader() {
        dao.clearReceiptImportHeader()
    }

    override fun getAllReceiptImportLine(documentNo: String): LiveData<List<ReceiptImportLineValue>> {
        return dao.getAllReceiptImportLine(documentNo)
    }

    override fun insertReceiptImportLine(receiptImportLineValue: ReceiptImportLineValue): Job =
        scope.launch(Dispatchers.IO) {
            dao.insertReceiptImportLine(receiptImportLineValue)
        }

    override fun clearReceiptImportLine() {
        dao.clearReceiptImportLine()
    }

    override fun getDetailImportLine(
        documentNo: String,
        partNo: String
    ): List<ReceiptImportLineValue> {
        return dao.getDetailImportLine(documentNo, partNo)
    }

    override fun getAllReceiptImportScanEntries(): LiveData<List<ReceiptImportScanEntriesValue>> =
        runBlocking {
            dao.getAllReceiptImportScanEntries()
        }

    override fun insertReceiptImportScanEntries(receiptImportScanEntries: ReceiptImportScanEntriesValue): Job =
        scope.launch(Dispatchers.IO) {
            dao.insertReceiptImportScanEntries(receiptImportScanEntries)
        }

    override fun deleteReceiptImportScanEntry(receiptImportScanEntries: ReceiptImportScanEntriesValue) {
        dao.deleteReceiptImportScanEntry(receiptImportScanEntries)
    }

    override fun updateReceiptImportScanEntry(receiptImportScanEntries: ReceiptImportScanEntriesValue) {
        dao.updateReceiptImportScanEntry(receiptImportScanEntries)
    }

    override fun getAllUnsycnImportScanEntry(): List<ReceiptImportScanEntriesValue> = runBlocking {
        dao.getAllUnsycnImportScanEntry()
    }

    override fun clearReceiptImportScanEntries() {
        dao.clearReceiptImportScanEntries()
    }

}
