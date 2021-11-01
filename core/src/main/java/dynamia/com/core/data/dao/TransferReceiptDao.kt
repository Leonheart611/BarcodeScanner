package dynamia.com.core.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dynamia.com.core.data.entinty.TransferReceiptHeader
import dynamia.com.core.data.entinty.TransferReceiptInput

@Dao
interface TransferReceiptDao {
    /**
     * TransferReceiptHeader
     */
    @Query("SELECT * FROM TransferReceiptHeader ORDER BY `no` DESC LIMIT :page")
    fun getAllTransferReceiptHeader(page: Int): LiveData<List<TransferReceiptHeader>>

    @Query("SELECT COUNT(*) FROM TransferReceiptHeader")
    fun getCount(): LiveData<Int>

    @Query("SELECT * FROM TransferReceiptHeader WHERE `no`=:no")
    fun getTransferHeaderDetail(no: String): TransferReceiptHeader

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = TransferReceiptHeader::class)
    fun insertTransferReceiptHeader(data: TransferReceiptHeader)

    @Query("SELECT count(*) from TransferReceiptHeader")
    fun getTransferReceiptCount(): Int

    @Query("DELETE FROM TransferReceiptHeader")
    fun deleteAllTransferReceiptHeader()

    /**
     * TransferReceiptInput
     */

    @Query("SELECT * FROM TransferReceiptInput")
    fun getAllTransferReceiptInput(): LiveData<List<TransferReceiptInput>>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = TransferReceiptInput::class)
    fun insertTransferReceiptInput(data: TransferReceiptInput)

    @Update(onConflict = OnConflictStrategy.ABORT, entity = TransferReceiptInput::class)
    fun updateTransferReceiptInput(data: TransferReceiptInput)

    @Query("SELECT * FROM TransferReceiptInput WHERE sync_status = :status")
    fun getAllUnsycnTransferReceiptInput(status: Boolean): List<TransferReceiptInput>

    @Query("SELECT * FROM TransferReceiptInput WHERE id = :id ORDER BY id DESC  ")
    fun getTransferReceiptInputHistory(id: Int): TransferReceiptInput

    @Query("SELECT * FROM TransferReceiptInput WHERE documentNo = :no ORDER BY id DESC  ")
    fun getTransferInputHistoryLiveData(no: String): LiveData<List<TransferReceiptInput>>

    @Query("SELECT * FROM TransferReceiptInput WHERE id = :id")
    fun getTransferInputDetail(id: Int): TransferReceiptInput

    @Query("SELECT SUM(quantity) FROM TransferReceiptInput WHERE documentNo=:no")
    fun getQtyScanInputTotal(no: String): Int

    @Query("DELETE FROM TransferReceiptInput where id =:id")
    fun deleteTransferInput(id: Int)

    @Query("DELETE FROM TransferReceiptInput")
    fun clearAllInputData()

}