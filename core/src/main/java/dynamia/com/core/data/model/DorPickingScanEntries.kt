package dynamia.com.core.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "DorPickingScanEntries")
data class DorPickingScanEntries(
	@PrimaryKey(autoGenerate = true) var id: Int? = null,
	@SerializedName("Document_No") @Expose(serialize = true) val documentNo: String,
	@SerializedName("Line_No") @Expose(serialize = true) val lineNo: Int,
	@SerializedName("Part_No") @Expose(serialize = true) val partNo: String,
	@SerializedName("Packing_ID") @Expose(serialize = true) val packingID: String,
	@SerializedName("PO_No") @Expose(serialize = true) val pONo: String,
	@SerializedName("Serial_No") @Expose(serialize = true) val serialNo: String,
	@SerializedName("Shipset") @Expose(serialize = true) val shipset: String,
	@SerializedName("Tracking_ID") @Expose(serialize = true) val trackingID: String,
	@SerializedName("Mac_Address") @Expose(serialize = true) val macAddress: String,
	@SerializedName("Employee_Code") @Expose(serialize = true) val employeeCode: String,
	@SerializedName("Exclude") @Expose(serialize = true) val Exclude: Boolean = false,
	@SerializedName("Date") @Expose(serialize = true) val date: String,
	@SerializedName("Time") @Expose(serialize = true) val time: String,
	@Expose(serialize = false, deserialize = false) var sycn_status: Boolean = false
) {
	fun successSync() {
		sycn_status = true
	}
}