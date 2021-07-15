package dynamia.com.core.data.repository

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import dynamia.com.core.data.dao.PurchaseOrderDao
import dynamia.com.core.data.entinty.PurchaseInputData
import dynamia.com.core.data.entinty.PurchaseOrderHeader
import dynamia.com.core.data.entinty.PurchaseOrderLine
import dynamia.com.core.domain.ErrorResponse
import dynamia.com.core.domain.MasariRetrofit
import dynamia.com.core.domain.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

interface PurchaseOrderRepository {
    /**
     * Purchase Order Header
     */
    fun getAllPurchaseOrderHeader(): LiveData<List<PurchaseOrderHeader>>
    suspend fun insertPurchaseOrderHeader(value: PurchaseOrderHeader)
    suspend fun getPurchaseOrderDetail(no: String): Flow<PurchaseOrderHeader>
    suspend fun deleteAllPurchaseOrderHeader()

    /**
     * Purchase Order Line
     */

    suspend fun insertPurchaseOrderLine(value: PurchaseOrderLine)
    fun getPurchaseOrderLineByNo(no: String): LiveData<List<PurchaseOrderLine>>
    fun getPurchaseOrderLineByBarcode(no: String, identifier: String): Flow<PurchaseOrderLine>
    fun getPurchaseOrderLineDetailById(id: Int): PurchaseOrderLine
    suspend fun deleteAllPurchaseOrderLine()


    /**
     * Purchase Order Input Data
     */

    fun getAllPurchaseInputData(): LiveData<List<PurchaseInputData>>
    fun getAllPurchaseInputByNo(no: String): LiveData<List<PurchaseInputData>>
    fun getAllUnSyncPurchaseInput(status: Boolean = false): List<PurchaseInputData>
    suspend fun getPurchaseInputDetail(id: Int): Flow<PurchaseInputData>
    suspend fun insertPurchaseOrderData(value: PurchaseInputData): Boolean
    suspend fun updatePurchaseInputDataQty(id: Int, newQty: Int)
    suspend fun updatePurchaseInputData(value: PurchaseInputData)
    suspend fun deletePurchaseInputData(id: Int)
    suspend fun deleteAllPurchaseInputData()

    /**
     * Network Repository Purchase Order
     * Calling From API
     */

    suspend fun getPurchaseOrderHeaderAsync(): Flow<ResultWrapper<MutableList<PurchaseOrderHeader>>>
    suspend fun getPurchaseOrderLineAsync(): Flow<ResultWrapper<MutableList<PurchaseOrderLine>>>
    suspend fun postPurchaseOrderData(value: String): Flow<PurchaseInputData>

}

