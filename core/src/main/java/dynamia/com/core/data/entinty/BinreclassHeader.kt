package dynamia.com.core.data.entinty

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "BinreclassHeader")
data class BinreclassHeader(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @SerializedName("Document_No") val documentNo: String,
    @SerializedName("Transfer_from_Bin_Code") var transferFromBinCode: String,
    @SerializedName("Transfer_to_Bin_Code") var transferToBinCode: String,
    @SerializedName("Date") val date: String,
    @Expose(serialize = false, deserialize = false) var sync_status: Boolean = false,
) {
    fun allDataPosted() {
        sync_status = true
    }
}
