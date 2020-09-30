package dynamia.com.core.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dynamia.com.core.data.model.ReceiptLocalHeaderValue
import dynamia.com.core.data.model.ReceiptLocalLineValue
import dynamia.com.core.data.model.ReceiptLocalScanEntriesValue

@Dao
interface ReceiptLocalDao {
    //ReceiptLocalHeader--------------------------------------------------
    @Query("SELECT * FROM ReceiptLocalHeader WHERE employeeCode =:employeeCode COLLATE NOCASE")
    fun getAllReceiptLocalHeader(employeeCode: String): LiveData<List<ReceiptLocalHeaderValue>>

    @Query("SELECT * FROM ReceiptLocalHeader WHERE `no`= :documentNo")
    fun getReceiptLocalHeader(documentNo: String): LiveData<ReceiptLocalHeaderValue>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReceiptLocalHeader(receiptLocalHeaderValue: ReceiptLocalHeaderValue)

    @Query("SELECT count(*) from ReceiptLocalHeader WHERE employeeCode =:employeeCode COLLATE NOCASE")
    fun getCountReceiptLocalHeader(employeeCode: String): LiveData<Int>

    @Query("DELETE FROM ReceiptLocalHeader")
    fun clearReceiptLocalHeader()

    //ReceiptLocalLineValue--------------------------------------------------
    @Query("SELECT * FROM ReceiptLocalLine WHERE documentNo =:documentNo")
    fun getAllReceiptLocalLine(documentNo: String): LiveData<List<ReceiptLocalLineValue>>

    @Query("SELECT * FROM ReceiptLocalLine WHERE documentNo =:documentNo AND partNo=:partNo")
    fun getReceiptLocalLineDetail(documentNo: String, partNo: String): List<ReceiptLocalLineValue>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReceiptLocalLine(receiptLocalLineValue: ReceiptLocalLineValue)

    @Query("SELECT * FROM ReceiptLocalLine WHERE lineNo= :lineNo AND partNo = :partNo")
    fun getDetailReceiptLocalLineData(lineNo: Int, partNo: String): ReceiptLocalLineValue

    @Update
    fun updateReceiptLocalLine(receiptLocalLineValue: ReceiptLocalLineValue)

    @Query("DELETE FROM ReceiptLocalLine")
    fun clearReceiptLocalLine()

    //ReceiptLocalScanEntriesValue--------------------------------------------------
    @Query("SELECT * FROM ReceiptLocalScanEntries")
    fun getAllReceiptLocalScanEntries(): LiveData<List<ReceiptLocalScanEntriesValue>>

    @Query("SELECT * FROM ReceiptLocalScanEntries WHERE documentNo = :localPoNo ORDER BY id DESC")
    fun getReceiptLocalScanEntriesNoLimit(localPoNo: String): LiveData<List<ReceiptLocalScanEntriesValue>>

    @Query("SELECT * FROM ReceiptLocalScanEntries WHERE documentNo = :localPoNo ORDER BY id DESC LIMIT :limit")
    fun getReceiptLocalScanEntries(
        localPoNo: String,
        limit: Int
    ): LiveData<List<ReceiptLocalScanEntriesValue>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReceiptLocalScanEntries(receiptLocalScanEntriesValue: ReceiptLocalScanEntriesValue)

    @Delete
    fun deleteReceiptLocalScanEntry(receiptLocalScanEntriesValue: ReceiptLocalScanEntriesValue)

    @Update
    fun updateReceiptLocalScanEntry(receiptLocalScanEntriesValue: ReceiptLocalScanEntriesValue)

    @Query("SELECT * FROM ReceiptLocalScanEntries WHERE sycn_status = 0")
    fun getUnsycnReceiptLocalScanEntry(): List<ReceiptLocalScanEntriesValue>

    @Query("DELETE FROM ReceiptLocalScanEntries")
    fun clearReceiptLocalScanEntries()
}