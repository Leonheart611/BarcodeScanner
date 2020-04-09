package dynamia.com.core.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class ReceiptImportHeader(
    @SerializedName("value")
    val value: List<ReceiptImportHeaderValue>
)

@Entity(tableName = "ReceiptImportHeader")
data class ReceiptImportHeaderValue(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @SerializedName("No")
    val no: String,
    @SerializedName("Purchase_Order_No")
    val purchaseOrderNo: String,
    @SerializedName("Buy_from_Vendor_Name")
    val buyFromVendorName: String,
    @SerializedName("Employee_Code")
    val employeeCode: String,
    @SerializedName("Posting_Date")
    val postingDate: String
)