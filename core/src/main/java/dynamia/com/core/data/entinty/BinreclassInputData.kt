package dynamia.com.core.data.entinty

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "BinreclassInputData")
data class BinreclassInputData(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @SerializedName("Table_ID") @Expose(serialize = true) val tableID: Int = 5,
    @SerializedName("Document_No") @Expose(serialize = true) val documentNo: String,
    @SerializedName("Line_No") @Expose(serialize = true) val lineNo: Int,
    @SerializedName("Item_No") @Expose(serialize = true) val itemNo: String = "",
    @SerializedName("Quantity") @Expose(serialize = true) var quantity: Int,
    @SerializedName("Location_Code") @Expose(serialize = true) val locationCode: String = "",
    @SerializedName("Location_from_Code") @Expose(serialize = true) val locationFromCode: String = "",
    @SerializedName("Location_to_Code") @Expose(serialize = true) val locationToCode: String = "",
    @SerializedName("Transfer_from_Bin_Code") @Expose(serialize = true) var transferFromBinCode: String = "",
    @SerializedName("Transfer_to_Bin_Code") @Expose(serialize = true) var transferToBinCode: String = "",
    @SerializedName("Bin_Code") @Expose(serialize = true) var binCode: String = "",
    @SerializedName("New_Bin_Code") @Expose(serialize = true) var newBinCode: String = "",
    @SerializedName("Sync_to_BC") @Expose(serialize = true) val syncToBC: Int = 2,
    @SerializedName("User_Name") @Expose(serialize = true) val userName: String,
    @SerializedName("Insert_DateTime") @Expose(serialize = true) val insertDateTime: String,
    @SerializedName("Item_Identifier") @Expose(serialize = true) val itemIdentifier: String = "",
    @SerializedName("Box") @Expose(serialize = true) val box: String = "",
    @Expose(serialize = false, deserialize = false) val headerId: Int,
    @Expose(serialize = false, deserialize = false) var sync_status: Boolean = false,
) {
    fun postSuccess() {
        sync_status = true
    }
}



