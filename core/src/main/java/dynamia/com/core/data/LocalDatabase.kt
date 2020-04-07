package dynamia.com.core.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dynamia.com.core.data.dao.PickingListDao
import dynamia.com.core.data.dao.ReceiptImportDao
import dynamia.com.core.data.dao.ReceiptLocalDao
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
        ReceiptLocalScanEntriesValue::class
    ], version = 4, exportSchema = false
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun pickingListDao(): PickingListDao
    abstract fun receiptImportDao(): ReceiptImportDao
    abstract fun receiptLocalHeaderDao(): ReceiptLocalDao
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
                ).allowMainThreadQueries().fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun clearAllTable() {
            INSTANCE?.clearAllTables()
        }

    }


}