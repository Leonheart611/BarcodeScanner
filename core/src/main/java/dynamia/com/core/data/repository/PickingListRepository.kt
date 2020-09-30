package dynamia.com.core.data.repository

import androidx.lifecycle.LiveData
import dynamia.com.core.data.dao.PickingListDao
import dynamia.com.core.data.model.PickingListHeaderValue
import dynamia.com.core.data.model.PickingListLineValue
import dynamia.com.core.data.model.PickingListScanEntriesValue
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

interface PickingListRepository {
    fun getAllPickingListHeader(employeeCode: String): LiveData<List<PickingListHeaderValue>>
    fun insertPickingListHeader(pickingListHeaderValue: PickingListHeaderValue): Job
    fun getCountPickingListHeader(employeeCode: String): LiveData<Int>
    fun getPickingListHeader(picking_List_No: String): PickingListHeaderValue
    fun getCheckEmptyOrNot(employeeCode: String): Boolean
    fun clearPickingListHeader()

    fun getAllPickingListLine(picking_List_No: String): LiveData<List<PickingListLineValue>>
    fun insertPickingListLine(pickingListLineValue: PickingListLineValue): Job
    fun getAllPickingListLineFromInsert(
        partNo: String,
        picking_List_No: String
    ): List<PickingListLineValue>

    fun clearPickingListLine()

    fun getAllPickingListScanEntries(): LiveData<List<PickingListScanEntriesValue>>
    fun getPickingListScanEntries(
        noPickingList: String,
        limit: Int? = null
    ): LiveData<List<PickingListScanEntriesValue>>

    fun checkPickingListNoandSN(noPickingList: String, serialNo: String, partNo: String): Boolean
    fun insertPickingListScanEntries(pickingListScanEntriesValue: PickingListScanEntriesValue): Boolean
    fun deletePickingListScanEntries(pickingListScanEntriesValue: PickingListScanEntriesValue)
    fun updatePickingScanEntry(pickingListScanEntriesValue: PickingListScanEntriesValue)
    fun clearPickingListScanEntries()
    fun getAllUnscynPickingListScanEntries(): MutableList<PickingListScanEntriesValue>
}

class PickingListRepositoryImpl(
    private val pickingListDao: PickingListDao
) :
    PickingListRepository {
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)


    override fun getAllPickingListHeader(employeeCode: String): LiveData<List<PickingListHeaderValue>> =
        runBlocking {
            pickingListDao.getAllPickingListHeader(employeeCode)
        }


    override fun insertPickingListHeader(pickingListHeaderValue: PickingListHeaderValue): Job =
        scope.launch(Dispatchers.IO) {
            pickingListDao.insertPickingListHeader(pickingListHeaderValue)
        }

    override fun getCountPickingListHeader(employeeCode: String): LiveData<Int> =
        pickingListDao.getCountPickingListHeader(employeeCode)


    override fun getPickingListHeader(picking_List_No: String): PickingListHeaderValue =
        runBlocking {
            pickingListDao.getPickingListHeader(picking_List_No)
        }

    override fun getCheckEmptyOrNot(employeeCode: String): Boolean =
        pickingListDao.getCheckEmptyOrNot(employeeCode) == 0


    override fun clearPickingListHeader() {
        pickingListDao.clearPickingListHeader()
    }

    override fun getAllPickingListLine(picking_List_No: String): LiveData<List<PickingListLineValue>> =
        runBlocking {
            pickingListDao.getAllPickingListLine(picking_List_No)
        }


    override fun getAllPickingListLineFromInsert(
        partNo: String,
        picking_List_No: String
    ): List<PickingListLineValue> = runBlocking {
        pickingListDao.getAllPickingListLineFromInsert(partNo, picking_List_No)
    }


    override fun insertPickingListLine(pickingListLineValue: PickingListLineValue): Job =
        scope.launch(Dispatchers.IO) {
            pickingListDao.insertPickingListLine(pickingListLineValue)
        }

    override fun clearPickingListLine() {
        pickingListDao.clearPickingListLine()
    }

    override fun getAllPickingListScanEntries(): LiveData<List<PickingListScanEntriesValue>> =
        runBlocking {
            pickingListDao.getAllPickingListScanEntries()
        }


    override fun insertPickingListScanEntries(pickingListScanEntriesValue: PickingListScanEntriesValue): Boolean =
        runBlocking(Dispatchers.IO) {
            try {
                val pickingListData = pickingListDao.getPickingListDetail(
                    lineNo = pickingListScanEntriesValue.lineNo,
                    partNo = pickingListScanEntriesValue.partNo
                )
                if (pickingListData.alreadyPickup < pickingListData.outstandingQuantity.toInt()) {
                    pickingListData.apply {
                        this.alreadyPickup = ++alreadyPickup
                    }
                    pickingListDao.insertPickingListScanEntries(pickingListScanEntriesValue)
                    pickingListDao.updatePickingListLine(pickingListData)
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                false
            }
        }

    override fun deletePickingListScanEntries(pickingListScanEntriesValue: PickingListScanEntriesValue) {
        scope.launch(Dispatchers.IO) {
            pickingListDao.deletePickingListScanEntries(pickingListScanEntriesValue)
            val pickingListData = pickingListDao.getPickingListDetail(
                lineNo = pickingListScanEntriesValue.lineNo,
                partNo = pickingListScanEntriesValue.partNo
            )
            pickingListData.apply {
                this.alreadyPickup = --alreadyPickup
            }
            pickingListDao.updatePickingListLine(pickingListData)
        }

    }

    override fun updatePickingScanEntry(pickingListScanEntriesValue: PickingListScanEntriesValue) {
        pickingListDao.updatePickingScanEntry(pickingListScanEntriesValue)
    }

    override fun clearPickingListScanEntries() {
        pickingListDao.clearPickingListScanEntries()
    }

    override fun getPickingListScanEntries(
        noPickingList: String,
        limit: Int?
    ): LiveData<List<PickingListScanEntriesValue>> =
        runBlocking {
            limit?.let {
                pickingListDao.getPickingListScanEntries(noPickingList, it)
            } ?: pickingListDao.getPickingListScanEntriesNoLimit(noPickingList)
        }

    override fun checkPickingListNoandSN(
        noPickingList: String,
        serialNo: String,
        partNo: String
    ): Boolean =
        runBlocking {
            pickingListDao.checkPickingListNoandSN(noPickingList, serialNo, partNo).isEmpty()
        }

    override fun getAllUnscynPickingListScanEntries(): MutableList<PickingListScanEntriesValue> =
        runBlocking {
            pickingListDao.getAllUnscynPickingListScanEntries()
        }
}

