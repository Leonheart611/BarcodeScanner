package dynamia.com.core.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import dynamia.com.core.data.dao.InventoryDao
import dynamia.com.core.data.entinty.InventoryInputData
import dynamia.com.core.data.entinty.InventoryPickHeader
import dynamia.com.core.data.entinty.InventoryPickLine
import dynamia.com.core.domain.ErrorResponse
import dynamia.com.core.domain.MasariAPI
import dynamia.com.core.domain.ResultWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface InventoryRepository {
    fun insertInventoryHeaderAll(datas: List<InventoryPickHeader>)
    fun getAllInventoryHeader(page: Int): LiveData<List<InventoryPickHeader>>
    fun getCountInventoryHeader(): LiveData<Int>
    fun getInventoryHeaderDetail(no: String): Flow<InventoryPickHeader>
    fun deleteAllInventoryHeader()

    /**
     * Inventory Line DAO
     */

    fun insertInventoryLineAll(datas: List<InventoryPickLine>)
    fun getAllInventoryPickLine(no: String, page: Int = 20): LiveData<List<InventoryPickLine>>
    fun getDetailInventoryPickLine(
        no: String,
        binCode: String,
        itemNoRef: String
    ): Flow<InventoryPickLine>

    fun updateInventoryPickLine(value: InventoryPickLine)
    fun getInventoryPickLineLiveData(id: Int): LiveData<InventoryPickLine>
    fun deleteAllInventoryPickLine()

    /**
     * Inventory Input Data
     */
    fun getInventoryQty(no: String): LiveData<Int>
    fun getInventoryAlreadyQty(no: String): LiveData<Int>
    suspend fun insertInputInventory(data: InventoryInputData): Flow<Boolean>
    fun getAllInventoryInputData(): LiveData<List<InventoryInputData>>
    fun getUnpostedInventoryData(): List<InventoryInputData>
    fun getInventoryInputData(no: String): LiveData<List<InventoryInputData>>
    fun getInventoryInputDetail(id: Int): Flow<InventoryInputData>
    fun updateInventoryQty(id: Int, newQty: Int): Flow<Boolean>
    fun updateInventoryInput(value: InventoryInputData)
    suspend fun deleteInventoryInput(id: Int)
    fun deleteAllInventoryInput()

    /**
     * Inventory Get From Remote By Cathrine
     */
    suspend fun getInventoryHeaderRemote(): Flow<ResultWrapper<MutableList<InventoryPickHeader>>>
    suspend fun getInventoryLineRemote(): Flow<ResultWrapper<MutableList<InventoryPickLine>>>
    suspend fun postInventoryData(value: String): Flow<InventoryInputData>
}

