package dynamia.com.core.data.repository

import androidx.lifecycle.LiveData
import dynamia.com.core.data.dao.DorDao
import dynamia.com.core.data.model.DorPickingDetail
import dynamia.com.core.data.model.DorPickingHeader
import dynamia.com.core.data.model.DorPickingScanEntries
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

interface DorPickingRepository {
	/**
	 * Dor Header Dao
	 */
	fun addAllDorHeader(datas: List<DorPickingHeader>)
	fun getAllDorHeader(employeeCode: String): LiveData<List<DorPickingHeader>>
	fun getDorHeaderDetail(no: String): LiveData<DorPickingHeader>
	fun deleteAllDorData()
	
	/**
	 * Dor Line Dao
	 */
	fun addAllDorDetail(datas: List<DorPickingDetail>)
	fun getAllDorDetail(no: String): LiveData<List<DorPickingDetail>>
	fun getAllDorDetailData(no: String, partNo: String): List<DorPickingDetail>
	fun updateDorDetail(data: DorPickingDetail)
	fun deleteAllDorDetail()
	
	/**
	 *Dor Scan Entries Dao
	 */
	fun insertDorScanEntry(data: DorPickingScanEntries): Boolean
	fun getAllDorScanEntriesHistory(
		documentNo: String,
		partNo: String
	): LiveData<List<DorPickingScanEntries>>
	
	fun checkDorSerialNo(serialNo: String): Boolean
	fun getAllDorScanEntries(): LiveData<List<DorPickingScanEntries>>
	fun getDorScanEntriesHistory(documentNo: String): LiveData<List<DorPickingScanEntries>>
	fun updateDorScanEntries(data: DorPickingScanEntries)
	fun deleteDorScanEntries(data: DorPickingScanEntries)
	fun deleteAllDorScanEntries()
}

class DorPickingRepoImpl(private val dao: DorDao) : DorPickingRepository {
	
	private val parentJob = Job()
	private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Main
	private val scope = CoroutineScope(coroutineContext)
	
	/**
	 * Dor Header Dao
	 */
	override fun addAllDorHeader(datas: List<DorPickingHeader>) {
		dao.addAllDorHeader(datas)
	}
	
	override fun getDorHeaderDetail(no: String): LiveData<DorPickingHeader> =
		dao.getDorHeaderDetail(no)
	
	override fun getAllDorHeader(employeeCode: String): LiveData<List<DorPickingHeader>> =
		dao.getAllDorHeader(employeeCode)
	
	override fun deleteAllDorData() {
		dao.deleteAllDorData()
	}
	
	/**
	 * Dor Line Dao
	 */
	override fun addAllDorDetail(datas: List<DorPickingDetail>) {
		dao.addAllDorDetail(datas)
	}
	
	override fun getAllDorDetailData(no: String, partNo: String): List<DorPickingDetail> =
		dao.getAllDorDetailData(no, partNo)
	
	override fun getAllDorDetail(no: String): LiveData<List<DorPickingDetail>> =
		dao.getAllDorDetail(no)
	
	override fun updateDorDetail(data: DorPickingDetail) {
		dao.updateDorDetail(data)
	}
	
	override fun deleteAllDorDetail() {
		dao.deleteAllDorDetail()
	}
	
	/**
	 *Dor Scan Entries Dao
	 */
	override fun insertDorScanEntry(data: DorPickingScanEntries): Boolean = runBlocking(
		Dispatchers.IO
	) {
		try {
			val importLineValue = dao.getAllDorDetailDataLine(
				no = data.documentNo,
				partNo = data.partNo,
				lineNo = data.lineNo
			)
			if (importLineValue.alreadyScanned < importLineValue.outstandingQuantity.toInt()) {
				importLineValue.apply {
					this.alreadyScanned = ++alreadyScanned
				}
				dao.insertDorScanEntry(data)
				dao.updateDorDetail(importLineValue)
				true
			} else {
				false
			}
		} catch (e: Exception) {
			false
		}
	}
	
	override fun getDorScanEntriesHistory(documentNo: String): LiveData<List<DorPickingScanEntries>> =
		dao.getDorScanEntriesHistory(documentNo)
	
	override fun getAllDorScanEntriesHistory(
		documentNo: String,
		partNo: String
	): LiveData<List<DorPickingScanEntries>> = dao.getAllDorScanEntriesHistory(documentNo, partNo)
	
	override fun checkDorSerialNo(serialNo: String): Boolean =
		dao.checkDorSerialNo(serialNo).isEmpty()
	
	override fun getAllDorScanEntries(): LiveData<List<DorPickingScanEntries>> =
		dao.getAllDorScanEntries()
	
	override fun updateDorScanEntries(data: DorPickingScanEntries) {
		dao.updateDorScanEntries(data)
	}
	
	override fun deleteDorScanEntries(data: DorPickingScanEntries) {
		scope.launch(Dispatchers.IO) {
			dao.deleteDorScanEntries(data)
			val pickingListData = dao.getAllDorDetailDataLine(
				lineNo = data.lineNo,
				partNo = data.partNo,
				no = data.documentNo
			)
			pickingListData.apply {
				this.alreadyScanned = --alreadyScanned
			}
			dao.updateDorDetail(pickingListData)
		}
	}
	
	override fun deleteAllDorScanEntries() {
		dao.deleteAllDorScanEntries()
	}
}