package dynamia.com.core.domain

import com.google.gson.annotations.SerializedName


sealed class ResultWrapper<out T> {
    class Success<out T>(val value: T) : ResultWrapper<T>()
    object SuccessEmptyValue : ResultWrapper<Nothing>()
    class GenericError(val code: Int? = null, val error: ErrorResponse? = null) :
        ResultWrapper<Nothing>()

    class NetworkError(val error: String) : ResultWrapper<Nothing>()
}

data class ErrorResponse(
    @SerializedName("odata.error") val odataError: OdataError
)

data class OdataError(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: Message
)

data class Message(
    @SerializedName("lang") val lang: String,
    @SerializedName("value") val value: String
)