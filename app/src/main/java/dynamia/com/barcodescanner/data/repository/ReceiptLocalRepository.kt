package dynamia.com.barcodescanner.data.repository

import androidx.lifecycle.LiveData
import dynamia.com.barcodescanner.data.dao.ReceiptLocalDao
import dynamia.com.barcodescanner.data.model.ReceiptLocalHeaderValue
import dynamia.com.barcodescanner.data.model.ReceiptLocalLineValue
import dynamia.com.barcodescanner.data.model.ReceiptLocalScanEntriesValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

interface ReceiptLocalRepository {
    //ReceiptLocalHeader--------------------------------------------------
    fun getAllReceiptLocalHeader(): LiveData<List<ReceiptLocalHeaderValue>>

    fun insertReceiptLocalHeader(receiptLocalHeaderValue: ReceiptLocalHeaderValue): Job
    fun getCountReceiptLocalHeader(): Int
    fun clearReceiptLocalHeader()

    //ReceiptLocalLineValue--------------------------------------------------
    fun getAllReceiptLocalLine(): LiveData<List<ReceiptLocalLineValue>>

    fun insertReceiptLocalLine(receiptLocalLineValue: ReceiptLocalLineValue): Job
    fun clearReceiptLocalLine()

    //ReceiptLocalScanEntriesValue--------------------------------------------------
    fun getAllReceiptLocalScanEntries(): LiveData<List<ReceiptLocalScanEntriesValue>>

    fun insertReceiptLocalScanEntries(receiptLocalScanEntriesValue: ReceiptLocalScanEntriesValue): Job
    fun clearReceiptLocalScanEntries()
}

class ReceiptLocalRepositoryImpl(private val dao: ReceiptLocalDao) : ReceiptLocalRepository {
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    override fun getAllReceiptLocalHeader(): LiveData<List<ReceiptLocalHeaderValue>> {
        return dao.getAllReceiptLocalHeader()
    }

    override fun insertReceiptLocalHeader(receiptLocalHeaderValue: ReceiptLocalHeaderValue): Job =
        scope.launch(Dispatchers.IO) {
            dao.insertReceiptLocalHeader(receiptLocalHeaderValue)
        }

    override fun getCountReceiptLocalHeader(): Int {
        return dao.getCountReceiptLocalHeader()
    }

    override fun clearReceiptLocalHeader() {
        dao.clearReceiptLocalHeader()
    }

    override fun getAllReceiptLocalLine(): LiveData<List<ReceiptLocalLineValue>> {
        return dao.getAllReceiptLocalLine()
    }

    override fun insertReceiptLocalLine(receiptLocalLineValue: ReceiptLocalLineValue): Job =
        scope.launch(Dispatchers.IO) {
            dao.insertReceiptLocalLine(receiptLocalLineValue)
        }

    override fun clearReceiptLocalLine() {
        dao.clearReceiptLocalLine()
    }

    override fun getAllReceiptLocalScanEntries(): LiveData<List<ReceiptLocalScanEntriesValue>> {
        return getAllReceiptLocalScanEntries()
    }

    override fun insertReceiptLocalScanEntries(receiptLocalScanEntriesValue: ReceiptLocalScanEntriesValue): Job =
        scope.launch(Dispatchers.IO) {
            dao.insertReceiptLocalScanEntries(receiptLocalScanEntriesValue)
        }

    override fun clearReceiptLocalScanEntries() {
        dao.clearReceiptLocalScanEntries()
    }
}