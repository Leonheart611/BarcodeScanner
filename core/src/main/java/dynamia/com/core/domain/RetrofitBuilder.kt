package dynamia.com.core.domain

import com.netcosports.ntlm.NTLMAuthenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitBuilder {
    private var retrofit: Retrofit? = null
    private var baseURL =
        "http://mikaoctofrentzen:7048/DynamicsNAV90/OData/Company('PT.%20Mastersystem%20Infotama')/"

    fun getClient(
        serverAddress: String = "",
        username: String = "nav",
        password: String = "12345"
    ): API {
        if (retrofit == null) {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .authenticator(NTLMAuthenticator(username, password, ""))
                .addInterceptor(CustomInterceptor())
                .addNetworkInterceptor(logger)
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(90, TimeUnit.SECONDS)
                .build()
            retrofit = Retrofit.Builder()
                .baseUrl(serverAddress)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
        return retrofit!!.create(API::class.java)
    }

    class CustomInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val url = chain.request().url.newBuilder()
                .addQueryParameter("\$format", "json")
                .build()
            val request = chain.request().newBuilder()
                .url(url)
                .build()
            return chain.proceed(request)
        }
    }
}