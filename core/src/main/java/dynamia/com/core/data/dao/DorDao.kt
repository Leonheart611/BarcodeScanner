package dynamia.com.core.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dynamia.com.core.data.model.DorPickingDetail
import dynamia.com.core.data.model.DorPickingHeader

@Dao
interface DorDao {
    /**
     * Dor Header Dao
     */

    @Insert(entity = DorPickingHeader::class, onConflict = OnConflictStrategy.REPLACE)
    fun addAllDorHeader(datas: List<DorPickingHeader>)

    @Query("SELECT * FROM DorPickingHeader WHERE employeeCode = :employeeCode COLLATE NOCASE")
    fun getAllDorHeader(employeeCode: String): LiveData<List<DorPickingHeader>>

    @Query("DELETE FROM DorPickingHeader")
    fun deleteAllDorData()

    /**
     * Dor Line Dao
     */

    @Insert(entity = DorPickingDetail::class, onConflict = OnConflictStrategy.REPLACE)
    fun addAllDorDetail(datas: List<DorPickingDetail>)

    @Query("SELECT * FROM DorPickingDetail WHERE documentNo = :no")
    fun getAllDorDetail(no: String): LiveData<List<DorPickingDetail>>

    @Update(entity = DorPickingDetail::class, onConflict = OnConflictStrategy.ABORT)
    fun updateDorDetail(data: DorPickingDetail)

    @Query("DELETE FROM DorPickingDetail")
    fun deleteAllDorDetail()

}