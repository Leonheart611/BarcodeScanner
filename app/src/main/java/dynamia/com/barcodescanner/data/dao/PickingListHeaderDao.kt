package dynamia.com.barcodescanner.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dynamia.com.barcodescanner.data.model.PickingListHeaderValue

@Dao
interface PickingListHeaderDao {
    @Query("SELECT * FROM PickingListHeader")
    fun getAllPickingListHeader(): LiveData<List<PickingListHeaderValue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPickingListHeader(pickingListHeaderValue: PickingListHeaderValue)

    @Query("DELETE FROM PickingListHeader")
    fun clearPickingListHeader()
}