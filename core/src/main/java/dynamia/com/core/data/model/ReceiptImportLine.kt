package dynamia.com.core.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class ReceiptImportLine(
    @SerializedName("value")
    val value: List<ReceiptImportLineValue>
)

/*@Entity(tableName = "ReceiptImportLine")
data class ReceiptImportLineValue(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @SerializedName("Description")
    val description: String,
    @SerializedName("Description_2")
    val description2: String,
    @SerializedName("Document_No")
    val documentNo: String,
    @SerializedName("Item_No")
    val itemNo: String,
    @SerializedName("Line_No")
    val lineNo: Int,
    @SerializedName("Purchase_Order_No")
    val purchaseOrderNo: String,
    @SerializedName("Qty_to_Receive")
    val qtyToReceive: String,
    @SerializedName("Qty_to_Ship")
    val qtyToShip: String,
    @SerializedName("Quantity")
    val quantity: String,
    @SerializedName("Quantity_Received")
    val quantityReceived: String,
    @SerializedName("Quantity_Shipped")
    val quantityShipped: String,
    @SerializedName("Unit_of_Measure")
    val unitOfMeasure: String
)*/

@Entity(tableName = "ReceiptImportLine")
data class ReceiptImportLineValue(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @SerializedName("Document_No")
    val documentNo: String,
    @SerializedName("Line_No")
    val lineNo: Int,
    @SerializedName("Item_No")
    val itemNo: String,
    @SerializedName("Description")
    val description: String,
    @SerializedName("Quantity")
    val quantity: String,
    @SerializedName("Outstanding_Quantity")
    val outstandingQuantity: String,
    @SerializedName("Status")
    val status: String,
    @SerializedName("Employee_Code")
    val employeeCode: String
)