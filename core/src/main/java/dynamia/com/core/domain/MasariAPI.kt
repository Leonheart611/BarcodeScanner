package dynamia.com.core.domain

import com.google.gson.annotations.SerializedName
import dynamia.com.core.data.entinty.TransferShipmentHeader
import dynamia.com.core.data.entinty.TransferShipmentLine
import retrofit2.Response
import retrofit2.http.GET

interface MasariAPI {
    /**
     * TransferShipment
     */
    @GET("/Android_TransferShipmentHeader")
    suspend fun getTransferShipmentHeader(): Response<BaseResponse<TransferShipmentHeader>>

    @GET("/Android_TransferLine")
    suspend fun getTransferShipmentLine(): Response<BaseResponse<TransferShipmentLine>>
}

data class BaseResponse<T>(
    @SerializedName("value") val value: List<T>
)