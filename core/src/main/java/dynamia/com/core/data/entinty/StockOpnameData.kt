package dynamia.com.core.data.entinty

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "StockOpnameData")
data class StockOpnameData(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @SerializedName("Item_Code") val itemCode: String,
    @SerializedName("Item_Name") val itemName: String,
    @SerializedName("Barcode") val barcode: String,
    @SerializedName("Doc_No") val docNo: String,
    @SerializedName("Line_No") val lineNo: String,
    @SerializedName("Journal_Template_Name") val journalTemplateName: String,
    @SerializedName("Location_Code") val locationCode: String,
    @SerializedName("Bin_Code") val binCode: String,
    @SerializedName("Already_scaned") var alredyScanned: Int = 0,
)

data class StockOpnameDataAssets(
    @SerializedName("value") val value: List<StockOpnameData>,
)