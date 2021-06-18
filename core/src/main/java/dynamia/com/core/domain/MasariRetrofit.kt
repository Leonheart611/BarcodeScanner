package dynamia.com.core.domain

import android.content.SharedPreferences
import com.netcosports.ntlm.NTLMAuthenticator
import dynamia.com.core.util.*
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


class MasariRetrofit {
    private var retrofit: Retrofit? = null

    fun getClient(sharedPreferences: SharedPreferences): MasariAPI {
        if (retrofit == null) {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .cache(null)
                .authenticator(
                    NTLMAuthenticator(
                        sharedPreferences.getUserName(),
                        sharedPreferences.getPassword(),
                        sharedPreferences.getDomain()
                    )
                )
                .build()
            retrofit = Retrofit.Builder()
                .baseUrl(sharedPreferences.getBaseUrl())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
        return retrofit!!.create(MasariAPI::class.java)
    }
}