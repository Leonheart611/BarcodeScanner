package dynamia.com.core.domain

import com.google.gson.annotations.SerializedName
import dynamia.com.core.data.entinty.TransferInputData
import dynamia.com.core.data.entinty.TransferShipmentHeader
import dynamia.com.core.data.entinty.TransferShipmentLine
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface MasariAPI {
    /**
     * TransferShipment
     */
    @GET("Android_TransferShipmentHeader")
    suspend fun getTransferShipmentHeader(): Response<BaseResponse<TransferShipmentHeader>>

    @GET("Android_TransferLine")
    suspend fun getTransferShipmentLine(): Response<BaseResponse<TransferShipmentLine>>

    @Headers("Content-Type: application/json")
    @POST("Android_Transaction")
    suspend fun postTransferData(@Body value: String): TransferInputData
}

data class BaseResponse<T>(
    @SerializedName("value") val value: List<T>?
)