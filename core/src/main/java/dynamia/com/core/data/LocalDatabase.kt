package dynamia.com.core.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dynamia.com.core.data.dao.*
import dynamia.com.core.data.entinty.*

@Database(
    entities = [
        TransferShipmentHeader::class,
        TransferShipmentLine::class,
        UserData::class,
        TransferInputData::class,
        TransferReceiptHeader::class,
        TransferReceiptInput::class,
        PurchaseOrderHeader::class,
        PurchaseOrderLine::class,
        PurchaseInputData::class,
        StockOpnameData::class,
        StockOpnameInputData::class,
        BinreclassHeader::class,
        BinreclassInputData::class,
        InventoryPickHeader::class,
        InventoryPickLine::class,
        InventoryInputData::class
    ], version = 25, exportSchema = false
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun transferShipment(): TransferShipmentDao
    abstract fun userDao(): UserDao
    abstract fun transferReceipt(): TransferReceiptDao
    abstract fun purchaseOrder(): PurchaseOrderDao
    abstract fun stockOpnameDao(): StockOpnameDao
    abstract fun binreclassDao(): BinreclassDao
    abstract fun inventoryDao(): InventoryDao
}