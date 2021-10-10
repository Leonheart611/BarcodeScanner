package dynamia.com.core.data.entinty

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "PurchaseOrderLine")
data class PurchaseOrderLine(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @SerializedName("Document_Type") val documentType: String,
    @SerializedName("Document_No") val documentNo: String,
    @SerializedName("Line_No") val lineNo: Int,
    @SerializedName("Item_Ref_No") val itemRefNo: String = "",
    @SerializedName("No") val no: String,
    @SerializedName("Description") val description: String,
    @SerializedName("Quantity") val quantity: Int,
    @SerializedName("Unit_of_Measure") val unitOfMeasure: String,
    @SerializedName("ItemIdentifier") val itemIdentifier: String,
    @SerializedName("Already_scaned") var alredyScanned: Int = 0,
)

data class PurchaseOrderLineAsset(
    @SerializedName("value") val value: List<PurchaseOrderLine>?,
)