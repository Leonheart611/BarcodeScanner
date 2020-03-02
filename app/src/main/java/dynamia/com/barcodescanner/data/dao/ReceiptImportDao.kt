package dynamia.com.barcodescanner.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dynamia.com.barcodescanner.data.model.ReceiptImportHeaderValue
import dynamia.com.barcodescanner.data.model.ReceiptImportLineValue
import dynamia.com.barcodescanner.data.model.ReceiptImportScanEntriesValue

@Dao
interface ReceiptImportDao {
    //ReceiptImportHeader------------------------------------------------------
    @Query("SELECT * FROM ReceiptImportHeader")
    fun getAllReceiptImportHeader(): LiveData<List<ReceiptImportHeaderValue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReceiptImportHeader(receiptImportHeaderValue: ReceiptImportHeaderValue)

    @Query("SELECT count(*) from ReceiptImportHeader")
    fun getCountReceiptImportHeader(): Int

    @Query("DELETE FROM ReceiptImportHeader")
    fun clearReceiptImportHeader()

    //ReceiptImportLine------------------------------------------------------
    @Query("SELECT * FROM ReceiptImportLine")
    fun getAllReceiptImportLine(): LiveData<List<ReceiptImportLineValue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReceiptImportLine(receiptImportLineValue: ReceiptImportLineValue)

    @Query("DELETE FROM ReceiptImportHeader")
    fun clearReceiptImportLine()

    //ReceiptImportScanEntries------------------------------------------------------
    @Query("SELECT * FROM ReceiptImportScanEntries")
    fun getAllReceiptImportScanEntries(): LiveData<List<ReceiptImportScanEntriesValue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReceiptImportScanEntries(receiptImportScanEntries: ReceiptImportScanEntriesValue)

    @Query("DELETE FROM ReceiptImportScanEntries")
    fun clearReceiptImportScanEntries()
}