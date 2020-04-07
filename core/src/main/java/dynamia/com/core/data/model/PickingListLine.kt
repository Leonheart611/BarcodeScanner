package dynamia.com.core.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class PickingListLine(
    @SerializedName("value")
    val value: List<PickingListLineValue>
)

@Entity(tableName = "PickingListLine")
data class PickingListLineValue( //TODO: Add Local Count
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @SerializedName("Picking_List_No")
    val pickingListNo: String,
    @SerializedName("Document_No")
    val documentNo: String,
    @SerializedName("Line_No")
    val lineNo: Int,
    @SerializedName("Shipping_Status")
    val shippingStatus: String,
    @SerializedName("Type")
    val type: String,
    @SerializedName("No")
    val no: String,
    @SerializedName("Description")
    val description: String,
    @SerializedName("Part_No_Original")
    val partNoOriginal: String,
    @SerializedName("Qty_to_Ship")
    val qtyToShip: String,
    @SerializedName("Outstanding_Quantity")
    val outstandingQuantity: String
)