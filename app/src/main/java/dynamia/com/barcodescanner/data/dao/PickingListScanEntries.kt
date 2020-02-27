package dynamia.com.barcodescanner.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dynamia.com.barcodescanner.data.model.PickingListScanEntriesValue

@Dao
interface PickingListScanEntries {
    @Query("SELECT * FROM PickingListScanEntries")
    fun getAllPickingListScanEntries(): LiveData<List<PickingListScanEntriesValue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPickingListScanEntries(pickingListScanEntriesValue: PickingListScanEntriesValue)

    @Query("DELETE FROM PickingListScanEntries")
    fun clearPickingListScanEntries()
}