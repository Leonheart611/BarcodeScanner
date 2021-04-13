package dynamia.com.core.data.repository

import androidx.lifecycle.LiveData
import dynamia.com.core.data.dao.PickingListDao
import dynamia.com.core.data.model.PickingListHeaderValue
import dynamia.com.core.data.model.PickingListLineValue
import dynamia.com.core.data.model.PickingListScanEntriesValue
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.CoroutineContext

interface PickingListRepository {
    fun getAllPickingListHeader(employeeCode: String): LiveData<List<PickingListHeaderValue>>
    fun insertPickingListHeader(pickingListHeaderValue: PickingListHeaderValue): Job
    fun getCountPickingListHeader(employeeCode: String): LiveData<Int>
    suspend fun getPickingListHeader(picking_List_No: String): PickingListHeaderValue
    suspend fun getCheckEmptyOrNot(): Flow<Int>
    suspend fun clearPickingListHeader()

    suspend fun getAllPickingListLine(picking_List_No: String): Flow<List<PickingListLineValue>>
    fun insertPickingListLine(pickingListLineValue: PickingListLineValue): Job
    suspend fun getAllPickingListLineFromInsert(
        partNo: String,
        picking_List_No: String
    ): List<PickingListLineValue>

    suspend fun clearPickingListLine()

    fun getAllPickingListScanEntries(): Flow<List<PickingListScanEntriesValue>>
    fun getAllPickingListScanLiveData(): LiveData<List<PickingListScanEntriesValue>>
    fun getPickingListScanEntries(
        noPickingList: String,
        limit: Int? = null
    ): LiveData<List<PickingListScanEntriesValue>>

    suspend fun checkPickingListNoandSN(serialNo: String): Boolean
    fun insertPickingListScanEntries(pickingListScanEntriesValue: PickingListScanEntriesValue): Boolean
    fun deletePickingListScanEntries(pickingListScanEntriesValue: PickingListScanEntriesValue)
    fun updatePickingScanEntry(pickingListScanEntriesValue: PickingListScanEntriesValue)
    suspend fun clearPickingListScanEntries()
    fun getAllUnscynPickingListScanEntries(): MutableList<PickingListScanEntriesValue>
    fun getPickingListandPartNo(
        pickingListNo: String,
        partNo: String,
        lineNo: Int
    ): LiveData<List<PickingListScanEntriesValue>>
}

class PickingListRepositoryImpl(
    private val pickingListDao: PickingListDao
) : PickingListRepository {

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


    override suspend fun getPickingListHeader(picking_List_No: String): PickingListHeaderValue =
        pickingListDao.getPickingListHeader(picking_List_No)

    override suspend fun getCheckEmptyOrNot(): Flow<Int> = flow {
        emit(pickingListDao.getCheckEmptyOrNot())
    }


    override suspend fun clearPickingListHeader() {
        pickingListDao.clearPickingListHeader()
    }

    override suspend fun getAllPickingListLine(picking_List_No: String): Flow<List<PickingListLineValue>> =
        flow {
            emit(pickingListDao.getAllPickingListLine(picking_List_No))
        }


    override suspend fun getAllPickingListLineFromInsert(
        partNo: String,
        picking_List_No: String
    ): List<PickingListLineValue> =
        pickingListDao.getAllPickingListLineFromInsert(partNo, picking_List_No)


    override fun insertPickingListLine(pickingListLineValue: PickingListLineValue): Job =
        scope.launch(Dispatchers.IO) {
            pickingListDao.insertPickingListLine(pickingListLineValue)
        }

    override suspend fun clearPickingListLine() {
        pickingListDao.clearPickingListLine()
    }

    override fun getAllPickingListScanEntries(): Flow<List<PickingListScanEntriesValue>> =
        flow {
            pickingListDao.getAllPickingListScanEntries().value?.let { emit(it) }
        }


    override fun insertPickingListScanEntries(pickingListScanEntriesValue: PickingListScanEntriesValue): Boolean =
        runBlocking(Dispatchers.IO) {
            try {
                val pickingListData = pickingListDao.getPickingListDetail(
                    lineNo = pickingListScanEntriesValue.lineNo,
                    partNo = pickingListScanEntriesValue.partNo,
                    pickingListNo = pickingListScanEntriesValue.pickingListNo
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
                partNo = pickingListScanEntriesValue.partNo,
                pickingListNo = pickingListScanEntriesValue.pickingListNo
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

    override suspend fun clearPickingListScanEntries() {
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

    override suspend fun checkPickingListNoandSN(serialNo: String): Boolean =
        pickingListDao.checkPickingListSN(serialNo).isEmpty()

    override fun getAllUnscynPickingListScanEntries(): MutableList<PickingListScanEntriesValue> =
        runBlocking {
            pickingListDao.getAllUnscynPickingListScanEntries()
        }

    override fun getPickingListandPartNo(
        pickingListNo: String,
        partNo: String,
        lineNo: Int
    ): LiveData<List<PickingListScanEntriesValue>> = runBlocking {
        pickingListDao.getPickingListandPartNo(pickingListNo, partNo, lineNo)
    }

    override fun getAllPickingListScanLiveData(): LiveData<List<PickingListScanEntriesValue>> =
        pickingListDao.getAllPickingListScanEntries()
}

