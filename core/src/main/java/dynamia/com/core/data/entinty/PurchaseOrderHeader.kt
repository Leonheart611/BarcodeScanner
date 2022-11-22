package dynamia.com.core.data.entinty
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "PurchaseOrderHeader")
data class PurchaseOrderHeader(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @SerializedName("Document_Type") val documentType: String,
    @SerializedName("No") val no: String,
    @SerializedName("Buy_from_Vendor_Name") val buyFromVendorName: String,
    @SerializedName("Document_Date") val documentDate: String,
    @SerializedName("Status") val status: String,
    @SerializedName("Vendor_Invoice_No") val vendorInvoiceNo: String?
)

data class PurchaseOrderHeaderAssets(
    @SerializedName("value") val value: List<PurchaseOrderHeader>?,
)