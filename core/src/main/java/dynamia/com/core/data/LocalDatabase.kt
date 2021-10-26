package dynamia.com.core.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dynamia.com.core.data.dao.*
import dynamia.com.core.data.model.*

@Database(
    entities = [PickingListHeaderValue::class,
        PickingListLineValue::class,
        PickingListScanEntriesValue::class,
        ReceiptImportHeaderValue::class,
        ReceiptImportLineValue::class,
        ReceiptImportScanEntriesValue::class,
        ReceiptLocalHeaderValue::class,
        ReceiptLocalLineValue::class,
        ReceiptLocalScanEntriesValue::class,
        StockCount::class,
        UserData::class,
        PeminjamanHeader::class,
        PeminjamanDetail::class,
        PeminjamScanEntries::class,
        DorPickingHeader::class,
        DorPickingDetail::class,
        DorPickingScanEntries::class
    ], version = 20, exportSchema = false
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun pickingListDao(): PickingListDao
    abstract fun receiptImportDao(): ReceiptImportDao
    abstract fun receiptLocalHeaderDao(): ReceiptLocalDao
    abstract fun stockCountDao(): StockCountDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: LocalDatabase? = null

        fun getDatabase(
            context: Context
        ): LocalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalDatabase::class.java,
                    "barcodeDB.sqlite"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }

        fun clearAllTable() {
            INSTANCE?.clearAllTables()
        }

    }


}