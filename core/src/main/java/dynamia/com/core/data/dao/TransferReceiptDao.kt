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
    @Query("SELECT * FROM TransferReceiptHeader")
    fun getAllTransferReceiptHeader(): LiveData<List<TransferReceiptHeader>>

    @Query("SELECT * FROM TransferReceiptHeader WHERE `no`=:no")
    fun getTransferHeaderDetail(no: String): TransferReceiptHeader

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = TransferReceiptHeader::class)
    fun insertTransferReceiptHeader(data: TransferReceiptHeader)

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

    @Query("DELETE FROM TransferReceiptInput where id =:id")
    fun deleteTransferInput(id: Int)

    @Query("DELETE FROM TransferReceiptInput")
    fun clearAllInputData()

}