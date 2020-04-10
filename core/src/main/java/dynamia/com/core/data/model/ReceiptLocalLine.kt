package dynamia.com.core.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class ReceiptLocalLine(
    @SerializedName("value")
    val value: List<ReceiptLocalLineValue>
)

@Entity(tableName = "ReceiptLocalLine")
data class ReceiptLocalLineValue(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @SerializedName("Document_Type")
    val documentType: String,
    @SerializedName("Document_No")
    val documentNo: String,
    @SerializedName("Line_No")
    val lineNo: Int,
    @SerializedName("Type")
    val type: String,
    @SerializedName("No")
    val no: String,
    @SerializedName("Description")
    val description: String,
    @SerializedName("Quantity")
    val quantity: String,
    @SerializedName("Outstanding_Quantity")
    val outstandingQuantity: String
)