class InventoryRepositoryImpl @Inject constructor(
    val dao: InventoryDao,
    private val retrofitService: MasariAPI
) : InventoryRepository {

    override fun insertInventoryHeaderAll(datas: List<InventoryPickHeader>) {
        dao.insertInventoryHeaderAll(datas)
    }

    override fun getAllInventoryHeader(page: Int): LiveData<List<InventoryPickHeader>> {
        return dao.getAllInventoryHeader(page)
    }

    override fun getCountInventoryHeader(): LiveData<Int> = dao.getCount()

    override fun getInventoryHeaderDetail(no: String): Flow<InventoryPickHeader> = flow {
        emit(dao.getInventoryHeaderDetail(no))
    }

    override fun deleteAllInventoryHeader() {
        dao.deleteAllInventoryHeader()
    }

    override fun insertInventoryLineAll(datas: List<InventoryPickLine>) {
        dao.insertInventoryLineAll(datas)
    }

    override fun getAllInventoryPickLine(no: String, page: Int): LiveData<List<InventoryPickLine>> {
        return dao.getAllInventoryPickLine(no, page)
    }

    override fun updateInventoryPickLine(value: InventoryPickLine) {
        dao.updateInventoryPickLine(value)
    }

    override fun getInventoryPickLineLiveData(id: Int): LiveData<InventoryPickLine> =
        dao.getInventoryPickLineLiveData(id)

    override fun deleteAllInventoryPickLine() {
        dao.deleteAllInventoryPickLine()
    }

    override suspend fun insertInputInventory(data: InventoryInputData): Flow<Boolean> = flow {
        try {
            getDetailInventoryPickLine(
                data.documentNo,
                data.binCode,
                data.itemNo
            ).collect { lineData ->
                if ((lineData.alredyScanned + data.quantity) <= lineData.quantity) {
                    lineData.apply {
                        this.alredyScanned += data.quantity
                    }
                    dao.insertInputInventory(data)
                    dao.updateInventoryPickLine(lineData)
                    emit(true)
                } else {
                    error("Qty has Reach maximum allowed")
                }
            }
        } catch (e: Exception) {
            e.message?.let {
                Log.e("Error Input", it)
                error(it)
            }
        }
    }

    override fun getAllInventoryInputData(): LiveData<List<InventoryInputData>> {
        return dao.getAllInventoryInputData()
    }

    override fun getDetailInventoryPickLine(
        no: String,
        binCode: String,
        itemNoRef: String
    ): Flow<InventoryPickLine> = flow {
        val result = dao.getInventoryPickLineDetail(no, binCode, itemNoRef)
        if (result != null) {
            emit(result)
        } else {
            dao.getInventoryPickLineDetailItemNo(no, binCode, itemNoRef)?.let { emit(it) }
                ?: kotlin.run { error("Data tidak di temukan") }
        }

    }

    override fun updateInventoryInput(value: InventoryInputData) {
        dao.updateInputData(value)
    }

    override fun getInventoryInputDetail(id: Int): Flow<InventoryInputData> = flow {
        emit(dao.getInventoryInputDetail(id))
    }

    override fun updateInventoryQty(id: Int, newQty: Int): Flow<Boolean> = flow {
        val inputData = dao.getInventoryInputDetail(id)
        val lineData = dao.getInventoryPickLineDetail(
            inputData.documentNo,
            inputData.binCode,
            inputData.itemNo
        )
        val totalQty = (lineData?.alredyScanned ?: 0) - inputData.quantity + newQty
        if (totalQty <= (lineData?.quantity ?: 0)) {
            lineData?.apply {
                alredyScanned = totalQty
            }
            inputData.apply {
                quantity = newQty
            }
            dao.updateInputData(inputData)
            lineData?.let { dao.updateInventoryPickLine(it) }
            emit(true)
        } else {
            error("Qty yang diinput melebihi yang diperbolehkan")
        }
    }


    override fun getInventoryQty(no: String): LiveData<Int> = dao.getQtyScanTotal(no)

    override fun getInventoryAlreadyQty(no: String): LiveData<Int> = dao.getAlreadyScanTotal(no)

    override fun getInventoryInputData(no: String): LiveData<List<InventoryInputData>> {
        return dao.getInventoryInputData(no)
    }

    override fun deleteAllInventoryInput() {
        dao.deleteAllInventoryInput()
    }

    override fun getUnpostedInventoryData(): List<InventoryInputData> {
        return dao.getAllUnsycnInputData()
    }

    override suspend fun deleteInventoryInput(id: Int) {
        try {
            val inputData = dao.getInventoryInputDetail(id)
            val lineData = dao.getInventoryPickLineDetail(
                inputData.documentNo,
                inputData.binCode,
                inputData.itemNo
            )
            lineData?.apply {
                alredyScanned -= inputData.quantity
            }
            dao.deleteInventoryInput(inputData)
            lineData?.let { dao.updateInventoryPickLine(it) }
        } catch (e: Exception) {
            error(e.localizedMessage)
        }

    }

    /**
     * Inventory From Remote By Catherine
     */

    override suspend fun getInventoryHeaderRemote(): Flow<ResultWrapper<MutableList<InventoryPickHeader>>> =
        flow {
            try {
                val result = retrofitService.getInventoryPickHeader()
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

    override suspend fun getInventoryLineRemote(): Flow<ResultWrapper<MutableList<InventoryPickLine>>> =
        flow {
            try {
                val result = retrofitService.getInventoryPickLine()
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

    override suspend fun postInventoryData(value: String): Flow<InventoryInputData> = flow {
        try {
            emit(retrofitService.postInventoryInput(value))
        } catch (e: Exception) {
            error(e.localizedMessage)
        }

    }
}