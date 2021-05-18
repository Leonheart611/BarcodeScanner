package dynamia.com.core.data.entinty

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TransferShipmentHeader")
data class TransferShipmentHeader(
    @PrimaryKey val id: Int?,
    @ColumnInfo(name = "No") val no: String,
    @ColumnInfo(name = "Status") val status: String,
    @ColumnInfo(name = "Posting_Date") val postingDate: String,
    @ColumnInfo(name = "Transfer_from_Code") val transferFromCode: String,
    @ColumnInfo(name = "Transfer_to_Code") val transferToCode: String
)
