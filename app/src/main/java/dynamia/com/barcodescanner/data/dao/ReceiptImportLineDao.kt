package dynamia.com.barcodescanner.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dynamia.com.barcodescanner.data.model.ReceiptImportLineValue

@Dao
interface ReceiptImportLineDao {
    @Query("SELECT * FROM ReceiptImportLine")
    fun getAllReceiptImportLine(): LiveData<List<ReceiptImportLineValue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReceiptImportLine(receiptImportLineValue: ReceiptImportLineValue)

    @Query("DELETE FROM ReceiptImportHeader")
    fun clearReceiptImportLine()
}