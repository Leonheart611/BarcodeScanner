package dynamia.com.barcodescanner.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dynamia.com.barcodescanner.data.model.ReceiptLocalScanEntriesValue

interface ReceiptLocalScanEntriesDao {
    @Query("SELECT * FROM ReceiptLocalScanEntries")
    fun getAllReceiptLocalScanEntries(): LiveData<List<ReceiptLocalScanEntriesValue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReceiptLocalScanEntries(receiptLocalScanEntriesValue: ReceiptLocalScanEntriesValue)

    @Query("DELETE FROM ReceiptLocalHeader")
    fun clearReceiptLocalScanEntries()
}