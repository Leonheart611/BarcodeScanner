package dynamia.com.core.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dynamia.com.core.data.model.PeminjamScanEntries
import dynamia.com.core.data.model.PeminjamanDetail
import dynamia.com.core.data.model.PeminjamanHeader

@Dao
interface PeminjamDao {
    /**
     * Peminjam Header Dao
     */
    @Insert(entity = PeminjamanHeader::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertAllPeminjam(data: List<PeminjamanHeader>)

    @Query("SELECT * FROM PeminjamanHeader")
    fun getAllPeminjamHeader(): LiveData<List<PeminjamanHeader>>

    @Query("SELECT * FROM PeminjamanHeader WHERE id =:id")
    fun getPeminjamHeaderDetail(id: Int): PeminjamanHeader

    @Query("DELETE FROM PeminjamanHeader")
    fun deleteAllPeminjamHeader()

    /**
     *Peminjam Detail Dao
     */

    @Insert(entity = PeminjamanDetail::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertAllPeminjamDetail(data: List<PeminjamanDetail>)

    @Query("SELECT * FROM PeminjamanDetail WHERE documentNo =:documentNo")
    fun getPeminjamanDetailData(documentNo: String): LiveData<List<PeminjamanDetail>>

    @Update(entity = PeminjamanDetail::class, onConflict = OnConflictStrategy.ABORT)
    fun updatePeminjamDetailData(data: PeminjamanDetail)

    @Query("DELETE FROM PeminjamanDetail")
    fun clearPeminjamDetailData()

    /**
     * Peminjam Scan Entries Dao
     */

    @Insert(entity = PeminjamScanEntries::class, onConflict = OnConflictStrategy.ABORT)
    fun insertPeminjamScanEntires(data: PeminjamScanEntries)

    @Update(entity = PeminjamScanEntries::class, onConflict = OnConflictStrategy.ABORT)
    fun updatePeminjamScanEntries(data: PeminjamScanEntries)

    @Delete(entity = PeminjamScanEntries::class)
    fun deletePeminjamScanEntries(data: PeminjamScanEntries)

    @Query("DELETE FROM PeminjamanHeader")
    fun deleteAllPeminjamScanEntries()
}