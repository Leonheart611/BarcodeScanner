package dynamia.com.barcodescanner.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dynamia.com.barcodescanner.data.model.ReceiptLocalHeaderValue
import dynamia.com.barcodescanner.data.model.ReceiptLocalLineValue
import dynamia.com.barcodescanner.data.model.ReceiptLocalScanEntriesValue

@Dao
interface ReceiptLocalDao {
    //ReceiptLocalHeader--------------------------------------------------
    @Query("SELECT * FROM ReceiptLocalHeader")
    fun getAllReceiptLocalHeader(): LiveData<List<ReceiptLocalHeaderValue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReceiptLocalHeader(receiptLocalHeaderValue: ReceiptLocalHeaderValue)

    @Query("SELECT count(*) from ReceiptLocalHeader")
    fun getCountReceiptLocalHeader(): Int

    @Query("DELETE FROM ReceiptLocalHeader")
    fun clearReceiptLocalHeader()

    //ReceiptLocalLineValue--------------------------------------------------
    @Query("SELECT * FROM ReceiptLocalLine")
    fun getAllReceiptLocalLine(): LiveData<List<ReceiptLocalLineValue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReceiptLocalLine(receiptLocalLineValue: ReceiptLocalLineValue)

    @Query("DELETE FROM ReceiptLocalHeader")
    fun clearReceiptLocalLine()

    //ReceiptLocalScanEntriesValue--------------------------------------------------
    @Query("SELECT * FROM ReceiptLocalScanEntries")
    fun getAllReceiptLocalScanEntries(): LiveData<List<ReceiptLocalScanEntriesValue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReceiptLocalScanEntries(receiptLocalScanEntriesValue: ReceiptLocalScanEntriesValue)

    @Query("DELETE FROM ReceiptLocalHeader")
    fun clearReceiptLocalScanEntries()
}