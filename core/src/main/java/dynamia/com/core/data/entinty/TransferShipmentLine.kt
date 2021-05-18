package dynamia.com.core.data.entinty

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TransferShipmentLine")
data class TransferShipmentLine(
    @PrimaryKey val id: Int?,
    @ColumnInfo(name = "Document_No") val documentNo: String,
    @ColumnInfo(name = "Line_No") val lineNo: Int,
    @ColumnInfo(name = "No") val no: String,
    @ColumnInfo(name = "Description") val description: String,
    @ColumnInfo(name = "Quantity") val quantity: Int,
    @ColumnInfo(name = "Unit_of_Measure") val unitOfMeasure: String,
    @ColumnInfo(name = "ItemIdentifier") val itemIdentifier: String,
    @ColumnInfo(name = "Transfer_From_Bin_Code") val transferFromBinCode: String,
    @ColumnInfo(name = "Transfer_To_Bin_Code") val transferToBinCode: String
)