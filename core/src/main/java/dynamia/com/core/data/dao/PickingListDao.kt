package dynamia.com.core.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dynamia.com.core.data.model.PickingListHeaderValue
import dynamia.com.core.data.model.PickingListLineValue
import dynamia.com.core.data.model.PickingListScanEntriesValue

@Dao
interface PickingListDao {
    //PickingListHeader---------------------------------------------------------
    @Query("SELECT * FROM PickingListHeader WHERE employeeCode = :employeeCode COLLATE NOCASE")
    fun getAllPickingListHeader(employeeCode: String): LiveData<List<PickingListHeaderValue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPickingListHeader(pickingListHeaderValue: PickingListHeaderValue)

    @Query("SELECT * FROM PickingListHeader WHERE pickingListNo = :picking_List_No")
    fun getPickingListHeader(picking_List_No: String): PickingListHeaderValue

    @Query("SELECT count(*) from PickingListHeader WHERE employeeCode = :employeeCode COLLATE NOCASE")
    fun getCountPickingListHeader(employeeCode: String): LiveData<Int>

    @Query("SELECT count(*) from PickingListHeader WHERE employeeCode = :employeeCode COLLATE NOCASE")
    fun getCheckEmptyOrNot(employeeCode: String): Int

    @Query("DELETE FROM PickingListHeader")
    fun clearPickingListHeader()

    //PickingListLine---------------------------------------------------------
    @Query("SELECT * FROM PickingListLine WHERE pickingListNo = :picking_List_No")
    fun getAllPickingListLine(picking_List_No: String): LiveData<List<PickingListLineValue>>

    @Update
    fun updatePickingListLine(pickingListLineValue: PickingListLineValue)

    @Query("SELECT * FROM PickingListLine WHERE partNoOriginal = :partNo AND pickingListNo = :picking_List_No")
    fun getAllPickingListLineFromInsert(
        partNo: String,
        picking_List_No: String
    ): List<PickingListLineValue>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPickingListLine(pickingListLineValue: PickingListLineValue)

    @Query("SELECT * FROM PickingListLine WHERE lineNo = :lineNo AND partNoOriginal = :partNo")
    fun getPickingListDetail(lineNo: Int, partNo: String): PickingListLineValue

    @Query("DELETE FROM PickingListLine")
    fun clearPickingListLine()

    //PickingListScanEntries---------------------------------------------------------
    @Query("SELECT * FROM PickingListScanEntries")
    fun getAllPickingListScanEntries(): LiveData<List<PickingListScanEntriesValue>>

    @Query("SELECT * FROM PickingListScanEntries WHERE pickingListNo = :noPickingList ORDER BY id DESC LIMIT :limit  ")
    fun getPickingListScanEntries(
        noPickingList: String,
        limit: Int?
    ): LiveData<List<PickingListScanEntriesValue>>

    @Query("SELECT * FROM PickingListScanEntries WHERE pickingListNo = :noPickingList ORDER BY id DESC")
    fun getPickingListScanEntriesNoLimit(noPickingList: String): LiveData<List<PickingListScanEntriesValue>>

    @Query("SELECT * FROM PickingListScanEntries WHERE pickingListNo = :noPickingList AND serialNo = :serialNo AND partNo =:partNo ORDER BY id DESC")
    fun checkPickingListNoandSN(
        noPickingList: String,
        serialNo: String,
        partNo: String
    ): List<PickingListScanEntriesValue>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPickingListScanEntries(pickingListScanEntriesValue: PickingListScanEntriesValue)

    @Delete
    fun deletePickingListScanEntries(pickingListScanEntriesValue: PickingListScanEntriesValue)

    @Update
    fun updatePickingScanEntry(pickingListScanEntriesValue: PickingListScanEntriesValue)

    @Query("DELETE FROM PickingListScanEntries")
    fun clearPickingListScanEntries()

    @Query("SELECT * FROM PickingListScanEntries WHERE sycn_status = 0")
    fun getAllUnscynPickingListScanEntries(): MutableList<PickingListScanEntriesValue>
}