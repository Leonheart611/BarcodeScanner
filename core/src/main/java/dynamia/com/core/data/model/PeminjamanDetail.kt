package dynamia.com.core.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "PeminjamanDetail")
data class PeminjamanDetail(
    @PrimaryKey(autoGenerate = true) var id: Int,
    @SerializedName("Description") val description: String,
    @SerializedName("Document_No") val documentNo: String,
    @SerializedName("Employe_Code") val employeCode: String,
    @SerializedName("Item_No") val itemNo: String,
    @SerializedName("Line_No") val lineNo: Int,
    @SerializedName("Outstanding_Quantity") val outstandingQuantity: String,
    @SerializedName("Part_No") val partNo: String,
    @SerializedName("Quantity") val quantity: String,
    @SerializedName("Status") val status: String,
    @SerializedName("Already_scaned") var alreadyScanned: Int = 0
)

data class PeminjamanDetailAsset(
    @SerializedName("value") val value: List<PeminjamanDetail>
)