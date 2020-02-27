package dynamia.com.barcodescanner.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dynamia.com.barcodescanner.data.model.ReceiptLocalHeaderValue

interface ReceiptLocalHeaderDao {
    @Query("SELECT * FROM ReceiptLocalHeader")
    fun getAllReceiptLocalHeader(): LiveData<List<ReceiptLocalHeaderValue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReceiptLocalHeader(receiptLocalHeaderValue: ReceiptLocalHeaderValue)

    @Query("DELETE FROM ReceiptLocalHeader")
    fun clearReceiptLocalHeader()
}