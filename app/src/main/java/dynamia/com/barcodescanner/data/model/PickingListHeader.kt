package dynamia.com.barcodescanner.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class PickingListHeader(
    @SerializedName("odata.metadata")
    val odataMetadata: String?,
    @SerializedName("value")
    val value: List<PickingListHeaderValue?>?
)

@Entity(tableName = "PickingListHeader")
data class PickingListHeaderValue(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @SerializedName("Assigned_Employee")
    val assignedEmployee: String?,
    @SerializedName("Customer_Purchase_Order_No")
    val customerPurchaseOrderNo: String?,
    @SerializedName("Order_Date")
    val orderDate: String?,
    @SerializedName("Picking_List_No")
    val pickingListNo: String?,
    @SerializedName("Posting_Date")
    val postingDate: String?,
    @SerializedName("Project_Code")
    val projectCode: String?,
    @SerializedName("Requested_Delivery_Date")
    val requestedDeliveryDate: String?,
    @SerializedName("SO_No")
    val sONo: String?,
    @SerializedName("Sell_to_Customer_Name")
    val sellToCustomerName: String?,
    @SerializedName("Status")
    val status: String?,
    @SerializedName("Update_from_PDT")
    val updateFromPDT: Boolean?
)