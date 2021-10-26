package dynamia.com.core.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName="DorPickingDetail")
data class DorPickingDetail(
    @PrimaryKey(autoGenerate = true) var id: Int,
    @SerializedName("Document_No") val documentNo: String,
    @SerializedName("Line_No") val lineNo: Int,
    @SerializedName("Item_No") val itemNo: String,
    @SerializedName("Description") val description: String,
    @SerializedName("Part_No") val partNo: String,
    @SerializedName("Quantity") val quantity: String,
    @SerializedName("Outstanding_Quantity") val outstandingQuantity: String,
    @SerializedName("Status") val status: String,
    @SerializedName("Employe_Code") val employeCode: String,
    @SerializedName("Already_scaned") var alreadyScanned: Int = 0
)

data class DorPickingDetailAsset(
    @SerializedName("value") val value: List<DorPickingDetail>
)