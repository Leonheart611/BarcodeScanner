package dynamia.com.core.domain

import com.google.gson.annotations.SerializedName
import dynamia.com.core.data.entinty.*
import retrofit2.Response
import retrofit2.http.*

interface MasariAPI {
    /**
     * TransferShipment
     */
    @GET("Android_TransferShipmentHeader")
    suspend fun getTransferShipmentHeader(): Response<BaseResponse<TransferShipmentHeader>>

    @GET("Android_TransferReceiptHeader")
    suspend fun getTransferReceiptHeader(): Response<BaseResponse<TransferReceiptHeader>>

    @GET("Android_TransferLine")
    suspend fun getTransferShipmentLine(): Response<BaseResponse<TransferShipmentLine>>

    @GET("Android_PurchaseOrderHeader")
    suspend fun getPurchaseOrderHeader(): Response<BaseResponse<PurchaseOrderHeader>>

    @GET("Android_PurchaseOrderLine")
    suspend fun getPurchaseOrderLine(): Response<BaseResponse<PurchaseOrderLine>>

    @GET("Android_StockCounting")
    suspend fun getStockOpname(@Query("\$filter") filter: String = "Journal_Template_Name eq 'PHYS. INVE'"): Response<BaseResponse<StockOpnameData>>

    @GET("Android_StockCheck")
    suspend fun getCheckStock(
        @Query("\$filter") filter: String,
        @Query("\$orderby") order: String = "Inventory desc"
    ): Response<BaseResponse<StockCheckingData>>

    @Headers("Content-Type: application/json")
    @POST("Android_Transaction")
    suspend fun postTransferShipment(@Body value: String): TransferInputData

    @Headers("Content-Type: application/json")
    @POST("Android_Transaction")
    suspend fun postTransferReceipt(@Body value: String): TransferReceiptInput

    @Headers("Content-Type: application/json")
    @POST("Android_Transaction")
    suspend fun postPurchaseOrderData(@Body value: String): PurchaseInputData

    @Headers("Content-Type: application/json")
    @POST("Android_Transaction")
    suspend fun postStockOpnameInput(@Body value: String): StockOpnameInputData

    @Headers("Content-Type: application/json")
    @POST("Android_Transaction")
    suspend fun postBinreclassInput(@Body value: String): BinreclassInputData

    @GET("Chart_of_Accounts")
    suspend fun getCustomer(): Response<String>
}

data class BaseResponse<T>(
    @SerializedName("value") val value: List<T>?,
)