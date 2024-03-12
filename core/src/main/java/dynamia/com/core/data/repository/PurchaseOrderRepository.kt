package dynamia.com.core.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.gson.Gson
import dynamia.com.core.data.dao.PurchaseOrderDao
import dynamia.com.core.data.entinty.PurchaseInputData
import dynamia.com.core.data.entinty.PurchaseOrderHeader
import dynamia.com.core.data.entinty.PurchaseOrderLine
import dynamia.com.core.domain.ErrorResponse
import dynamia.com.core.domain.MasariAPI
import dynamia.com.core.domain.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

interface PurchaseOrderRepository {
    /**
     * Purchase Order Header
     */
    fun getAllPurchaseOrderHeader(page: Int = 20): LiveData<List<PurchaseOrderHeader>>
    fun getCountPurchaseOrderHeader(): LiveData<Int>
    suspend fun insertPurchaseOrderHeader(value: PurchaseOrderHeader)
    suspend fun getPurchaseOrderDetail(no: String): Flow<PurchaseOrderHeader>
    suspend fun deleteAllPurchaseOrderHeader()
    suspend fun getPurchaseOrderHeaderCount(): Int
    fun getAllPurchaseOrderPage(): Flow<PagingData<PurchaseOrderHeader>>

    /**
     * Purchase Order Line
     */

    fun getPurchaseOrderLineLiveData(id: Int): LiveData<PurchaseOrderLine>
    fun getPurchaseQtyTotal(no: String): LiveData<Int>
    fun getPurchaseAlreadyScan(no: String): LiveData<Int>
    suspend fun insertPurchaseOrderLine(value: MutableList<PurchaseOrderLine>)
    fun getPurchaseOrderLineByNo(no: String, page: Int = 20): LiveData<List<PurchaseOrderLine>>
    fun getPurchaseOrderLineByBarcode(no: String, identifier: String): Flow<PurchaseOrderLine>
    fun getPurchaseOrderLineDetailById(id: Int): Flow<PurchaseOrderLine>
    suspend fun deleteAllPurchaseOrderLine()


    /**
     * Purchase Order Input Data
     */

    fun getAllPurchaseInputData(): LiveData<List<PurchaseInputData>>
    fun getAllPurchaseInputByNo(
        no: String,
        accidentallyScan: Boolean
    ): LiveData<List<PurchaseInputData>>

    fun getAllUnSyncPurchaseInput(status: Boolean = false): List<PurchaseInputData>
    suspend fun getPurchaseInputDetail(id: Int): Flow<PurchaseInputData>
    suspend fun insertPurchaseOrderData(value: PurchaseInputData): Boolean
    suspend fun updatePurchaseInputDataQty(id: Int, newQty: Int)
    suspend fun updatePurchaseInputData(value: PurchaseInputData)
    suspend fun deletePurchaseInputData(id: Int)
    suspend fun deleteAllPurchaseInputData()
    fun getPurchaseOrderAccidentInput(no: String): LiveData<Int>

    /**
     * Network Repository Purchase Order
     * Calling From API
     */

    suspend fun getPurchaseOrderHeaderAsync(): Flow<ResultWrapper<MutableList<PurchaseOrderHeader>>>
    suspend fun getPurchaseOrderLineAsync(): Flow<ResultWrapper<MutableList<PurchaseOrderLine>>>
    suspend fun postPurchaseOrderData(value: String): Flow<PurchaseInputData>

}

