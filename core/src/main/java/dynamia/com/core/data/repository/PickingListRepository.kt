package dynamia.com.core.data.repository

import androidx.lifecycle.LiveData
import dynamia.com.core.data.dao.PickingListDao
import dynamia.com.core.data.model.PickingListHeaderValue
import dynamia.com.core.data.model.PickingListLineValue
import dynamia.com.core.data.model.PickingListScanEntriesValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

interface PickingListRepository {
    fun getAllPickingListHeader(): LiveData<List<PickingListHeaderValue>>
    fun insertPickingListHeader(pickingListHeaderValue: PickingListHeaderValue): Job
    fun getCountPickingListHeader(): Int
    fun getPickingListHeader(picking_List_No:String): PickingListHeaderValue
    fun clearPickingListHeader()

    fun getAllPickingListLine(picking_List_No:String): LiveData<List<PickingListLineValue>>
    fun insertPickingListLine(pickingListLineValue: PickingListLineValue): Job
    fun getAllPickingListLineFromInsert(partNo:String,picking_List_No: String):List<PickingListLineValue>
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

    override fun getPickingListHeader(picking_List_No: String): PickingListHeaderValue {
        return pickingListDao.getPickingListHeader(picking_List_No)
    }

    override fun clearPickingListHeader() {
        pickingListDao.clearPickingListHeader()
    }

    override fun getAllPickingListLine(picking_List_No: String): LiveData<List<PickingListLineValue>> {
        return pickingListDao.getAllPickingListLine(picking_List_No)
    }

    override fun getAllPickingListLineFromInsert(partNo: String, picking_List_No: String): List<PickingListLineValue> {
        return pickingListDao.getAllPickingListLineFromInsert(partNo,picking_List_No)
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

