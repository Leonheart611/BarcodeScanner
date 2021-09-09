package dynamia.com.core.data.entinty

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "StockOpnameData")
data class StockOpnameData(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @SerializedName("Journal_Template_Name") val journalTemplateName: String,
    @SerializedName("Journal_Batch_Name") val journalBatchName: String,
    @SerializedName("Line_No") val lineNo: Int,
    @SerializedName("Document_No") val documentNo: String,
    @SerializedName("Item_No") val itemNo: String,
    @SerializedName("Description") val description: String,
    @SerializedName("Qty_Calculated") val qtyCalculated: Int,
    @SerializedName("ItemIdentifier") val itemIdentifier: String,
    @SerializedName("Location_Code") val locationCode: String,
    @SerializedName("Bin_Code") val binCode: String,
    @SerializedName("Already_scaned") var alredyScanned: Int = 0,
)

data class StockOpnameDataAssets(
    @SerializedName("value") val value: List<StockOpnameData>,
)


data class StockCheckingData(
    @SerializedName("Location_Code") val locationCode: String,
    @SerializedName("Item_No") val itemNo: String,
    @SerializedName("Variant_Code") val variantCode: String,
    @SerializedName("Item_Identifiers") val itemIdentifiers: String,
    @SerializedName("Replenishment_System") val replenishmentSystem: String,
    @SerializedName("Description") val description: String,
    @SerializedName("Inventory") val inventory: Int,
    @SerializedName("Reorder_Point") val reorderPoint: Int,
    @SerializedName("Reorder_Quantity") val reorderQuantity: Int,
    @SerializedName("Maximum_Inventory") val maximumInventory: Int,
    @SerializedName("Assembly_Policy") val assemblyPolicy: String,
    @SerializedName("Global_Dimension_1_Filter") val globalDimension1Filter: String,
    @SerializedName("Global_Dimension_2_Filter") val globalDimension2Filter: String,
    @SerializedName("Drop_Shipment_Filter") val dropShipmentFilter: String
)

data class StockCheckDataAssets(
    @SerializedName("value") val value: List<StockCheckingData>,
)
