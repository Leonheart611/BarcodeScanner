package dynamia.com.core.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


data class PickingListScanEntries(
    @SerializedName("value")
    val value: List<PickingListScanEntriesValue>
)

@Parcelize
@Entity(tableName = "PickingListScanEntries")
data class PickingListScanEntriesValue(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @SerializedName("Document_No") @Expose(serialize = true) val documentNo: String,
    @SerializedName("Line_No") @Expose(serialize = true) val lineNo: Int,
    @SerializedName("Part_No") @Expose(serialize = true) val partNo: String,
    @SerializedName("Qty_Scan") @Expose(serialize = true) val qtyScan: String,
    @SerializedName("Serial_No") @Expose(serialize = true) val serialNo: String,
    @SerializedName("Mac_Address") @Expose(serialize = true) val macAddress: String,
    @SerializedName("Note") @Expose(serialize = true) val note: String,
    @SerializedName("Employee_Code") @Expose(serialize = true) val employeeCode: String,
    @SerializedName("Date") @Expose(serialize = true) val date: String,
    @SerializedName("Time") @Expose(serialize = true) val time: String,
    @Expose(serialize = false, deserialize = false) var sycn_status: Boolean = false,
    @Expose(serialize = false, deserialize = false) var pickingListNo: String
) : Parcelable