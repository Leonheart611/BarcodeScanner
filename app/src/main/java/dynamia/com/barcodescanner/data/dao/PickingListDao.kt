package dynamia.com.barcodescanner.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dynamia.com.barcodescanner.data.model.PickingListHeaderValue
import dynamia.com.barcodescanner.data.model.PickingListLineValue
import dynamia.com.barcodescanner.data.model.PickingListScanEntriesValue

@Dao
interface PickingListDao {
    //PickingListHeader---------------------------------------------------------
    @Query("SELECT * FROM PickingListHeader")
    fun getAllPickingListHeader(): LiveData<List<PickingListHeaderValue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPickingListHeader(pickingListHeaderValue: PickingListHeaderValue)

    @Query("SELECT * FROM PickingListHeader WHERE pickingListNo = :picking_List_No")
    fun getPickingListHeader(picking_List_No:String): PickingListHeaderValue

    @Query("SELECT count(*) from PickingListHeader")
    fun getCountPickingListHeader(): Int

    @Query("DELETE FROM PickingListHeader")
    fun clearPickingListHeader()

    //PickingListLine---------------------------------------------------------
    @Query("SELECT * FROM PickingListLine WHERE pickingListNo = :picking_List_No")
    fun getAllPickingListLine(picking_List_No:String): LiveData<List<PickingListLineValue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPickingListLine(pickingListLineValue: PickingListLineValue)

    @Query("DELETE FROM PickingListLine")
    fun clearPickingListLine()

    //PickingListScanEntries---------------------------------------------------------
    @Query("SELECT * FROM PickingListScanEntries")
    fun getAllPickingListScanEntries(): LiveData<List<PickingListScanEntriesValue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPickingListScanEntries(pickingListScanEntriesValue: PickingListScanEntriesValue)

    @Query("DELETE FROM PickingListScanEntries")
    fun clearPickingListScanEntries()
}