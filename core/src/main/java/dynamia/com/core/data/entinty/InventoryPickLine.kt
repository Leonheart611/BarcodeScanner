package dynamia.com.core.data.entinty

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "InventoryPickLine")
data class InventoryPickLine(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @SerializedName("Activity_Type") val activityType: String,
    @SerializedName("Bin_Code") val binCode: String,
    @SerializedName("Description") val description: String,
    @SerializedName("Item_No") val itemNo: String,
    @SerializedName("Item_Ref_No") val itemRefNo: String,
    @SerializedName("Line_No") val lineNo: Int,
    @SerializedName("No") val no: String,
    @SerializedName("Qty_Handled") val qtyHandled: Int,
    @SerializedName("Qty_Outstanding") val qtyOutstanding: Int,
    @SerializedName("Qty_to_Handle") val qtyToHandle: Int,
    @SerializedName("Quantity") val quantity: Int,
    @SerializedName("Unit_of_Measure_Code") val unitOfMeasureCode: String,
    @SerializedName("ItemIdentifier") val itemIdentifier: String,
    @SerializedName("Already_scaned") var alredyScanned: Int = 0
)

data class InventoryPickLineAsset(
    @SerializedName("value") val value: List<InventoryPickLine>?,
)