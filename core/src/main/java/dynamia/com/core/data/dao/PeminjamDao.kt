package dynamia.com.core.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dynamia.com.core.data.model.PeminjamScanEntries
import dynamia.com.core.data.model.PeminjamanDetail
import dynamia.com.core.data.model.PeminjamanHeader

@Dao
interface PeminjamDao {
	/**
	 * Peminjam Header Dao
	 */
	@Insert(entity = PeminjamanHeader::class, onConflict = OnConflictStrategy.REPLACE)
	fun insertAllPeminjam(data: List<PeminjamanHeader>)
	
	@Query("SELECT * FROM PeminjamanHeader WHERE employeeCode =:name")
	fun getAllPeminjamHeader(name: String): LiveData<List<PeminjamanHeader>>
	
	@Query("SELECT * FROM PeminjamanHeader WHERE  `no`=:id")
	fun getPeminjamHeaderDetail(id: String): LiveData<PeminjamanHeader>
	
	
	@Query("DELETE FROM PeminjamanHeader")
	fun deleteAllPeminjamHeader()
	
	/**
	 *Peminjam Detail Dao
	 */
	
	@Insert(entity = PeminjamanDetail::class, onConflict = OnConflictStrategy.REPLACE)
	fun insertAllPeminjamDetail(data: List<PeminjamanDetail>)
	
	@Query("SELECT * FROM PeminjamanDetail WHERE documentNo =:documentNo")
	fun getPeminjamanDetailData(documentNo: String): LiveData<List<PeminjamanDetail>>
	
	@Query("SELECT * FROM PeminjamanDetail WHERE documentNo =:documentNo AND partNo =:partNo")
	fun getPeminjamDetail(documentNo: String, partNo: String): List<PeminjamanDetail>
	
	@Query("SELECT * FROM PeminjamanDetail WHERE documentNo =:documentNo AND partNo =:partNo AND lineNo =:lineNo")
	fun getPeminjamDetailData(documentNo: String, partNo: String, lineNo: Int): PeminjamanDetail
	
	@Update(entity = PeminjamanDetail::class, onConflict = OnConflictStrategy.ABORT)
	fun updatePeminjamDetailData(data: PeminjamanDetail)
	
	@Query("DELETE FROM PeminjamanDetail")
	fun clearPeminjamDetailData()
	
	/**
	 * Peminjam Scan Entries Dao
	 */
	
	@Insert(entity = PeminjamScanEntries::class, onConflict = OnConflictStrategy.ABORT)
	fun insertPeminjamScanEntires(data: PeminjamScanEntries)
	
	@Query("SELECT * FROM PeminjamScanEntries WHERE sycn_status = :status")
	fun getPeminjamUnpostedData(status: Boolean): List<PeminjamScanEntries>
	
	@Query("SELECT * FROM PeminjamScanEntries WHERE serialNo = :serialNo")
	fun checkPeminjamSerialNo(serialNo: String): List<PeminjamScanEntries>
	
	@Query("SELECT * FROM PeminjamScanEntries WHERE documentNo =:documentNo")
	fun getPeminjamScanEntriesHistory(documentNo: String): LiveData<List<PeminjamScanEntries>>
	
	@Query("SELECT * FROM PeminjamScanEntries WHERE partNo = :partNo AND documentNo =:documentNo LIMIT 5")
	fun getAllPeminjamScanEntriesHistory(
		documentNo: String,
		partNo: String
	): LiveData<List<PeminjamScanEntries>>
	
	@Update(entity = PeminjamScanEntries::class, onConflict = OnConflictStrategy.ABORT)
	fun updatePeminjamScanEntries(data: PeminjamScanEntries)
	
	@Delete(entity = PeminjamScanEntries::class)
	fun deletePeminjamScanEntries(data: PeminjamScanEntries)
	
	@Query("DELETE FROM PeminjamScanEntries")
	fun deleteAllPeminjamScanEntries()
}