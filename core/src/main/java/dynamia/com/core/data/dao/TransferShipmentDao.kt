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
    @Query("SELECT * FROM TransferShipmentHeader")
    fun getAllTransferHeader(): LiveData<List<TransferShipmentHeader>>

    @Query("SELECT * FROM TransferShipmentHeader WHERE `no`=:no")
    fun getTransferHeaderDetail(no: String): TransferShipmentHeader

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = TransferShipmentHeader::class)
    fun insertTransferHeader(data: TransferShipmentHeader)

    @Query("DELETE FROM TransferShipmentHeader")
    fun deleteAllTransferHeader()

    @Query("SELECT count(*) from TransferShipmentHeader")
    fun getCheckEmptyOrNot(): LiveData<Int>

    /**
     * TransferShipmentLine
     */
    @Query("SELECT * FROM TransferShipmentLine")
    fun getAllTransferLine(): LiveData<List<TransferShipmentLine>>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = TransferShipmentLine::class)
    fun insertTransferLine(data: TransferShipmentLine)

    @Query("DELETE FROM TransferShipmentLine")
    fun deleteAllTransferLine()

    @Query("SELECT * FROM TransferShipmentLine WHERE documentNo = :no")
    fun getLineListFromHeader(no: String): List<TransferShipmentLine>

    @Query("SELECT * FROM TransferShipmentLine WHERE documentNo = :no")
    fun getLineListFromHeaderLiveData(no: String): LiveData<List<TransferShipmentLine>>

    @Query("SELECT * FROM TransferShipmentLine WHERE documentNo = :no AND lineNo = :lineNo")
    fun getLineDetail(no: String, lineNo: Int): TransferShipmentLine

    @Query("SELECT * FROM TransferShipmentLine WHERE documentNo = :no AND `no` = :identifier")
    fun getLineDetailFromBarcode(no: String, identifier: String): TransferShipmentLine

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

    @Query("SELECT * FROM TransferInputData WHERE sycn_status = :status")
    fun getAllUnsycnTransferInput(status: Boolean): List<TransferInputData>

    @Query("SELECT * FROM TransferInputData WHERE documentNo = :no ORDER BY id DESC  ")
    fun getTransferInputHistory(no: String): List<TransferInputData>

    @Query("DELETE FROM TransferInputData")
    fun deleteAllTransferInput()


}