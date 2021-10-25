package dynamia.com.core.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "DorPickingHeader")
data class DorPickingHeader(
    @PrimaryKey(autoGenerate = true) var id: Int,
    @SerializedName("Employee_Code") val employeeCode: String,
    @SerializedName("No") val no: String,
    @SerializedName("Project_Code") val projectCode: String,
    @SerializedName("Req_Delivery_Date") val reqDeliveryDate: String,
    @SerializedName("Sales_Rent_Doc_No") val salesRentDocNo: String,
    @SerializedName("Salesperson_Code") val salespersonCode: String,
    @SerializedName("Status") val status: String,
    @SerializedName("Transfer_from_Name") val transferFromName: String,
    @SerializedName("Transfer_to_Name") val transferToName: String
)