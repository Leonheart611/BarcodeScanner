package dynamia.com.core.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dynamia.com.core.data.entinty.StockOpnameData
import dynamia.com.core.data.entinty.StockOpnameInputData

@Dao
interface StockOpnameDao {

    /**
     * Stock Opname Data
     */
    @Query("SELECT * FROM StockOpnameData ORDER BY binCode ASC LIMIT :page")
    fun getALlStockOpname(page: Int): LiveData<List<StockOpnameData>>

    @Query("SELECT COUNT(*) FROM StockOpnameData")
    fun getCount(): LiveData<Int>

    @Query("SELECT * FROM StockOpnameData WHERE id=:id")
    fun getStockOpnameDetail(id: Int): StockOpnameData

    @Query("SELECT * FROM StockOpnameData WHERE itemIdentifier=:barcode AND binCode =:binCode")
    fun getStockOpnameDetailBinCode(barcode: String, binCode: String): StockOpnameData?

    @Query("SELECT * FROM StockOpnameData WHERE itemRefNo = :barcode AND binCode =:binCode")
    fun getStockOpnameDetailItemRef(barcode: String, binCode: String): StockOpnameData?

    @Query("SELECT * FROM StockOpnameData WHERE itemIdentifier=:barcode")
    fun getStockOpnameDetailBinCode(barcode: String): StockOpnameData?

    @Query("SELECT * FROM StockOpnameData WHERE itemRefNo = :barcode")
    fun getStockOpnameDetailItemRef(barcode: String): StockOpnameData?

    @Query("SELECT * FROM StockOpnameData WHERE itemIdentifier=:barcode AND id =:id")
    fun getStockOpnameDetail(barcode: String, id: Int): StockOpnameData

    @Query("SELECT * FROM StockOpnameData WHERE documentNo = :documentNo AND itemNo =:itemCode")
    fun getStockOpnameDetail(documentNo: String, itemCode: String): StockOpnameData

    @Update(onConflict = OnConflictStrategy.ABORT, entity = StockOpnameData::class)
    fun updateStockOpname(value: StockOpnameData)

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = StockOpnameData::class)
    fun insertStockOpnameData(data: StockOpnameData)

    @Query("SELECT alredyScanned from StockOpnameData WHERE id = :id")
    fun getStockOpnameInputCount(id: Int): LiveData<Int>

    @Query("SELECT count(*) from StockOpnameData")
    fun getStockOpnameDataCount(): Int

    @Query("SELECT SUM(alredyScanned) from StockOpnameData")
    fun getStockOpnameAlreadyScanTotal(): LiveData<Int>

    @Query("DELETE FROM StockOpnameData")
    fun deleteAllStockOpname()

    /**
     * Stock Opname Input Data
     */
    @Query("SELECT * FROM StockOpnameInputData ORDER BY id DESC")
    fun getALlStockOpnameInput(): LiveData<List<StockOpnameInputData>>

    @Query("SELECT * FROM StockOpnameInputData WHERE documentNo = :documentNo")
    fun getAllStockOpnameInput(documentNo: String): LiveData<List<StockOpnameInputData>>

    @Query("SELECT * FROM StockOpnameInputData WHERE sync_status = :sync")
    fun getAllUnsyncOpnameInput(sync: Boolean = false): List<StockOpnameInputData>

    @Query("SELECT * FROM StockOpnameInputData WHERE id = :id")
    fun getStockOpnameInputDetail(id: Int): StockOpnameInputData

    @Query("SELECT * FROM StockOpnameInputData WHERE box = :box")
    fun getAllStockOpnameByBox(box: String): LiveData<List<StockOpnameInputData>>

    @Query("SELECT * FROM StockOpnameInputData WHERE box = :box")
    fun getAllStockOpnameByBoxList(box: String): List<StockOpnameInputData>

    @Query("SELECT * FROM StockOpnameInputData WHERE itemNo LIKE :itemNo")
    fun getAllStockOpnameByItemNo(itemNo: String): LiveData<List<StockOpnameInputData>>

    @Query("SELECT * FROM StockOpnameInputData WHERE itemNo LIKE :itemNo AND documentNo = :documentNo")
    fun getAllStockOpnameByItemNoAndDocumentNo(
        itemNo: String,
        documentNo: String
    ): LiveData<List<StockOpnameInputData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = StockOpnameInputData::class)
    fun insertStockOpnameInput(data: StockOpnameInputData)

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = StockOpnameInputData::class)
    fun updateStockOpnameInput(data: StockOpnameInputData)

    @Delete(entity = StockOpnameInputData::class)
    fun deleteStockOpnameInput(value: StockOpnameInputData)

    @Query("DELETE FROM StockOpnameInputData")
    fun deleteAllStockOpnameInput()
}