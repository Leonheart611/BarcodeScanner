package dynamia.com.core.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dynamia.com.core.data.model.DorPickingDetail
import dynamia.com.core.data.model.DorPickingHeader
import dynamia.com.core.data.model.DorPickingScanEntries

@Dao
interface DorDao {
	/**
	 * Dor Header Dao
	 */
	
	@Insert(entity = DorPickingHeader::class, onConflict = OnConflictStrategy.REPLACE)
	fun addAllDorHeader(datas: List<DorPickingHeader>)
	
	@Query("SELECT * FROM DorPickingHeader WHERE employeeCode = :employeeCode COLLATE NOCASE")
	fun getAllDorHeader(employeeCode: String): LiveData<List<DorPickingHeader>>
	
	@Query("SELECT * FROM DorPickingHeader WHERE `no` = :documentId")
	fun getDorHeaderDetail(documentId: String): LiveData<DorPickingHeader>
	
	@Query("DELETE FROM DorPickingHeader")
	fun deleteAllDorData()
	
	/**
	 * Dor Line Dao
	 */
	
	@Insert(entity = DorPickingDetail::class, onConflict = OnConflictStrategy.REPLACE)
	fun addAllDorDetail(datas: List<DorPickingDetail>)
	
	@Query("SELECT * FROM DorPickingDetail WHERE documentNo = :no")
	fun getAllDorDetail(no: String): LiveData<List<DorPickingDetail>>
	
	@Query("SELECT * FROM DorPickingDetail WHERE documentNo = :no AND partNo = :partNo")
	fun getAllDorDetailData(no: String, partNo: String): List<DorPickingDetail>
	
	@Query("SELECT * FROM DorPickingDetail WHERE documentNo = :no AND partNo = :partNo AND lineNo =:lineNo")
	fun getAllDorDetailDataLine(no: String, partNo: String, lineNo: Int): DorPickingDetail
	
	@Update(entity = DorPickingDetail::class, onConflict = OnConflictStrategy.ABORT)
	fun updateDorDetail(data: DorPickingDetail)
	
	@Query("DELETE FROM DorPickingDetail")
	fun deleteAllDorDetail()
	
	/**
	 *Dor Scan Entries Dao
	 */
	
	@Insert(entity = DorPickingScanEntries::class, onConflict = OnConflictStrategy.ABORT)
	fun insertDorScanEntry(data: DorPickingScanEntries)
	
	@Query("SELECT * FROM DorPickingScanEntries")
	fun getAllDorScanEntries(): LiveData<List<DorPickingScanEntries>>
	
	@Query("SELECT * FROM DorPickingScanEntries WHERE serialNo = :serialNo")
	fun checkDorSerialNo(serialNo: String): List<DorPickingScanEntries>
	
	@Query("SELECT * FROM DorPickingScanEntries WHERE documentNo =:documentNo")
	fun getDorScanEntriesHistory(documentNo: String): LiveData<List<DorPickingScanEntries>>
	
	@Query("SELECT * FROM DorPickingScanEntries WHERE sycn_status = :status")
	fun getDorScanEntriesUnposted(status: Boolean): List<DorPickingScanEntries>
	
	@Query("SELECT * FROM DorPickingScanEntries WHERE partNo = :partNo AND documentNo =:documentNo LIMIT 5")
	fun getAllDorScanEntriesHistory(
		documentNo: String,
		partNo: String
	): LiveData<List<DorPickingScanEntries>>
	
	@Delete(entity = DorPickingScanEntries::class)
	fun deleteDorScanEntries(data: DorPickingScanEntries)
	
	@Update(entity = DorPickingScanEntries::class, onConflict = OnConflictStrategy.ABORT)
	fun updateDorScanEntries(data: DorPickingScanEntries)
	
	@Query("DELETE FROM DorPickingScanEntries")
	fun deleteAllDorScanEntries()
	
}