package dynamia.com.barcodescanner.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class ReceiptLocalLine(
    @SerializedName("odata.metadata")
    val odataMetadata: String,
    @SerializedName("value")
    val value: List<ReceiptLocalLineValue>
)

@Entity(tableName = "ReceiptLocalLine")
data class ReceiptLocalLineValue(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @SerializedName("Buy_from_Vendor_No")
    val buyFromVendorNo: String,
    @SerializedName("Description")
    val description: String,
    @SerializedName("Document_No")
    val documentNo: String,
    @SerializedName("Document_Type")
    val documentType: String,
    @SerializedName("ETag")
    val eTag: String,
    @SerializedName("Line_No")
    val lineNo: Int,
    @SerializedName("Location_Code")
    val locationCode: String,
    @SerializedName("No")
    val no: String,
    @SerializedName("Outstanding_Quantity")
    val outstandingQuantity: String,
    @SerializedName("Project_Code")
    val projectCode: String,
    @SerializedName("Qty_to_Receive")
    val qtyToReceive: String,
    @SerializedName("Quantity")
    val quantity: String,
    @SerializedName("Quantity_Received")
    val quantityReceived: String,
    @SerializedName("Type")
    val type: String,
    @SerializedName("Unit_of_Measure")
    val unitOfMeasure: String
)