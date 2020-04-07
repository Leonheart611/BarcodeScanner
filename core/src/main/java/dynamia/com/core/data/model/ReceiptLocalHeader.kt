package dynamia.com.core.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class ReceiptLocalHeader(
    @SerializedName("value")
    val value: List<ReceiptLocalHeaderValue>
)

/*@Entity(tableName = "ReceiptLocalHeader")
data class ReceiptLocalHeaderValue(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @SerializedName("Assigned_Employee_ID")
    val assignedEmployeeID: String,
    @SerializedName("Buy_from_Vendor_Name")
    val buyFromVendorName: String,
    @SerializedName("Document_Type")
    val documentType: String,
    @SerializedName("Expected_Receipt_Date")
    val expectedReceiptDate: String,
    @SerializedName("Location_Code")
    val locationCode: String,
    @SerializedName("No")
    val no: String,
    @SerializedName("Order_Date")
    val orderDate: String,
    @SerializedName("Project_Code")
    val projectCode: String,
    @SerializedName("Status")
    val status: String,
    @SerializedName("UpdateFromPDT")
    val updateFromPDT: Boolean
)*/

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
    val employeeCode: String
)