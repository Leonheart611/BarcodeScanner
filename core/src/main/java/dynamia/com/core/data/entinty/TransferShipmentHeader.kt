package dynamia.com.core.data.entinty

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "TransferShipmentHeader")
data class TransferShipmentHeader(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @SerializedName("No") val no: String,
    @SerializedName("Status") val status: String,
    @SerializedName("Posting_Date") val postingDate: String,
    @SerializedName("Transfer_from_Code") val transferFromCode: String,
    @SerializedName("Transfer_to_Code") val transferToCode: String
)

data class TransferShipmentHeaderAsset(
    @SerializedName("value") val value: List<TransferShipmentHeader>?
)
