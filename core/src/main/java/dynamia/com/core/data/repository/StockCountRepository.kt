package dynamia.com.core.data.repository

import androidx.lifecycle.LiveData
import dynamia.com.core.data.dao.StockCountDao
import dynamia.com.core.data.model.StockCount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

interface StockCountRepository {
    fun getAllStockCount(): LiveData<MutableList<StockCount>>
    fun insertStockCount(data: StockCount): Job
    suspend fun clearStockCount()
    suspend fun getAllUnsycnStockCount(): MutableList<StockCount>
    fun updateStockCount(data: StockCount)
    suspend fun checkSN(serialNo: String): Boolean
    fun deleteSn(data: StockCount)
}

class StockCountRepositoryImpl(val dao: StockCountDao) : StockCountRepository {
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    override fun getAllStockCount(): LiveData<MutableList<StockCount>> =
        dao.getAllStockCount()

    override fun insertStockCount(data: StockCount): Job = scope.launch(Dispatchers.IO) {
        dao.insertStockCount(data)
    }

    override suspend fun clearStockCount() {
        dao.clearStockCount()
    }

    override suspend fun getAllUnsycnStockCount(): MutableList<StockCount> =
        dao.getAllUnsycnStockCount()

    override fun updateStockCount(data: StockCount) {
        dao.updateStockCount(data)
    }

    override suspend fun checkSN(serialNo: String): Boolean =
        dao.checkSN(serialNo).isEmpty()

    override fun deleteSn(data: StockCount) {
        dao.deleteSN(data)
    }
}