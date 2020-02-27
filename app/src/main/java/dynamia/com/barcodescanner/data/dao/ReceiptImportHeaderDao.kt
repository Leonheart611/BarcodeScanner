package dynamia.com.barcodescanner.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dynamia.com.barcodescanner.data.model.ReceiptImportHeaderValue

@Dao
interface ReceiptImportHeaderDao {
    @Query("SELECT * FROM ReceiptImportHeader")
    fun getAllReceiptImportHeader(): LiveData<List<ReceiptImportHeaderValue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReceiptImportHeader(receiptImportHeaderValue: ReceiptImportHeaderValue)

    @Query("DELETE FROM ReceiptImportHeader")
    fun clearReceiptImportHeader()
}