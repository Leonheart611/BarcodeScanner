package dynamia.com.core.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "PeminjamanHeader")
data class PeminjamanHeader(
    @PrimaryKey(autoGenerate = true) var id: Int,
    @SerializedName("Employee_Code") val employeeCode: String,
    @SerializedName("No") val no: String,
    @SerializedName("Posting_Date") val postingDate: String,
    @SerializedName("Project_Code") val projectCode: String,
    @SerializedName("Status") val status: String,
    @SerializedName("Transfer_from_Name") val transferFromName: String,
    @SerializedName("Transfer_to_Name") val transferToName: String
)

data class PeminjamanHeaderAsset(
    @SerializedName("value") val value: List<PeminjamanHeader>
)