package dynamia.com.core.data.entinty

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "user")
data class UserData(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @SerializedName("host_name") val hostName: String,
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("companyName") val companyName: String,
    @SerializedName("domainName") val domainName: String
)
