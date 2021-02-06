package dynamia.com.core.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dynamia.com.core.data.model.StockCount

@Dao
interface StockCountDao {
    @Query("SELECT * FROM StockCount")
    fun getAllStockCount(): LiveData<MutableList<StockCount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStockCount(data: StockCount)

    @Query("DELETE FROM StockCount")
    fun clearStockCount()

    @Query("SELECT * FROM StockCount WHERE sycn_status = 0")
    fun getAllUnsycnStockCount(): MutableList<StockCount>

    @Update
    fun updateStockCount(data: StockCount)

    @Query("SELECT * FROM StockCount WHERE Serial_No = :serialNo ORDER BY id DESC")
    fun checkSN(
        serialNo: String
    ): List<StockCount>

    @Delete
    fun deleteSN(data: StockCount)
}