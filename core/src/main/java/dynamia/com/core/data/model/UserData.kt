package dynamia.com.core.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "user")
data class UserData(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @SerializedName("host_name")  val hostName: String,
    @SerializedName("username") val username:String,
    @SerializedName("password")  val password: String,
    @SerializedName("Employee_COde")val employeeCode:String

)