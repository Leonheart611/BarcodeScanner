package dynamia.com.core.domain

import dynamia.com.core.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface API {
	@GET("PickingListHeader")
	suspend fun getPickingListHeaderAsync(): Response<PickingListHeader>
	
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
	
	@GET("PeminjamanHeader")
	suspend fun getPeminjamHeaderAsync(): Response<PeminjamanHeaderAsset>
	
	@GET("PeminjamanDetail")
	suspend fun getPeminjamanDetailAsycn(): Response<PeminjamanDetailAsset>
	
	@GET("DORPickingListHeader")
	suspend fun getDorPickingListHeaderAsycn(): Response<DorPickHeaderAsset>
	
	@GET("DORPickingListDetail")
	suspend fun getDorPickingListDetailAsycn(): Response<DorPickingDetailAsset>
	
	@Headers("Content-Type: application/json")
	@POST("ReceiptImportEntry")
	suspend fun postReceiptImportEntry(@Body value: String): ReceiptImportScanEntriesValue
	
	@Headers("Content-Type: application/json")
	@POST("RecieptLocalEntry")
	suspend fun postReceiptLocalEntry(@Body value: String): ReceiptLocalScanEntriesValue
	
	@Headers("Content-Type: application/json")
	@POST("PickingListEntry")
	suspend fun postPickingListEntry(@Body value: String): PickingListScanEntriesValue
	
	@Headers("Content-Type: application/json")
	@POST("StockCountEntry")
	suspend fun postStockCountEntry(@Body value: String): Response<StockCount>
	
	@Headers("Content-Type: application/json")
	@POST("PeminjamanEntry")
	suspend fun postPeminjamEntry(@Body value: String): Response<PeminjamScanEntries>
	
	@Headers("Content-Type: application/json")
	@POST("DORPickingEntry")
	suspend fun postDorPickEntry(@Body value: String): Response<DorPickingScanEntries>
	
	
}