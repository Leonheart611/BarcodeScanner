package dynamia.com.core.data.entinty

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "InventoryPickHeader")
data class InventoryPickHeader(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @SerializedName("Destination_No") val destinationNo: String,
    @SerializedName("Location_Code") val locationCode: String,
    @SerializedName("No") val no: String,
    @SerializedName("Posting_Date") val postingDate: String,
    @SerializedName("Source_Document") val sourceDocument: String,
    @SerializedName("Source_No") val sourceNo: String,
    @SerializedName("Transfer_to_Code") val transferToCode: String,
    @SerializedName("Type") val type: String
)

data class InventoryPickHeaderAssets(
    @SerializedName("value") val value: List<InventoryPickHeader>?,
)