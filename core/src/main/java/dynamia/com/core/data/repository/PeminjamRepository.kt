package dynamia.com.core.data.repository

import androidx.lifecycle.LiveData
import dynamia.com.core.data.dao.PeminjamDao
import dynamia.com.core.data.model.PeminjamScanEntries
import dynamia.com.core.data.model.PeminjamanDetail
import dynamia.com.core.data.model.PeminjamanHeader
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

interface PeminjamRepository {
	/**
	 * Peminjam Header Dao
	 */
	fun insertAllPeminjam(data: List<PeminjamanHeader>)
	fun getAllPeminjamHeader(employeeName: String): LiveData<List<PeminjamanHeader>>
	fun getPeminjamHeaderDetail(id: String): LiveData<PeminjamanHeader>
	fun deleteAllPeminjamHeader()
	
	/**
	 *Peminjam Detail Dao
	 */
	fun insertAllPeminjamDetail(data: List<PeminjamanDetail>)
	fun getPeminjamanDetailData(documentNo: String): LiveData<List<PeminjamanDetail>>
	fun getPeminjamanDetail(documentNo: String, partNo: String): List<PeminjamanDetail>
	fun updatePeminjamDetailData(data: PeminjamanDetail)
	fun clearPeminjamDetailData()
	
	/**
	 * Peminjam Scan Entries Dao
	 */
	
	fun insertPeminjamScanEntires(data: PeminjamScanEntries): Boolean
	fun getAllPeminjamScanEntriesHistory(
		documentNo: String,
		partNo: String
	): LiveData<List<PeminjamScanEntries>>
	
	fun getPeminjamanUnpostedList(): List<PeminjamScanEntries>
	fun getPeminjamScanEntriesHistory(documentNo: String): LiveData<List<PeminjamScanEntries>>
	fun updatePeminjamScanEntries(data: PeminjamScanEntries)
	fun deletePeminjamScanEntries(data: PeminjamScanEntries)
	fun checkPeminjamSerialNo(serialNo: String): Boolean
	fun deleteAllPeminjamScanEntries()
}

class PeminjamRepoImpl(private val dao: PeminjamDao) : PeminjamRepository {
	
	private val parentJob = Job()
	private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Main
	private val scope = CoroutineScope(coroutineContext)
	
	/**
	 * Peminjam Header Dao
	 */
	override fun insertAllPeminjam(data: List<PeminjamanHeader>) {
		dao.insertAllPeminjam(data)
	}
	
	override fun getAllPeminjamHeader(employeeName: String): LiveData<List<PeminjamanHeader>> =
		dao.getAllPeminjamHeader(employeeName)
	
	override fun getPeminjamHeaderDetail(id: String): LiveData<PeminjamanHeader> =
		dao.getPeminjamHeaderDetail(id)
	
	override fun deleteAllPeminjamHeader() {
		dao.deleteAllPeminjamHeader()
	}
	
	/**
	 *Peminjam Detail Dao
	 */
	override fun insertAllPeminjamDetail(data: List<PeminjamanDetail>) {
		dao.insertAllPeminjamDetail(data)
	}
	
	override fun getPeminjamanDetailData(documentNo: String): LiveData<List<PeminjamanDetail>> =
		dao.getPeminjamanDetailData(documentNo)
	
	override fun getPeminjamanDetail(documentNo: String, partNo: String): List<PeminjamanDetail> =
		dao.getPeminjamDetail(documentNo, partNo)
	
	override fun updatePeminjamDetailData(data: PeminjamanDetail) {
		dao.updatePeminjamDetailData(data)
	}
	
	override fun clearPeminjamDetailData() {
		dao.clearPeminjamDetailData()
	}
	
	/**
	 * Peminjam Scan Entries Dao
	 */
	override fun insertPeminjamScanEntires(data: PeminjamScanEntries): Boolean = runBlocking(
		Dispatchers.IO
	) {
		try {
			val importLineValue = dao.getPeminjamDetailData(
				documentNo = data.documentNo,
				partNo = data.partNo,
				lineNo = data.lineNo
			)
			if (importLineValue.alreadyScanned < importLineValue.outstandingQuantity.toInt()) {
				importLineValue.apply {
					this.alreadyScanned = ++alreadyScanned
				}
				dao.insertPeminjamScanEntires(data)
				dao.updatePeminjamDetailData(importLineValue)
				true
			} else {
				false
			}
		} catch (e: Exception) {
			false
		}
	}
	
	override fun getPeminjamanUnpostedList(): List<PeminjamScanEntries> =
		dao.getPeminjamUnpostedData(false)
	
	override fun getPeminjamScanEntriesHistory(documentNo: String): LiveData<List<PeminjamScanEntries>> =
		dao.getPeminjamScanEntriesHistory(documentNo)
	
	override fun getAllPeminjamScanEntriesHistory(
		documentNo: String,
		partNo: String
	): LiveData<List<PeminjamScanEntries>> =
		dao.getAllPeminjamScanEntriesHistory(documentNo, partNo)
	
	override fun updatePeminjamScanEntries(data: PeminjamScanEntries) {
		dao.updatePeminjamScanEntries(data)
	}
	
	override fun checkPeminjamSerialNo(serialNo: String): Boolean =
		dao.checkPeminjamSerialNo(serialNo).isEmpty()
	
	override fun deletePeminjamScanEntries(data: PeminjamScanEntries) {
		scope.launch(Dispatchers.IO) {
			dao.deletePeminjamScanEntries(data)
			val pickingListData = dao.getPeminjamDetailData(
				lineNo = data.lineNo,
				partNo = data.partNo,
				documentNo = data.documentNo
			)
			pickingListData.apply {
				this.alreadyScanned = --alreadyScanned
			}
			dao.updatePeminjamDetailData(pickingListData)
		}
	}
	
	override fun deleteAllPeminjamScanEntries() {
		dao.deleteAllPeminjamScanEntries()
	}
}