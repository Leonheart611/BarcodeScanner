package dynamia.com.core.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dynamia.com.core.data.entinty.TransferInputData
import dynamia.com.core.data.entinty.TransferShipmentHeader
import dynamia.com.core.data.entinty.TransferShipmentLine

@Dao
interface TransferShipmentDao {
    /**
     * TransferShipmentHeader
     */
    @Query("SELECT * FROM TransferShipmentHeader ORDER BY `no` DESC")
    fun getAllTransferHeader(): LiveData<List<TransferShipmentHeader>>

    @Query("SELECT * FROM TransferShipmentHeader WHERE `no`=:no")
    fun getTransferHeaderDetail(no: String): TransferShipmentHeader

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = TransferShipmentHeader::class)
    fun insertTransferHeader(data: TransferShipmentHeader)

    @Query("DELETE FROM TransferShipmentHeader")
    fun deleteAllTransferHeader()

    @Query("SELECT count(*) from TransferShipmentHeader")
    fun getCheckEmptyOrNot(): Int

    /**
     * TransferShipmentLine
     */
    @Query("SELECT * FROM TransferShipmentLine")
    fun getAllTransferLine(): LiveData<List<TransferShipmentLine>>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = TransferShipmentLine::class)
    fun insertTransferLine(data: List<TransferShipmentLine>)

    @Query("DELETE FROM TransferShipmentLine")
    fun deleteAllTransferLine()

    @Query("SELECT * FROM TransferShipmentLine WHERE documentNo = :no AND quantity != 0 LIMIT :page")
    fun getLineListFromHeaderLiveData(no: String, page:Int): LiveData<List<TransferShipmentLine>>

    @Query("SELECT * FROM TransferShipmentLine WHERE documentNo = :no AND lineNo = :lineNo")
    fun getLineDetail(no: String, lineNo: Int): TransferShipmentLine

    @Query("SELECT * FROM TransferShipmentLine WHERE documentNo = :no AND itemIdentifier = :identifier")
    fun getLineDetailFromBarcode(no: String, identifier: String): TransferShipmentLine?

    @Query("SELECT * FROM TransferShipmentLine WHERE documentNo = :no AND itemRefNo = :identifier")
    fun getLineDetailFromRef(no: String, identifier: String): TransferShipmentLine?

    @Query("SELECT SUM(alredyScanned) FROM TransferShipmentLine WHERE documentNo =:no")
    fun getAlreadyScanTotal(no: String): Int

    @Query("SELECT SUM(quantity) FROM TransferShipmentLine WHERE documentNo=:no")
    fun getQtyScanTotal(no: String): Int

    @Update(onConflict = OnConflictStrategy.ABORT, entity = TransferShipmentLine::class)
    fun updateTransferLine(data: TransferShipmentLine)

    /**
     * Transfer Shipment InputData
     */

    @Query("SELECT * FROM TransferInputData")
    fun getAllTransferInput(): LiveData<List<TransferInputData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = TransferInputData::class)
    fun insertTransferInput(data: TransferInputData)

    @Update(onConflict = OnConflictStrategy.ABORT, entity = TransferInputData::class)
    fun updateTransferInput(data: TransferInputData)

    @Query("SELECT * FROM TransferInputData WHERE sync_status = :status")
    fun getAllUnsycnTransferInput(status: Boolean): List<TransferInputData>

    @Query("SELECT * FROM TransferInputData WHERE id = :id ORDER BY id DESC  ")
    fun getTransferInputHistory(id: Int): TransferInputData

    @Query("SELECT * FROM TransferInputData WHERE documentNo = :no ORDER BY id DESC  ")
    fun getTransferInputHistoryLiveData(no: String): LiveData<List<TransferInputData>>

    @Query("SELECT * FROM TransferInputData WHERE id = :id")
    fun getTransferInputDetail(id: Int): TransferInputData

    @Query("DELETE FROM TransferInputData where id =:id")
    fun deleteTransferInput(id: Int)

    @Query("DELETE FROM TransferInputData")
    fun clearAllInputData()
}