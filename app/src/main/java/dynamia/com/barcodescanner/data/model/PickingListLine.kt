package dynamia.com.barcodescanner.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class PickingListLine(
    @SerializedName("odata.metadata")
    val odataMetadata: String,
    @SerializedName("value")
    val value: List<PickingListLineValue>
)

@Entity(tableName = "PickingListLine")
data class PickingListLineValue(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @SerializedName("Description")
    val description: String,
    @SerializedName("Description_2")
    val description2: String,
    @SerializedName("Document_No")
    val documentNo: String,
    @SerializedName("Line_No")
    val lineNo: Int,
    @SerializedName("No")
    val no: String,
    @SerializedName("Outstanding_Quantity")
    val outstandingQuantity: String,
    @SerializedName("Picking_List_No")
    val pickingListNo: String,
    @SerializedName("Project_Code")
    val projectCode: String,
    @SerializedName("Purch_Order_No")
    val purchOrderNo: String,
    @SerializedName("Qty_to_Ship")
    val qtyToShip: String,
    @SerializedName("Quantity")
    val quantity: String,
    @SerializedName("Quantity_Shipped")
    val quantityShipped: String,
    @SerializedName("Type")
    val type: String,
    @SerializedName("Unit_of_Measure")
    val unitOfMeasure: String
)