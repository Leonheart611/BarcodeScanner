package dynamia.com.core.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dynamia.com.core.data.entinty.InventoryInputData
import dynamia.com.core.data.entinty.InventoryPickHeader
import dynamia.com.core.data.entinty.InventoryPickLine

@Dao
interface InventoryDao {
    /*
      Inventory Header Dao
       */
    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = InventoryPickHeader::class)
    fun insertInventoryHeaderAll(datas: List<InventoryPickHeader>)

    @Query("SELECT * FROM InventoryPickHeader ORDER BY `no` DESC")
    fun getAllInventoryHeader(): LiveData<List<InventoryPickHeader>>

    @Query("SELECT * FROM InventoryPickHeader WHERE `no`=:no")
    fun getInventoryHeaderDetail(no: String): InventoryPickHeader

    @Query("DELETE FROM InventoryPickHeader")
    fun deleteAllInventoryHeader()

    /**
     * Inventory Line DAO
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = InventoryPickLine::class)
    fun insertInventoryLineAll(datas: List<InventoryPickLine>)

    @Query("SELECT * FROM InventoryPickLine WHERE `no` = :no ORDER BY `no` DESC LIMIT :page")
    fun getAllInventoryPickLine(no: String, page: Int): LiveData<List<InventoryPickLine>>

    @Query("SELECT * FROM InventoryPickLine WHERE `no` =:no AND itemRefNo = :itemRefNo")
    fun getInventoryPickLineDetail(no: String, itemRefNo: String): InventoryPickLine

    @Query("SELECT SUM(alredyScanned) FROM InventoryPickLine WHERE `no` =:no")
    fun getAlreadyScanTotal(no: String): Int

    @Query("SELECT SUM(quantity) FROM InventoryPickLine WHERE `no`=:no")
    fun getQtyScanTotal(no: String): Int

    @Update(onConflict = OnConflictStrategy.ABORT, entity = InventoryPickLine::class)
    fun updateInventoryPickLine(value: InventoryPickLine)

    @Query("DELETE FROM InventoryPickLine")
    fun deleteAllInventoryPickLine()

    /*
    Inventory Input Data
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = InventoryInputData::class)
    fun insertInputInventory(data: InventoryInputData)

    @Query("SELECT * FROM InventoryInputData")
    fun getAllInventoryInputData(): LiveData<List<InventoryInputData>>

    @Query("SELECT * FROM InventoryInputData WHERE id =:id")
    fun getInventoryInputDetail(id: Int): InventoryInputData

    @Query("SELECT * FROM InventoryInputData WHERE sync_status =:status")
    fun getAllUnsycnInputData(status: Boolean = false): List<InventoryInputData>

    @Query("SELECT * FROM InventoryInputData WHERE documentNo =:no")
    fun getInventoryInputData(no: String): LiveData<List<InventoryInputData>>

    @Update(onConflict = OnConflictStrategy.ABORT, entity = InventoryInputData::class)
    fun updateInputData(data: InventoryInputData)

    @Query("DELETE FROM InventoryInputData WHERE id =:id")
    fun deleteInventoryInput(id: Int)

    @Query("DELETE FROM InventoryInputData")
    fun deleteAllInventoryInput()


}