class PurchaseOrderRepositoryImpl @Inject constructor(
    private val dao: PurchaseOrderDao,
    private val retrofitService: MasariAPI,
) : PurchaseOrderRepository {

    /**
     * Purchase Order Header
     */


    override fun getAllPurchaseOrderHeader(page: Int): LiveData<List<PurchaseOrderHeader>> =
        dao.getAllPurchaseOrderHeader(page)

    override fun getCountPurchaseOrderHeader(): LiveData<Int> = dao.getCount()

    override fun getAllPurchaseOrderPage(): Flow<PagingData<PurchaseOrderHeader>> {
        val pagingSourceFactory = { dao.getAllPurchaseOrderPage() }

        return Pager(
            config = PagingConfig(pageSize = 25, enablePlaceholders = false),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override suspend fun insertPurchaseOrderHeader(value: PurchaseOrderHeader) {
        dao.insertPurchaseOrderHeader(value)
    }

    override suspend fun getPurchaseOrderDetail(no: String): Flow<PurchaseOrderHeader> = flow {
        emit(dao.getPurchaseOrderDetail(no))
    }

    override suspend fun getPurchaseOrderHeaderCount(): Int = dao.getPurchaseOrderHeaderCount()

    override suspend fun deleteAllPurchaseOrderHeader() {
        dao.deleteAllPurchaseOrderHeader()
    }

    /**
     * Purchase Order Line
     */
    override suspend fun insertPurchaseOrderLine(value: MutableList<PurchaseOrderLine>) {
        val emptyQty = value.filter { it.quantity <= 0 }
        if (emptyQty.isNotEmpty())
            value.removeAll(emptyQty)
        dao.insertPurchaseOrderLine(value)
    }

    override fun getPurchaseOrderLineLiveData(id: Int): LiveData<PurchaseOrderLine> =
        dao.getPurchaseOrderLineLiveData(id)

    override fun getPurchaseOrderLineByNo(
        no: String,
        page: Int
    ): LiveData<List<PurchaseOrderLine>> =
        dao.getPurchaseOrderLineDetailByNo(no, page)

    override fun getPurchaseOrderLineDetailById(id: Int): Flow<PurchaseOrderLine> = flow {
        try {
            emit(dao.getPurchaseOrderLineDetailById(id))
        } catch (e: Exception) {
            error("Item Name not found")
        }
    }


    override fun getPurchaseQtyTotal(no: String): LiveData<Int> = dao.getQtyScanTotal(no)

    override fun getPurchaseAlreadyScan(no: String): LiveData<Int> = dao.getAlreadyScanTotal(no)

    override suspend fun deleteAllPurchaseOrderLine() {
        dao.deleteAllPurchaseOrderLine()
    }

    /**
     * Purchase Order Input Data
     */
    override fun getAllPurchaseInputData(): LiveData<List<PurchaseInputData>> =
        dao.getAllPurchaseInputData()

    override fun getAllPurchaseInputByNo(
        no: String,
        accidentallyScan: Boolean
    ): LiveData<List<PurchaseInputData>> =
        dao.getAllPurchaseInputDataByNo(no, accidentallyScan)

    override suspend fun getPurchaseInputDetail(id: Int): Flow<PurchaseInputData> = flow {
        emit(dao.getPurchaseInputDataDetail(id))
    }

    override suspend fun insertPurchaseOrderData(value: PurchaseInputData): Boolean =
        runBlocking(Dispatchers.IO) {
            try {
                if (value.lineNo == 0) {
                    value.apply { accidentalScanned = true }
                    dao.insertPurchaseOrderData(value)
                } else {
                    val lineData =
                        dao.getPurchaseOrderLineByLineno(value.documentNo, value.lineNo)
                    lineData.apply {
                        this.alredyScanned += value.quantity
                    }
                    dao.insertPurchaseOrderData(value)
                    dao.updatePurchaseOrderLine(lineData)
                }
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
        val result = dao.getPurchaseOrderLineByBarcode(no, identifier)
        if (result != null) {
            emit(result)
        } else {
            emit(dao.getPurchaseOrderLineByItemRef(no, identifier))
        }
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

    override fun getPurchaseOrderAccidentInput(no: String): LiveData<Int> =
        dao.getPurchaseOrderAccidentInput(no)

    /**
     * Network Repository Purchase Order
     * Calling From API
     */
    override suspend fun getPurchaseOrderHeaderAsync(): Flow<ResultWrapper<MutableList<PurchaseOrderHeader>>> =
        flow {
            try {
                val result = retrofitService.getPurchaseOrderHeader()
                when (result.code()) {
                    200 -> result.body()?.value?.let { emit(ResultWrapper.Success(it.toMutableList())) }
                    400 -> emit(ResultWrapper.SuccessEmptyValue)
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
                when (result.code()) {
                    200 -> result.body()?.value?.let { emit(ResultWrapper.Success(it.toMutableList())) }
                    400 -> emit(ResultWrapper.SuccessEmptyValue)
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