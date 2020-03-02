package dynamia.com.barcodescanner.data.repository

import androidx.lifecycle.LiveData
import dynamia.com.barcodescanner.data.dao.ReceiptImportDao
import dynamia.com.barcodescanner.data.model.ReceiptImportHeaderValue
import dynamia.com.barcodescanner.data.model.ReceiptImportLineValue
import dynamia.com.barcodescanner.data.model.ReceiptImportScanEntriesValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

interface ReceiptImportRepository {
    //ReceiptImportHeader------------------------------------------------------
    fun getAllReceiptImportHeader(): LiveData<List<ReceiptImportHeaderValue>>
    fun insertReceiptImportHeader(receiptImportHeaderValue: ReceiptImportHeaderValue): Job
    fun getCountReceiptImportHeader(): Int
    fun clearReceiptImportHeader()

    //ReceiptImportLine------------------------------------------------------
    fun getAllReceiptImportLine(): LiveData<List<ReceiptImportLineValue>>

    fun insertReceiptImportLine(receiptImportLineValue: ReceiptImportLineValue): Job
    fun clearReceiptImportLine()

    //ReceiptImportScanEntries------------------------------------------------------
    fun getAllReceiptImportScanEntries(): LiveData<List<ReceiptImportScanEntriesValue>>

    fun insertReceiptImportScanEntries(receiptImportScanEntries: ReceiptImportScanEntriesValue): Job
    fun clearReceiptImportScanEntries()
}

class ReceiptImportRepositoryImpl(private val dao: ReceiptImportDao) : ReceiptImportRepository {
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    override fun getAllReceiptImportHeader(): LiveData<List<ReceiptImportHeaderValue>> {
        return dao.getAllReceiptImportHeader()
    }

    override fun insertReceiptImportHeader(receiptImportHeaderValue: ReceiptImportHeaderValue): Job =
        scope.launch(Dispatchers.IO) {
            dao.insertReceiptImportHeader(receiptImportHeaderValue)
        }

    override fun getCountReceiptImportHeader(): Int {
        return dao.getCountReceiptImportHeader()
    }

    override fun clearReceiptImportHeader() {
        dao.clearReceiptImportHeader()
    }

    override fun getAllReceiptImportLine(): LiveData<List<ReceiptImportLineValue>> {
        return dao.getAllReceiptImportLine()
    }

    override fun insertReceiptImportLine(receiptImportLineValue: ReceiptImportLineValue): Job =
        scope.launch(Dispatchers.IO) {
            dao.insertReceiptImportLine(receiptImportLineValue)
        }

    override fun clearReceiptImportLine() {
        dao.clearReceiptImportLine()
    }

    override fun getAllReceiptImportScanEntries(): LiveData<List<ReceiptImportScanEntriesValue>> {
        return dao.getAllReceiptImportScanEntries()
    }

    override fun insertReceiptImportScanEntries(receiptImportScanEntries: ReceiptImportScanEntriesValue): Job =
        scope.launch(Dispatchers.IO) {
            dao.insertReceiptImportScanEntries(receiptImportScanEntries)
        }

    override fun clearReceiptImportScanEntries() {
        dao.clearReceiptImportScanEntries()
    }

}
