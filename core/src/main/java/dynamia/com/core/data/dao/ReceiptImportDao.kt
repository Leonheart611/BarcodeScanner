package dynamia.com.core.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dynamia.com.core.data.model.ReceiptImportHeaderValue
import dynamia.com.core.data.model.ReceiptImportLineValue
import dynamia.com.core.data.model.ReceiptImportScanEntriesValue

@Dao
interface ReceiptImportDao {
    //ReceiptImportHeader------------------------------------------------------
    @Query("SELECT * FROM ReceiptImportHeader")
    fun getAllReceiptImportHeader(): LiveData<List<ReceiptImportHeaderValue>>

    @Query("SELECT * FROM ReceiptImportHeader WHERE `no`= :documentNo")
    fun getReceiptImportHeader(documentNo:String):LiveData<ReceiptImportHeaderValue>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReceiptImportHeader(receiptImportHeaderValue: ReceiptImportHeaderValue)

    @Query("SELECT count(*) from ReceiptImportHeader")
    fun getCountReceiptImportHeader(): Int

    @Query("DELETE FROM ReceiptImportHeader")
    fun clearReceiptImportHeader()

    //ReceiptImportLine------------------------------------------------------
    @Query("SELECT * FROM ReceiptImportLine WHERE documentNo = :documentNo")
    fun getAllReceiptImportLine(documentNo: String): LiveData<List<ReceiptImportLineValue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReceiptImportLine(receiptImportLineValue: ReceiptImportLineValue)

    @Query("DELETE FROM ReceiptImportHeader")
    fun clearReceiptImportLine()

    @Query("SELECT * FROM ReceiptImportLine WHERE documentNo = :documentNo AND itemNo =:partNo")
    fun getDetailImportLine(documentNo: String,partNo:String):List<ReceiptImportLineValue>

    //ReceiptImportScanEntries------------------------------------------------------
    @Query("SELECT * FROM ReceiptImportScanEntries")
    fun getAllReceiptImportScanEntries(): LiveData<List<ReceiptImportScanEntriesValue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReceiptImportScanEntries(receiptImportScanEntries: ReceiptImportScanEntriesValue)

    @Delete
    fun deleteReceiptImportScanEntry(receiptImportScanEntries: ReceiptImportScanEntriesValue)

    @Update
    fun updateReceiptImportScanEntry(receiptImportScanEntries: ReceiptImportScanEntriesValue)

    @Query("DELETE FROM ReceiptImportScanEntries")
    fun clearReceiptImportScanEntries()
}