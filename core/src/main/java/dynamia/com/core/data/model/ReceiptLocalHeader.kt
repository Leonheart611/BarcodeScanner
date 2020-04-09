package dynamia.com.core.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class ReceiptLocalHeader(
    @SerializedName("value")
    val value: List<ReceiptLocalHeaderValue>
)

@Entity(tableName = "ReceiptLocalHeader")
data class ReceiptLocalHeaderValue(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @SerializedName("Document_Type")
    val documentType: String,
    @SerializedName("No")
    val no: String,
    @SerializedName("Buy_from_Vendor_No")
    val buyFromVendorNo: String,
    @SerializedName("Buy_from_Vendor_Name")
    val buyFromVendorName: String,
    @SerializedName("Employee_Code")
    val employeeCode: String,
    @SerializedName("Expected_Receipt_Date")
    val expectedReceiptDate: String
)