class PurchaseOrderRepositoryImpl(
    private val dao: PurchaseOrderDao,
    private val sharedPreferences: SharedPreferences,
) : PurchaseOrderRepository {
    private val retrofitService by lazy { MasariRetrofit().getClient(sharedPreferences) }

    /**
     * Purchase Order Header
     */
    override fun getAllPurchaseOrderHeader(): LiveData<List<PurchaseOrderHeader>> =
        dao.getAllPurchaseOrderHeader()

    override suspend fun insertPurchaseOrderHeader(value: PurchaseOrderHeader) {
        dao.insertPurchaseOrderHeader(value)
    }

    override suspend fun getPurchaseOrderDetail(no: String): Flow<PurchaseOrderHeader> = flow {
        emit(dao.getPurchaseOrderDetail(no))
    }


    override suspend fun deleteAllPurchaseOrderHeader() {
        dao.deleteAllPurchaseOrderHeader()
    }

    /**
     * Purchase Order Line
     */
    override suspend fun insertPurchaseOrderLine(value: PurchaseOrderLine) {
        dao.insertPurchaseOrderLine(value)
    }

    override fun getPurchaseOrderLineByNo(no: String): LiveData<List<PurchaseOrderLine>> =
        dao.getPurchaseOrderLineDetailByNo(no)

    override fun getPurchaseOrderLineDetailById(id: Int): PurchaseOrderLine =
        dao.getPurchaseOrderLineDetailById(id)

    override suspend fun deleteAllPurchaseOrderLine() {
        dao.deleteAllPurchaseOrderLine()
    }

    /**
     * Purchase Order Input Data
     */
    override fun getAllPurchaseInputData(): LiveData<List<PurchaseInputData>> =
        dao.getAllPurchaseInputData()

    override fun getAllPurchaseInputByNo(no: String): LiveData<List<PurchaseInputData>> =
        dao.getAllPurchaseInputDataByNo(no)

    override suspend fun getPurchaseInputDetail(id: Int): Flow<PurchaseInputData> = flow {
        emit(dao.getPurchaseInputDataDetail(id))
    }

    override suspend fun insertPurchaseOrderData(value: PurchaseInputData): Boolean =
        runBlocking(Dispatchers.IO) {
            try {
                val lineData =
                    dao.getPurchaseOrderLineByLineno(value.documentNo, value.lineNo)
                lineData.apply {
                    this.alredyScanned += value.quantity
                }
                dao.insertPurchaseOrderData(value)
                dao.updatePurchaseOrderLine(lineData)
                true
            } catch (e: Exception) {
                false
            }
        }

    override suspend fun updatePurchaseInputData(value: PurchaseInputData) {
        dao.updatePurchaseInputData(value)
    }

    override suspend fun updatePurchaseInputDataQty(id: Int, newQty: Int) {
        val purchaseInput = dao.getPurchaseInputDataDetail(id)
        val lineData =
            dao.getPurchaseOrderLineByLineno(purchaseInput.documentNo, purchaseInput.lineNo)
        val totalQty = lineData.alredyScanned - purchaseInput.quantity + newQty
        lineData.apply {
            alredyScanned = totalQty
        }
        purchaseInput.apply {
            quantity = newQty
        }
        dao.updatePurchaseInputData(purchaseInput)
        dao.updatePurchaseOrderLine(lineData)
    }

    override fun getPurchaseOrderLineByBarcode(
        no: String,
        identifier: String,
    ): Flow<PurchaseOrderLine> = flow {
        emit(dao.getPurchaseOrderLineByBarcode(no, identifier))
    }

    override suspend fun deletePurchaseInputData(id: Int) {
        val transferInput = dao.getPurchaseInputDataDetail(id)
        val lineData =
            dao.getPurchaseOrderLineByLineno(transferInput.documentNo, transferInput.lineNo)
        lineData.apply {
            alredyScanned -= transferInput.quantity
        }
        dao.deletePurchaseInput(id)
        dao.updatePurchaseOrderLine(lineData)
    }

    override fun getAllUnSyncPurchaseInput(status: Boolean): List<PurchaseInputData> =
        dao.getAllUnsyncPurchaseInput(status)

    override suspend fun deleteAllPurchaseInputData() {
        dao.deleteAllPurchaseInputData()
    }

    /**
     * Network Repository Purchase Order
     * Calling From API
     */
    override suspend fun getPurchaseOrderHeaderAsync(): Flow<ResultWrapper<MutableList<PurchaseOrderHeader>>> =
        flow {
            try {
                val result = retrofitService.getPurchaseOrderHeader()
                when (result.isSuccessful) {
                    true -> {
                        result.body()?.value?.let { emit(ResultWrapper.Success(it.toMutableList())) }
                    }
                    else -> {
                        result.errorBody()?.let {
                            val errorMessage = Gson().fromJson(
                                it.charStream().readText(),
                                ErrorResponse::class.java
                            )
                            emit(ResultWrapper.GenericError(result.code(), errorMessage))
                        }
                    }
                }
            } catch (e: Exception) {
                emit(ResultWrapper.NetworkError(e.localizedMessage))
            }
        }

    override suspend fun getPurchaseOrderLineAsync(): Flow<ResultWrapper<MutableList<PurchaseOrderLine>>> =
        flow {
            try {
                val result = retrofitService.getPurchaseOrderLine()
                when (result.isSuccessful) {
                    true -> {
                        result.body()?.value?.let { emit(ResultWrapper.Success(it.toMutableList())) }
                    }
                    else -> {
                        result.errorBody()?.let {
                            val errorMessage = Gson().fromJson(
                                it.charStream().readText(),
                                ErrorResponse::class.java
                            )
                            emit(ResultWrapper.GenericError(result.code(), errorMessage))
                        }
                    }
                }
            } catch (e: Exception) {
                emit(ResultWrapper.NetworkError(e.localizedMessage))
            }
        }

    override suspend fun postPurchaseOrderData(value: String): Flow<PurchaseInputData> =
        flow { emit(retrofitService.postPurchaseOrderData(value)) }
}