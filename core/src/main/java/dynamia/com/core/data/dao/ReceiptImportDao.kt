package dynamia.com.core.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dynamia.com.core.data.model.ReceiptImportHeaderValue
import dynamia.com.core.data.model.ReceiptImportLineValue
import dynamia.com.core.data.model.ReceiptImportScanEntriesValue

@Dao
interface ReceiptImportDao {
    //ReceiptImportHeader------------------------------------------------------
    @Query("SELECT * FROM ReceiptImportHeader WHERE employeeCode =:employeeCode COLLATE NOCASE")
    fun getAllReceiptImportHeader(employeeCode: String): LiveData<List<ReceiptImportHeaderValue>>

    @Query("SELECT * FROM ReceiptImportHeader WHERE `no`= :documentNo")
    fun getReceiptImportHeader(documentNo: String): LiveData<ReceiptImportHeaderValue>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReceiptImportHeader(receiptImportHeaderValue: ReceiptImportHeaderValue)

    @Query("SELECT count(*) from ReceiptImportHeader WHERE employeeCode =:employeeCode COLLATE NOCASE")
    fun getCountReceiptImportHeader(employeeCode: String): LiveData<Int>

    @Query("DELETE FROM ReceiptImportHeader")
    fun clearReceiptImportHeader()

    //ReceiptImportLine------------------------------------------------------
    @Query("SELECT * FROM ReceiptImportLine WHERE documentNo = :documentNo")
    fun getAllReceiptImportLine(documentNo: String): LiveData<List<ReceiptImportLineValue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReceiptImportLine(receiptImportLineValue: ReceiptImportLineValue)

    @Query("DELETE FROM ReceiptImportLine")
    fun clearReceiptImportLine()

    @Query("SELECT * FROM ReceiptImportLine WHERE documentNo = :documentNo AND partNo =:partNo")
    fun getDetailImportLine(documentNo: String, partNo: String): List<ReceiptImportLineValue>

    @Query("SELECT * FROM ReceiptImportLine WHERE lineNo = :lineNo AND partNo =:partNo AND documentNo =:documentNo ")
    fun getDetailImportLineData(
        lineNo: Int,
        partNo: String,
        documentNo: String
    ): ReceiptImportLineValue

    @Update
    fun updateImportLineData(importLineValue: ReceiptImportLineValue)

    //ReceiptImportScanEntries------------------------------------------------------
    @Query("SELECT * FROM ReceiptImportScanEntries")
    fun getAllReceiptImportScanEntries(): LiveData<List<ReceiptImportScanEntriesValue>>

    @Query("SELECT * FROM ReceiptImportScanEntries WHERE documentNo = :importPoNo ORDER BY id DESC")
    fun getReceiptImportScanEntriesNoLimit(importPoNo: String): LiveData<List<ReceiptImportScanEntriesValue>>

    @Query("SELECT * FROM ReceiptImportScanEntries WHERE documentNo = :importPoNo ORDER BY id DESC LIMIT :limit")
    fun getReceiptImportScanEntries(
        importPoNo: String,
        limit: Int
    ): LiveData<List<ReceiptImportScanEntriesValue>>

    @Query("SELECT * FROM ReceiptImportScanEntries WHERE serialNo = :serialNo ORDER BY id DESC")
    fun checkSN(
        serialNo: String
    ): List<ReceiptImportScanEntriesValue>

    @Query("SELECT * FROM ReceiptImportScanEntries WHERE documentNo = :documentNo and lineNo = :lineNo and partNo =:partNo")
    fun getFilteredImportScanEntries(
        documentNo: String,
        lineNo: Int,
        partNo: String
    ): LiveData<List<ReceiptImportScanEntriesValue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReceiptImportScanEntries(receiptImportScanEntries: ReceiptImportScanEntriesValue)

    @Delete
    fun deleteReceiptImportScanEntry(receiptImportScanEntries: ReceiptImportScanEntriesValue)

    @Update
    fun updateReceiptImportScanEntry(receiptImportScanEntries: ReceiptImportScanEntriesValue)

    @Query("SELECT * FROM ReceiptImportScanEntries WHERE sycn_status = 0")
    fun getAllUnsycnImportScanEntry(): List<ReceiptImportScanEntriesValue>

    @Query("DELETE FROM ReceiptImportScanEntries")
    fun clearReceiptImportScanEntries()
}