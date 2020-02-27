package dynamia.com.barcodescanner.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class ReceiptImportHeader(
    @SerializedName("odata.metadata")
    val odataMetadata: String,
    @SerializedName("value")
    val value: List<ReceiptImportHeaderValue>
)

@Entity(tableName = "ReceiptImportHeader")
data class ReceiptImportHeaderValue(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @SerializedName("Assigned_Employee_ID")
    val assignedEmployeeID: String,
    @SerializedName("ETag")
    val eTag: String,
    @SerializedName("No")
    val no: String,
    @SerializedName("Posting_Date")
    val postingDate: String,
    @SerializedName("Project_Code")
    val projectCode: String,
    @SerializedName("Purchase_Order_No")
    val purchaseOrderNo: String,
    @SerializedName("Scan_from_PDT")
    val scanFromPDT: String,
    @SerializedName("Status")
    val status: String,
    @SerializedName("Transfer_from_Code")
    val transferFromCode: String,
    @SerializedName("Transfer_to_Code")
    val transferToCode: String,
    @SerializedName("Vendor_No")
    val vendorNo: String,
    @SerializedName("Vendor_Shipment_No")
    val vendorShipmentNo: String
)