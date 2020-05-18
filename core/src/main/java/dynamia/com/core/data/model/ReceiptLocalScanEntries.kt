package dynamia.com.core.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class ReceiptLocalScanEntries(
    @SerializedName("value")
    val value: List<ReceiptLocalScanEntriesValue>
)

@Entity(tableName = "ReceiptLocalScanEntries")
data class ReceiptLocalScanEntriesValue(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @SerializedName("Document_No") val documentNo: String,
    @SerializedName("Line_No") val lineNo: Int,
    @SerializedName("Part_No") val partNo: String,
    @SerializedName("Packing_ID") val packingID: String,
    @SerializedName("PO_No") val pONo: String,
    @SerializedName("Serial_No") val serialNo: String,
    @SerializedName("Shipset") val shipset: String,
    @SerializedName("Tracking_ID") val trackingID: String,
    @SerializedName("Mac_Address") val macAddress: String,
    @SerializedName("Employee_Code") val employeeCode: String,
    @SerializedName("Date") val date: String,
    @SerializedName("Time") val time: String,
    var sycn_status:Boolean = false
)
