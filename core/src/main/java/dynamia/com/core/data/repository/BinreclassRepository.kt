package dynamia.com.core.data.repository

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import dynamia.com.core.data.dao.BinreclassDao
import dynamia.com.core.data.entinty.BinreclassHeader
import dynamia.com.core.data.entinty.BinreclassInputData
import dynamia.com.core.domain.MasariRetrofit
import dynamia.com.core.util.getDocumentCode
import dynamia.com.core.util.getNormalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface BinreclassRepository {
    /**
     * Bin Reclass Header Dao
     */
    fun getAllBinReclassHeader(): LiveData<List<BinreclassHeader>>
    suspend fun getBinReclassHeaderDetail(binFrom: String, binTo: String): BinreclassHeader
    fun getAllUnsycnHeaderData(status: Boolean = false): List<BinreclassHeader>
    fun getALlSycn(status: Boolean = false): LiveData<List<BinreclassHeader>>
    fun checkBinFromAndBinToCode(binFrom: String, binTo: String): Flow<Boolean>
    fun updateBinFromAndBinToCode(id: Int, binFrom: String, binTo: String): Flow<Boolean>
    fun insertBinReclassHeader(data: BinreclassHeader)
    fun updateBinReclassHeader(data: BinreclassHeader)
    fun deleteBinReclassHeader(data: BinreclassHeader)
    fun deleteAllBinreclass()


    /**
     * Bin Reclass Input Dao
     */
    fun getAllBinreclassInputData(): LiveData<List<BinreclassInputData>>
    fun getBinreclassInputData(headerId: Int): LiveData<List<BinreclassInputData>>
    fun getAllUnSyncBinreclassnput(status: Boolean = false): List<BinreclassInputData>
    fun getAllUnSyncBinreclassnputByHeaderId(
        status: Boolean = false,
        headerId: Int,
    ): List<BinreclassInputData>

    fun getBinReclassById(id: Int): Flow<BinreclassInputData>
    fun insertBinReclassInputData(data: BinreclassInputData)
    fun updateBinReclassInputQty(id: Int, qty: Int)
    fun updateAllBinReclassBin(headerId: Int, newBinTo: String, newFromBin: String)
    fun updateAllBinReclassBin(data: BinreclassInputData)
    fun deleteBinReclassInputData(id: Int)
    suspend fun postDataBinreclass(value: String): Flow<BinreclassInputData>
    fun deleteAllRebinInput()

}

class BinreclassRepositoryImpl(val dao: BinreclassDao, val sharedPreferences: SharedPreferences) :
    BinreclassRepository {

    private val retrofitService by lazy { MasariRetrofit().getClient(sharedPreferences) }

    /**
     * Bin Reclass Header Dao
     */
    override fun getAllBinReclassHeader(): LiveData<List<BinreclassHeader>> =
        dao.getAllBinReclassHeader()

    override suspend fun getBinReclassHeaderDetail(
        binFrom: String,
        binTo: String,
    ): BinreclassHeader = dao.getBinReclassDetail(transferFrom = binFrom, transferTo = binTo)

    override fun getALlSycn(status: Boolean): LiveData<List<BinreclassHeader>> =
        dao.getAllBinreclassheaderByStatus(status)

    override fun getAllUnsycnHeaderData(status: Boolean): List<BinreclassHeader> =
        dao.getAllUnsycnBinreclassheader(status)

    override fun checkBinFromAndBinToCode(binFrom: String, binTo: String): Flow<Boolean> = flow {
        val binFromCount = dao.checkTransferFromCount(binFrom) == 0
        val binToCount = dao.checkTransferToCodeCount(binTo) == 0
        if (binFromCount || binToCount) {
            dao.insertBinReclassHeader(BinreclassHeader(
                transferToBinCode = binTo,
                transferFromBinCode = binFrom,
                date = getNormalDate(),
                documentNo = getDocumentCode()
            ))
            emit(true)
        } else {
            emit(false)
        }
    }

    override fun updateBinFromAndBinToCode(id: Int, binFrom: String, binTo: String): Flow<Boolean> =
        flow {
            val binFromCount = dao.checkTransferFromCount(binFrom) == 0
            val binToCount = dao.checkTransferToCodeCount(binTo) == 0
            if (binFromCount || binToCount) {
                val current = dao.getBinReclassDetailById(id)
                current.apply {
                    transferFromBinCode = binFrom
                    transferToBinCode = binTo
                }
                updateAllBinReclassBin(id, binTo, binFrom)
                dao.updateBinReclassHeader(current)
                emit(true)
            } else {
                emit(false)
            }
        }

    override fun insertBinReclassHeader(data: BinreclassHeader) {
        dao.insertBinReclassHeader(data)
    }

    override fun updateBinReclassHeader(data: BinreclassHeader) {
        dao.updateBinReclassHeader(data)
    }

    override fun deleteBinReclassHeader(data: BinreclassHeader) {
        dao.deleteBinReclassHeader(data)
    }

    override fun deleteAllBinreclass() {
        dao.deleteAllBinreclass()
    }

    /**
     * Bin Reclass Input Dao
     */
    override fun getAllBinreclassInputData(): LiveData<List<BinreclassInputData>> =
        dao.getAllBinreclassInputData()

    override fun updateAllBinReclassBin(headerId: Int, newBinTo: String, newFromBin: String) {
        dao.getBinreclassInputDataDetailByHeaderId(headerId).forEach {
            it.apply {
                binCode = newFromBin
                newBinCode = newBinTo
            }
            dao.updateBinReclassInput(it)
        }
    }

    override fun getBinreclassInputData(headerId: Int): LiveData<List<BinreclassInputData>> =
        dao.getBinreclassInputData(headerId)

    override fun getBinReclassById(id: Int): Flow<BinreclassInputData> = flow {
        emit(dao.getBinreclassInputDataDetailById(id))
    }

    override fun insertBinReclassInputData(data: BinreclassInputData) {
        val getExisting = dao.getBinreclassInputDataDetail(data.transferFromBinCode,
            data.transferToBinCode,
            data.itemIdentifier)
        getExisting?.let {
            it.apply {
                quantity += data.quantity
            }
            dao.updateBinReclassInput(it)
        } ?: kotlin.run {
            dao.insertBinReclassInputData(data)
        }
    }

    override fun getAllUnSyncBinreclassnput(status: Boolean): List<BinreclassInputData> =
        dao.getAllUnsycnBinreclassInput(status)

    override fun updateBinReclassInputQty(id: Int, qty: Int) {
        val data = dao.getBinreclassInputDataDetailById(id)
        data.apply {
            quantity = qty
        }
        dao.updateBinReclassInput(data)
    }

    override fun deleteBinReclassInputData(id: Int) {
        dao.deleteRebinInput(id)
    }

    override suspend fun postDataBinreclass(value: String): Flow<BinreclassInputData> = flow {
        emit(retrofitService.postBinreclassInput(value))
    }

    override fun getAllUnSyncBinreclassnputByHeaderId(
        status: Boolean,
        headerId: Int,
    ): List<BinreclassInputData> = dao.getAllUnsycnRebinclassinputByheaderId(status, headerId)

    override fun updateAllBinReclassBin(data: BinreclassInputData) {
        dao.updateBinReclassInput(data)
    }

    override fun deleteAllRebinInput() {
        dao.deleteAllRebinInput()
    }
}