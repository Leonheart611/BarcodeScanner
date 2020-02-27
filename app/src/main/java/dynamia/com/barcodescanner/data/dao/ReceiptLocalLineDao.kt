package dynamia.com.barcodescanner.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dynamia.com.barcodescanner.data.model.ReceiptLocalLineValue

interface ReceiptLocalLineDao {
    @Query("SELECT * FROM ReceiptLocalLine")
    fun getAllReceiptLocalHeader(): LiveData<List<ReceiptLocalLineValue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReceiptLocalHeader(receiptLocalLineValue: ReceiptLocalLineValue)

    @Query("DELETE FROM ReceiptLocalHeader")
    fun clearReceiptLocalHeader()
}