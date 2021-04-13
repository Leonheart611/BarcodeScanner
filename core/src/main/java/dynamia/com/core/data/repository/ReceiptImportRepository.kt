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
    suspend fun clearReceiptImportHeader()

    //ReceiptImportLine------------------------------------------------------
    fun getAllReceiptImportLine(documentNo: String): LiveData<List<ReceiptImportLineValue>>
    fun insertReceiptImportLine(receiptImportLineValue: ReceiptImportLineValue): Job
    suspend fun clearReceiptImportLine()
    suspend fun getDetailImportLine(
        documentNo: String,
        partNo: String
    ): List<ReceiptImportLineValue>

    //ReceiptImportScanEntries------------------------------------------------------
    fun getAllReceiptImportScanEntries(): LiveData<List<ReceiptImportScanEntriesValue>>
    fun getReceiptImportScanEntries(
        importPoNo: String,
        limit: Int? = null
    ): LiveData<List<ReceiptImportScanEntriesValue>>

    fun getFilteredImportScanEntries(
        documentNo: String,
        lineNo: Int,
        partNo: String
    ): LiveData<List<ReceiptImportScanEntriesValue>>

    suspend fun checkSN(serialNo: String): Boolean
    fun insertReceiptImportScanEntries(receiptImportScanEntries: ReceiptImportScanEntriesValue): Boolean
    fun deleteReceiptImportScanEntry(receiptImportScanEntries: ReceiptImportScanEntriesValue)
    fun updateReceiptImportScanEntry(receiptImportScanEntries: ReceiptImportScanEntriesValue)
    fun getAllUnsycnImportScanEntry(): List<ReceiptImportScanEntriesValue>
    suspend fun clearReceiptImportScanEntries()
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

    override suspend fun clearReceiptImportHeader() {
        dao.clearReceiptImportHeader()
    }

    override fun getAllReceiptImportLine(documentNo: String): LiveData<List<ReceiptImportLineValue>> {
        return dao.getAllReceiptImportLine(documentNo)
    }

    override fun insertReceiptImportLine(receiptImportLineValue: ReceiptImportLineValue): Job =
        scope.launch(Dispatchers.IO) {
            dao.insertReceiptImportLine(receiptImportLineValue)
        }

    override suspend fun clearReceiptImportLine() {
        dao.clearReceiptImportLine()
    }

    override suspend fun getDetailImportLine(
        documentNo: String,
        partNo: String
    ): List<ReceiptImportLineValue> = dao.getDetailImportLine(documentNo, partNo)


    override fun getAllReceiptImportScanEntries(): LiveData<List<ReceiptImportScanEntriesValue>> =
        runBlocking {
            dao.getAllReceiptImportScanEntries()
        }

    override fun insertReceiptImportScanEntries(receiptImportScanEntries: ReceiptImportScanEntriesValue): Boolean =
        runBlocking(Dispatchers.IO) {
            try {
                val importLineValue = dao.getDetailImportLineData(
                    receiptImportScanEntries.lineNo,
                    receiptImportScanEntries.partNo,
                    receiptImportScanEntries.documentNo
                )
                if (importLineValue.alreadyScanned < importLineValue.outstandingQuantity.toInt()) {
                    importLineValue.apply {
                        this.alreadyScanned = ++alreadyScanned
                    }
                    dao.insertReceiptImportScanEntries(receiptImportScanEntries)
                    dao.updateImportLineData(importLineValue)
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                false
            }
        }

    override fun deleteReceiptImportScanEntry(receiptImportScanEntries: ReceiptImportScanEntriesValue) {
        scope.launch(Dispatchers.IO) {
            dao.deleteReceiptImportScanEntry(receiptImportScanEntries)
            val importLineValue = dao.getDetailImportLineData(
                receiptImportScanEntries.lineNo,
                receiptImportScanEntries.partNo,
                receiptImportScanEntries.documentNo
            )
            importLineValue.apply {
                this.alreadyScanned = --alreadyScanned
            }
            dao.updateImportLineData(importLineValue)
        }
    }

    override fun updateReceiptImportScanEntry(receiptImportScanEntries: ReceiptImportScanEntriesValue) {
        dao.updateReceiptImportScanEntry(receiptImportScanEntries)
    }

    override fun getReceiptImportScanEntries(
        importPoNo: String,
        limit: Int?
    ): LiveData<List<ReceiptImportScanEntriesValue>> =
        runBlocking {
            limit?.let {
                dao.getReceiptImportScanEntries(importPoNo, it)
            } ?: dao.getReceiptImportScanEntriesNoLimit(importPoNo)
        }

    override fun getAllUnsycnImportScanEntry(): List<ReceiptImportScanEntriesValue> = runBlocking {
        dao.getAllUnsycnImportScanEntry()
    }

    override suspend fun clearReceiptImportScanEntries() {
        dao.clearReceiptImportScanEntries()
    }

    override suspend fun checkSN(serialNo: String): Boolean = dao.checkSN(serialNo).isEmpty()


    override fun getFilteredImportScanEntries(
        documentNo: String,
        lineNo: Int,
        partNo: String
    ): LiveData<List<ReceiptImportScanEntriesValue>> =
        dao.getFilteredImportScanEntries(documentNo, lineNo, partNo)


}
