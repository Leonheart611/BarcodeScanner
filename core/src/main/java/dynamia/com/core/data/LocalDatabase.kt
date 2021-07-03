package dynamia.com.core.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dynamia.com.core.data.dao.TransferReceiptDao
import dynamia.com.core.data.dao.TransferShipmentDao
import dynamia.com.core.data.dao.UserDao
import dynamia.com.core.data.entinty.*
import dynamia.com.core.data.model.UserData

@Database(
    entities = [
        TransferShipmentHeader::class,
        TransferShipmentLine::class,
        UserData::class,
        TransferInputData::class,
        TransferReceiptHeader::class,
        TransferReceiptInput::class
    ], version = 8, exportSchema = false
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun transferShipment(): TransferShipmentDao
    abstract fun userDao(): UserDao
    abstract fun transferReceipt(): TransferReceiptDao

    companion object {
        @Volatile
        private var INSTANCE: LocalDatabase? = null

        fun getDatabase(
            context: Context,
        ): LocalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalDatabase::class.java,
                    "MasariDB.sqlite"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}