package dynamia.com.barcodescanner.domain

import dynamia.com.core.data.model.*
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET

interface API {
    @GET("PickingListHeader")
    suspend fun getPickingListHeaderAsync(): PickingListHeader

    @GET("PickingListLine")
    suspend fun getPickingListLineAsync(): PickingListLine

    @GET("ReceiptImportHeader")
    suspend fun getReceiptImportHeaderAsync(): ReceiptImportHeader

    @GET("ReceiptImportLine")
    suspend fun getReceiptImportLineAsync(): ReceiptImportLine

    @GET("ReceiptLocalHeader")
    suspend fun getReceiptLocalHeaderAsync(): ReceiptLocalHeader

    @GET("ReceiptLocalLine")
    suspend fun getReceiptLocalLineAsync(): ReceiptLocalLine

}