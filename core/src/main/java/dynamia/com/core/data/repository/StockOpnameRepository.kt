package dynamia.com.core.data.repository

import androidx.lifecycle.LiveData
import com.google.gson.Gson
import dynamia.com.core.data.dao.StockOpnameDao
import dynamia.com.core.data.entinty.StockCheckingData
import dynamia.com.core.data.entinty.StockOpnameData
import dynamia.com.core.data.entinty.StockOpnameInputData
import dynamia.com.core.domain.ErrorResponse
import dynamia.com.core.domain.MasariAPI
import dynamia.com.core.domain.ResultWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface StockOpnameRepository {
    /**
     * Stock Opname
     */
    fun getALlStockOpname(page: Int = 20): LiveData<List<StockOpnameData>>
    fun getCountStockOpname(): LiveData<Int>
    fun getStockOpnameDetail(id: Int): StockOpnameData
    fun getStockOpnameDetailByBarcode(barcode: String, binCode: String): Flow<StockOpnameData>
    fun getStockOpnameDetailByBarcode(barcode: String, id: Int): Flow<StockOpnameData>
    fun getStockOpnameDetailByBarcode(barcode: String): Flow<StockOpnameData>
    suspend fun insertStockOpnameData(data: StockOpnameData)
    suspend fun countStockOpnameData(): Int
    fun deleteAllStockOpname()

    /**
     * Stock Opname Input
     */

    fun getAllInputStockOpname(): LiveData<List<StockOpnameInputData>>
    fun getAllUnsyncStockInput(): List<StockOpnameInputData>
    fun getAllInputStockOpnameByDocumentNo(documentNo: String): LiveData<List<StockOpnameInputData>>
    suspend fun getInputStockOpnameDetail(id: Int): Flow<StockOpnameInputData>
    fun getCountQtyInput(id: Int): LiveData<Int>
    suspend fun insertInputStockOpname(data: StockOpnameInputData)
    suspend fun updateInputStockOpname(data: StockOpnameInputData)
    suspend fun updateInputStockOpnameQty(id: Int, newQty: Int)
    suspend fun deleteInputStockOpname(id: Int)
    suspend fun deleteAllInputStockOpname()


    /**
     * Stock Opname API Repository
     */

    suspend fun getStockOpnameAsync(): Flow<ResultWrapper<MutableList<StockOpnameData>>>
    suspend fun postStockOpnameData(value: String): Flow<StockOpnameInputData>

    suspend fun getStockCheck(value: String): Flow<ResultWrapper<MutableList<StockCheckingData>>>

}

class StockOpnameRepositoryImpl @Inject constructor(
    val dao: StockOpnameDao,
    private val retrofitService: MasariAPI,
) : StockOpnameRepository {

    override fun getALlStockOpname(page: Int): LiveData<List<StockOpnameData>> =
        dao.getALlStockOpname(page)

    override fun getCountStockOpname(): LiveData<Int> = dao.getCount()

    override fun getStockOpnameDetail(id: Int): StockOpnameData = dao.getStockOpnameDetail(id)

    override fun getStockOpnameDetailByBarcode(
        barcode: String,
        binCode: String,
    ): Flow<StockOpnameData> = flow {
        val value = dao.getStockOpnameDetailBinCode(barcode, binCode)
        if (value != null) {
            emit(value)
        } else {
            dao.getStockOpnameDetailItemRef(barcode, binCode)?.let { emit(it) }
                ?: kotlin.run { error("Barcode dan Bincode data tidak ditemukan") }
        }
    }

    override fun getStockOpnameDetailByBarcode(barcode: String): Flow<StockOpnameData> = flow {
        val value = dao.getStockOpnameDetailBinCode(barcode)
        if (value != null) {
            emit(value)
        } else {
            dao.getStockOpnameDetailItemRef(barcode)?.let { emit(it) }
                ?: kotlin.run { error("Barcode dan Bincode data tidak ditemukan") }
        }
    }

    override fun getCountQtyInput(id: Int): LiveData<Int> = dao.getStockOpnameInputCount(id)

    override fun getStockOpnameDetailByBarcode(barcode: String, id: Int): Flow<StockOpnameData> =
        flow {
            emit(dao.getStockOpnameDetail(barcode, id))
        }


    override suspend fun insertStockOpnameData(data: StockOpnameData) {
        dao.insertStockOpnameData(data)
    }

    override fun deleteAllStockOpname() {
        dao.deleteAllStockOpname()
    }


    /**
     * Stock Opname Input
     */


    override fun getAllInputStockOpname(): LiveData<List<StockOpnameInputData>> =
        dao.getALlStockOpnameInput()

    override fun getAllUnsyncStockInput(): List<StockOpnameInputData> =
        dao.getAllUnsyncOpnameInput()


    override fun getAllInputStockOpnameByDocumentNo(documentNo: String): LiveData<List<StockOpnameInputData>> =
        dao.getAllStockOpnameInput(documentNo)

    override suspend fun getInputStockOpnameDetail(id: Int): Flow<StockOpnameInputData> = flow {
        emit(dao.getStockOpnameInputDetail(id))
    }

    override suspend fun insertInputStockOpname(data: StockOpnameInputData) {
        val dataOpname = dao.getStockOpnameDetail(id = data.headerId)
        dataOpname.apply {
            this.alredyScanned += data.quantity
        }
        dao.insertStockOpnameInput(data)
        dao.updateStockOpname(dataOpname)
    }

    override suspend fun updateInputStockOpnameQty(id: Int, newQty: Int) {
        val stockOpnameInputData = dao.getStockOpnameInputDetail(id)
        val stockOpnameData =
            dao.getStockOpnameDetail(stockOpnameInputData.headerId)
        val totalQty = stockOpnameData.alredyScanned - stockOpnameInputData.quantity + newQty
        stockOpnameData.apply {
            alredyScanned = totalQty
        }
        stockOpnameInputData.apply {
            quantity = newQty
        }
        dao.updateStockOpnameInput(stockOpnameInputData)
        dao.updateStockOpname(stockOpnameData)
    }

    override suspend fun updateInputStockOpname(data: StockOpnameInputData) {
        dao.updateStockOpnameInput(data)
    }

    override suspend fun deleteInputStockOpname(id: Int) {
        val stockOpnameInputData = dao.getStockOpnameInputDetail(id)
        val stockOpnameData =
            dao.getStockOpnameDetail(stockOpnameInputData.documentNo, stockOpnameInputData.itemNo)
        stockOpnameData.apply {
            alredyScanned -= stockOpnameInputData.quantity
        }
        dao.deleteStockOpnameInput(stockOpnameInputData)
        dao.updateStockOpname(stockOpnameData)
    }

    override suspend fun deleteAllInputStockOpname() {
        dao.deleteAllStockOpnameInput()
    }

    override suspend fun countStockOpnameData(): Int = dao.getStockOpnameDataCount()

    /**
     * Stock Opname API Repository
     */
    override suspend fun getStockOpnameAsync(): Flow<ResultWrapper<MutableList<StockOpnameData>>> =
        flow {
            try {
                val result = retrofitService.getStockOpname()
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

    override suspend fun postStockOpnameData(value: String): Flow<StockOpnameInputData> =
        flow { emit(retrofitService.postStockOpnameInput(value)) }

    override suspend fun getStockCheck(value: String): Flow<ResultWrapper<MutableList<StockCheckingData>>> =
        flow {
            try {
                val result = retrofitService.getCheckStock(value)
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
}