package dynamia.com.core.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class PickingListHeader(
    @SerializedName("value")
    val value: List<PickingListHeaderValue>?
)

@Entity(tableName = "PickingListHeader")
data class PickingListHeaderValue(
    @PrimaryKey(autoGenerate = true) var id: Int,
    @SerializedName("Picking_List_No") val pickingListNo: String,
    @SerializedName("SO_No") val sONo: String,
    @SerializedName("Sell_to_Customer_No") val sellToCustomerNo: String,
    @SerializedName("Sell_to_Customer_Name") val sellToCustomerName: String,
    @SerializedName("Salesperson_Code") val salespersonCode: String,
    @SerializedName("Salesperson_Name") val salespersonName: String,
    @SerializedName("Project_Code") val projectCode: String,
    @SerializedName("Requested_Delivery_Date") val requestedDeliveryDate: String,
    @SerializedName("Employee_Code") val employeeCode: String
)