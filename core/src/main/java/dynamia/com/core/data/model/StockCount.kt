package dynamia.com.core.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "StockCount")
data class StockCount(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @SerializedName("Part_No") @Expose(serialize = true) val Part_No: String,
    @SerializedName("Serial_No") @Expose(serialize = true) val Serial_No: String,
    @SerializedName("Employee_COde")@Expose(serialize = true) val Employee_COde:String,
    @SerializedName("Item_No")@Expose(serialize = true) val Item_No:String,
    @SerializedName("Date") @Expose(serialize = true) val date: String,
    @SerializedName("Time") @Expose(serialize = true) val time: String,
    @Expose(serialize = false,deserialize = false)var sycn_status:Boolean = false
)