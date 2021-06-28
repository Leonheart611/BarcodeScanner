package dynamia.com.core.data.entinty

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "TransferShipmentLine")
data class TransferShipmentLine(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @SerializedName("Document_No") val documentNo: String,
    @SerializedName("Line_No") val lineNo: Int,
    @SerializedName("No") val no: String,
    @SerializedName("Description") val description: String,
    @SerializedName("Quantity") val quantity: Int,
    @SerializedName("Unit_of_Measure") val unitOfMeasure: String,
    @SerializedName("ItemIdentifier") val itemIdentifier: String,
    @SerializedName("Qty_in_Transit") val qtyInTransit: Int? = 0,
    @SerializedName("Transfer_From_Bin_Code") val transferFromBinCode: String,
    @SerializedName("Transfer_To_Bin_Code") val transferToBinCode: String,
    @SerializedName("Already_scaned") var alredyScanned: Int = 0,
)

data class TransferShipmentLineAsset(
    @SerializedName("value") val value: List<TransferShipmentLine>?,
)