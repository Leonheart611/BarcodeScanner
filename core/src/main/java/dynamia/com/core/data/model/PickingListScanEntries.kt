package dynamia.com.core.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class PickingListScanEntries(
    @SerializedName("value")
    val value: List<PickingListScanEntriesValue>
)

@Entity(tableName = "PickingListScanEntries")
data class PickingListScanEntriesValue(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @SerializedName("Document_No") val documentNo: String,
    @SerializedName("Line_No") val lineNo: Int,
    @SerializedName("Part_No") val partNo: String,
    @SerializedName("Qty_Scan") val qtyScan: String,
    @SerializedName("Serial_No") val serialNo: String,
    @SerializedName("Mac_Address") val macAddress: String,
    @SerializedName("Note") val note: String,
    @SerializedName("Employee_Code") val employeeCode: String,
    @SerializedName("Date") val date: String,
    @SerializedName("Time") val time: String,
    var sycn_status:Boolean = false
)