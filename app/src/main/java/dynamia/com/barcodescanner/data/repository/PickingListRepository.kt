package dynamia.com.barcodescanner.data.repository

import androidx.lifecycle.LiveData
import dynamia.com.barcodescanner.data.dao.PickingListDao
import dynamia.com.barcodescanner.data.model.PickingListHeaderValue
import dynamia.com.barcodescanner.data.model.PickingListLineValue
import dynamia.com.barcodescanner.data.model.PickingListScanEntriesValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

interface PickingListRepository {
    fun getAllPickingListHeader(): LiveData<List<PickingListHeaderValue>>
    fun insertPickingListHeader(pickingListHeaderValue: PickingListHeaderValue): Job
    fun getCountPickingListHeader(): Int
    fun clearPickingListHeader()

    fun getAllPickingListLine(): LiveData<List<PickingListLineValue>>
    fun insertPickingListLine(pickingListLineValue: PickingListLineValue): Job
    fun clearPickingListLine()

    fun getAllPickingListScanEntries(): LiveData<List<PickingListScanEntriesValue>>
    fun insertPickingListScanEntries(pickingListScanEntriesValue: PickingListScanEntriesValue): Job
    fun clearPickingListScanEntries()
}

class PickingListRepositoryImpl(private val pickingListDao: PickingListDao) :
    PickingListRepository {
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    override fun getAllPickingListHeader(): LiveData<List<PickingListHeaderValue>> {
        return pickingListDao.getAllPickingListHeader()
    }

    override fun insertPickingListHeader(pickingListHeaderValue: PickingListHeaderValue): Job =
        scope.launch(Dispatchers.IO) {
            pickingListDao.insertPickingListHeader(pickingListHeaderValue)
        }

    override fun getCountPickingListHeader(): Int {
        return pickingListDao.getCountPickingListHeader()
    }

    override fun clearPickingListHeader() {
        pickingListDao.clearPickingListHeader()
    }

    override fun getAllPickingListLine(): LiveData<List<PickingListLineValue>> {
        return pickingListDao.getAllPickingListLine()
    }

    override fun insertPickingListLine(pickingListLineValue: PickingListLineValue): Job =
        scope.launch(Dispatchers.IO) {
            pickingListDao.insertPickingListLine(pickingListLineValue)
        }

    override fun clearPickingListLine() {
        pickingListDao.clearPickingListLine()
    }

    override fun getAllPickingListScanEntries(): LiveData<List<PickingListScanEntriesValue>> {
        return pickingListDao.getAllPickingListScanEntries()
    }

    override fun insertPickingListScanEntries(pickingListScanEntriesValue: PickingListScanEntriesValue): Job =
        scope.launch(Dispatchers.IO) {
            pickingListDao.insertPickingListScanEntries(pickingListScanEntriesValue)
        }

    override fun clearPickingListScanEntries() {
        pickingListDao.clearPickingListScanEntries()
    }
}

