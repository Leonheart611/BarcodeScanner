package dynamia.com.barcodescanner.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dynamia.com.barcodescanner.data.model.PickingListLineValue

interface PickingListLineDao {
    @Query("SELECT * FROM PickingListLine")
    fun getAllPickingListLine():LiveData<List<PickingListLineValue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPickingListLine(pickingListLineValue: PickingListLineValue)

    @Query("DELETE FROM PickingListLine")
    fun clearPickingListLine()
}