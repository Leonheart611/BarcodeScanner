package dynamia.com.barcodescanner.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class ReceiptLocalScanEntries(
    @SerializedName("odata.metadata")
    val odataMetadata: String,
    @SerializedName("value")
    val value: List<ReceiptLocalScanEntriesValue>
)

@Entity(tableName = "ReceiptLocalScanEntries")
data class ReceiptLocalScanEntriesValue(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @SerializedName("Description")
    val description: String,
    @SerializedName("Entry_No")
    val entryNo: String,
    @SerializedName("Item_No")
    val itemNo: String,
    @SerializedName("Line_No")
    val lineNo: Int,
    @SerializedName("Mac_Address")
    val macAddress: String,
    @SerializedName("Packing_ID_No")
    val packingIDNo: String,
    @SerializedName("Part_No")
    val partNo: String,
    @SerializedName("Purchase_Order_No")
    val purchaseOrderNo: String,
    @SerializedName("Serial_Number")
    val serialNumber: String,
    @SerializedName("Shipset")
    val shipset: String,
    @SerializedName("Tracking_ID")
    val trackingID: String
)