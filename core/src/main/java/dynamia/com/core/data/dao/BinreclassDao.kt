package dynamia.com.core.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dynamia.com.core.data.entinty.BinreclassHeader
import dynamia.com.core.data.entinty.BinreclassInputData

@Dao
interface BinreclassDao {
    /**
     * Bin Reclass Header Dao
     */
    @Query("SELECT * FROM BinreclassHeader")
    fun getAllBinReclassHeader(): LiveData<List<BinreclassHeader>>

    @Query("SELECT count(*) FROM BinreclassHeader WHERE transferToBinCode =:transferTobinCode")
    fun checkTransferToCodeCount(transferTobinCode: String): Int

    @Query("SELECT count(*) FROM BinreclassHeader WHERE transferFromBinCode =:transferFrom")
    fun checkTransferFromCount(transferFrom: String): Int

    @Query("SELECT*FROM BinreclassHeader WHERE sync_status =:status")
    fun getAllUnsycnBinreclassheader(status: Boolean): List<BinreclassHeader>

    @Query("SELECT*FROM BinreclassHeader WHERE sync_status =:status")
    fun getAllBinreclassheaderByStatus(status: Boolean): LiveData<List<BinreclassHeader>>

    @Query("SELECT*FROM BinreclassHeader WHERE transferFromBinCode =:transferFrom AND transferToBinCode =:transferTo")
    fun getBinReclassDetail(transferFrom: String, transferTo: String): BinreclassHeader

    @Query("SELECT*FROM BinreclassHeader WHERE id=:id")
    fun getBinReclassDetailById(id: Int): BinreclassHeader

    @Insert(onConflict = OnConflictStrategy.ABORT, entity = BinreclassHeader::class)
    fun insertBinReclassHeader(data: BinreclassHeader)

    @Update(onConflict = OnConflictStrategy.ABORT, entity = BinreclassHeader::class)
    fun updateBinReclassHeader(data: BinreclassHeader)

    @Delete(entity = BinreclassHeader::class)
    fun deleteBinReclassHeader(data: BinreclassHeader)

    @Query("DELETE FROM BinreclassHeader")
    fun deleteAllBinreclass()

    /**
     * Bin Reclass Input Dao
     */
    @Query("SELECT * FROM BinreclassInputData")
    fun getAllBinreclassInputData(): LiveData<List<BinreclassInputData>>

    @Query("SELECT * FROM BinreclassInputData WHERE headerId =:headerId")
    fun getBinreclassInputData(headerId: Int): LiveData<List<BinreclassInputData>>

    @Query("SELECT * FROM BinreclassInputData WHERE transferFromBinCode =:transferFrom AND transferToBinCode =:transferTo AND itemNo =:barcode")
    fun getBinreclassInputDataDetail(
        transferFrom: String,
        transferTo: String,
        barcode: String,
    ): BinreclassInputData?

    @Query("SELECT * FROM BinreclassInputData WHERE id= :id")
    fun getBinreclassInputDataDetailById(id: Int): BinreclassInputData

    @Query("SELECT * FROM BinreclassInputData WHERE sync_status= :unsycn")
    fun getAllUnsycnBinreclassInput(unsycn: Boolean): List<BinreclassInputData>

    @Query("SELECT * FROM BinreclassInputData WHERE headerId= :id")
    fun getBinreclassInputDataDetailByHeaderId(id: Int): List<BinreclassInputData>

    @Insert(onConflict = OnConflictStrategy.ABORT, entity = BinreclassInputData::class)
    fun insertBinReclassInputData(data: BinreclassInputData)

    @Update(onConflict = OnConflictStrategy.ABORT, entity = BinreclassInputData::class)
    fun updateBinReclassInput(data: BinreclassInputData)

    @Query("DELETE FROM BinreclassInputData where id =:id")
    fun deleteRebinInput(id: Int)

    @Query("SELECT * FROM BinreclassInputData WHERE sync_status= :unsycn AND headerId =:headerId")
    fun getAllUnsycnRebinclassinputByheaderId(
        unsycn: Boolean,
        headerId: Int,
    ): List<BinreclassInputData>

}