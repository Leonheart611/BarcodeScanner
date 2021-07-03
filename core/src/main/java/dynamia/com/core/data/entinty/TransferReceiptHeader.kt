package dynamia.com.core.data.entinty

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "TransferReceiptHeader")
data class TransferReceiptHeader(
    @SerializedName("No") val no: String,
    @SerializedName("Status") val status: String,
    @SerializedName("Posting_Date") val postingDate: String,
    @SerializedName("Transfer_from_Code") val transferFromCode: String,
    @SerializedName("Transfer_to_Code") val transferToCode: String,
)