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

    fun getClient(
        serverAddress: String,
        username: String,
        password: String
    ): API {
        if (retrofit == null) {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .authenticator(NTLMAuthenticator(username, password, ""))
                .addInterceptor(CustomInterceptor())
                .addNetworkInterceptor(logger)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(90, TimeUnit.SECONDS)
                .cache(null)
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
                .build()
            val request = chain.request().newBuilder()
                .addHeader("Accept", "application/json")
                .url(url)
                .build()
            return chain.proceed(request)
        }
    }
}