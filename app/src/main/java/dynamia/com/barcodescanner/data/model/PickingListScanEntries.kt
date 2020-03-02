package dynamia.com.barcodescanner.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class PickingListScanEntries(
    @SerializedName("odata.metadata")
    val odataMetadata: String,
    @SerializedName("value")
    val value: List<PickingListScanEntriesValue>
)

@Entity(tableName = "PickingListScanEntries")
data class PickingListScanEntriesValue(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @SerializedName("Description")
    val description: String,
    @SerializedName("Document_No")
    val documentNo: String,
    @SerializedName("Entry_No")
    val entryNo: String,
    @SerializedName("Item_No")
    val itemNo: String,
    @SerializedName("Line_No")
    val lineNo: Int,
    @SerializedName("Mac_Address")
    val macAddress: String,
    @SerializedName("Note")
    val note: String,
    @SerializedName("Part_No")
    val partNo: String,
    @SerializedName("Picking_List_No")
    val pickingListNo: String,
    @SerializedName("Quantity")
    val quantity: String,
    @SerializedName("Serial_Number")
    val serialNumber: String
)