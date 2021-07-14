package dynamia.com.core.data.repository

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import dynamia.com.core.data.dao.StockOpnameDao
import dynamia.com.core.data.entinty.StockCheckingData
import dynamia.com.core.data.entinty.StockOpnameData
import dynamia.com.core.data.entinty.StockOpnameInputData
import dynamia.com.core.domain.ErrorResponse
import dynamia.com.core.domain.MasariRetrofit
import dynamia.com.core.domain.ResultWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface StockOpnameRepository {
    /**
     * Stock Opname
     */
    fun getALlStockOpname(): LiveData<List<StockOpnameData>>
    fun getStockOpnameDetail(id: Int): StockOpnameData
    fun getStockOpnameDetailByBarcode(barcode: String): Flow<StockOpnameData>
    suspend fun insertStockOpnameData(data: StockOpnameData)
    fun deleteAllStockOpname()

    /**
     * Stock Opname Input
     */

    fun getAllInputStockOpname(): LiveData<List<StockOpnameInputData>>
    fun getAllUnsyncStockInput(): List<StockOpnameInputData>
    fun getAllInputStockOpnameByDocumentNo(documentNo: String): LiveData<List<StockOpnameInputData>>
    suspend fun getInputStockOpnameDetail(id: Int): Flow<StockOpnameInputData>
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

class StockOpnameRepositoryImpl(val dao: StockOpnameDao, val sharedPreferences: SharedPreferences) :
    StockOpnameRepository {
    private val retrofitService by lazy { MasariRetrofit().getClient(sharedPreferences) }

    override fun getALlStockOpname(): LiveData<List<StockOpnameData>> = dao.getALlStockOpname()

    override fun getStockOpnameDetail(id: Int): StockOpnameData = dao.getStockOpnameDetail(id)

    override fun getStockOpnameDetailByBarcode(barcode: String): Flow<StockOpnameData> = flow {
        emit(dao.getStockOpnameDetail(barcode))
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
        val dataOpname = dao.getStockOpnameDetail(data.documentNo, data.itemNo)
        dataOpname.apply {
            this.alredyScanned += data.quantity
        }
        dao.insertStockOpnameInput(data)
        dao.updateStockOpname(dataOpname)
    }

    override suspend fun updateInputStockOpnameQty(id: Int, newQty: Int) {
        val stockOpnameInputData = dao.getStockOpnameInputDetail(id)
        val stockOpnameData =
            dao.getStockOpnameDetail(stockOpnameInputData.documentNo, stockOpnameInputData.itemNo)
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

    /**
     * Stock Opname API Repository
     */
    override suspend fun getStockOpnameAsync(): Flow<ResultWrapper<MutableList<StockOpnameData>>> =
        flow {
            try {
                val result = retrofitService.getStockOpname()
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

    override suspend fun postStockOpnameData(value: String): Flow<StockOpnameInputData> =
        flow { emit(retrofitService.postStockOpnameInput(value)) }

    override suspend fun getStockCheck(value: String): Flow<ResultWrapper<MutableList<StockCheckingData>>> =
        flow {
            try {
                val result = retrofitService.getCheckStock(value)
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
}