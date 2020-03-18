package dynamia.com.barcodescanner.domain

import dynamia.com.core.data.model.PickingListHeader
import dynamia.com.core.data.model.PickingListLine
import retrofit2.Response
import retrofit2.http.GET

interface API {
    @GET("PickingListHeader")
    suspend fun getPickingListHeader(): Response<PickingListHeader>

    @GET("PickingListLine")
    suspend fun getPickingListLine():Response<PickingListLine>
}