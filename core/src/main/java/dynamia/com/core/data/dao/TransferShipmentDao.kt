package dynamia.com.core.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dynamia.com.core.data.entinty.TransferShipmentHeader
import dynamia.com.core.data.entinty.TransferShipmentLine

@Dao
interface TransferShipmentDao {
    /**
     * TransferShipmentHeader
     */
    @Query("SELECT * FROM TransferShipmentHeader")
    fun getAllTransferHeader(): LiveData<List<TransferShipmentHeader>>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = TransferShipmentHeader::class)
    fun insertTransferHeader(data: TransferShipmentHeader)

    @Query("DELETE FROM TransferShipmentHeader")
    fun deleteAllTransferHeader()

    /**
     * TransferShipmentLine
     */
    @Query("SELECT * FROM TransferShipmentLine")
    fun getAllTransferLine(): LiveData<List<TransferShipmentLine>>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = TransferShipmentLine::class)
    fun insertTransferLine(data: TransferShipmentLine)

    @Query("DELETE FROM TransferShipmentHeader")
    fun deleteAllTransferLine()

    @Query("SELECT * FROM TransferShipmentLine WHERE Document_No = :no")
    fun getLineListFromHeader(no: String): LiveData<List<TransferShipmentLine>>

}