package dynamia.com.core.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import dynamia.com.core.data.entinty.PurchaseInputData
import dynamia.com.core.data.entinty.PurchaseOrderHeader
import dynamia.com.core.data.entinty.PurchaseOrderLine

@Dao
interface PurchaseOrderDao {
    /**
     * Purchase Order Header
     */
    @Query("SELECT * FROM PurchaseOrderHeader ORDER BY `no` DESC LIMIT :page")
    fun getAllPurchaseOrderHeader(page: Int): LiveData<List<PurchaseOrderHeader>>

    @Query("SELECT COUNT(*) FROM PurchaseOrderHeader")
    fun getCount(): LiveData<Int>

    @Query("SELECT * FROM PurchaseOrderHeader")
    fun getAllPurchaseOrderPage(): PagingSource<Int, PurchaseOrderHeader>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = PurchaseOrderHeader::class)
    fun insertPurchaseOrderHeader(value: PurchaseOrderHeader)

    @Query("SELECT * FROM PurchaseOrderHeader WHERE `no` =:no ")
    fun getPurchaseOrderDetail(no: String): PurchaseOrderHeader

    @Query("SELECT count(*) from PurchaseOrderHeader")
    fun getPurchaseOrderHeaderCount(): Int

    @Query("DELETE FROM PurchaseOrderHeader")
    fun deleteAllPurchaseOrderHeader()

    /**
     * Purchase Order Line
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = PurchaseOrderLine::class)
    fun insertPurchaseOrderLine(value: MutableList<PurchaseOrderLine>)

    @Query("SELECT * FROM PurchaseOrderLine WHERE documentNo =:no AND quantity > 0 LIMIT :page")
    fun getPurchaseOrderLineDetailByNo(no: String, page: Int): LiveData<List<PurchaseOrderLine>>

    @Query("SELECT * FROM PurchaseOrderLine WHERE id=:id")
    fun getPurchaseOrderLineDetailById(id: Int): PurchaseOrderLine

    @Query("SELECT * FROM PurchaseOrderLine WHERE documentNo=:no AND itemIdentifier =:identifier")
    fun getPurchaseOrderLineByBarcode(no: String, identifier: String): PurchaseOrderLine?

    @Query("SELECT * FROM PurchaseOrderLine WHERE documentNo=:no AND itemRefNo =:identifier")
    fun getPurchaseOrderLineByItemRef(no: String, identifier: String): PurchaseOrderLine

    @Query("SELECT * FROM PurchaseOrderLine WHERE id =:id")
    fun getPurchaseOrderLineLiveData(id: Int): LiveData<PurchaseOrderLine>

    @Query("SELECT * FROM PurchaseOrderLine WHERE documentNo=:no AND lineNo =:lineNo")
    fun getPurchaseOrderLineByLineno(no: String, lineNo: Int): PurchaseOrderLine

    @Query("SELECT SUM(alredyScanned) FROM PurchaseOrderLine WHERE documentNo =:no")
    fun getAlreadyScanTotal(no: String): LiveData<Int>

    @Query("SELECT SUM(quantity) FROM PurchaseOrderLine WHERE documentNo=:no")
    fun getQtyScanTotal(no: String): LiveData<Int>

    @Update(onConflict = OnConflictStrategy.ABORT, entity = PurchaseOrderLine::class)
    fun updatePurchaseOrderLine(value: PurchaseOrderLine)

    @Query("DELETE FROM PurchaseOrderLine")
    fun deleteAllPurchaseOrderLine()


    /**
     * Purchase Order Input Data
     */

    @Query("SELECT * FROM PurchaseInputData ORDER BY id DESC")
    fun getAllPurchaseInputData(): LiveData<List<PurchaseInputData>>

    @Query("SELECT * FROM PurchaseInputData WHERE documentNo=:no AND accidentalScanned = :accidentallyInput")
    fun getAllPurchaseInputDataByNo(no: String, accidentallyInput: Boolean): LiveData<List<PurchaseInputData>>

    @Query("SELECT * FROM PurchaseInputData WHERE id=:id")
    fun getPurchaseInputDataDetail(id: Int): PurchaseInputData

    @Query("SELECT * FROM PurchaseInputData WHERE sync_status = :status")
    fun getAllUnsyncPurchaseInput(status: Boolean): List<PurchaseInputData>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = PurchaseInputData::class)
    fun insertPurchaseOrderData(value: PurchaseInputData)

    @Update(onConflict = OnConflictStrategy.ABORT, entity = PurchaseInputData::class)
    fun updatePurchaseInputData(value: PurchaseInputData)

    @Query("DELETE FROM PurchaseInputData where id =:id")
    fun deletePurchaseInput(id: Int)

    @Query("DELETE FROM PurchaseInputData")
    fun deleteAllPurchaseInputData()

    @Query("SELECT SUM(quantity) FROM PurchaseInputData WHERE documentNo=:no and accidentalScanned = :accidentallyInput")
    fun getPurchaseOrderAccidentInput(
        no: String,
        accidentallyInput: Boolean = true
    ): LiveData<Int>

}