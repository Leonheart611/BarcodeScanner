package dynamia.com.barcodescanner.di

import android.content.SharedPreferences
import com.netcosports.ntlm.NTLMAuthenticator
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.FragmentScoped
import dagger.hilt.android.scopes.ViewModelScoped
import dynamia.com.core.domain.MasariAPI
import dynamia.com.core.util.getBaseUrl
import dynamia.com.core.util.getDomain
import dynamia.com.core.util.getPassword
import dynamia.com.core.util.getUserName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
object NetworkModule {
    @Provides
    @ViewModelScoped
    @Named("BASE_URL")
    fun provideBaseUrl(sharedPreferences: SharedPreferences): String =
        sharedPreferences.getBaseUrl()

    @Provides
    @ViewModelScoped
    @Named("USERNAME")
    fun provideUsername(sharedPreferences: SharedPreferences): String =
        sharedPreferences.getUserName()

    @Provides
    @ViewModelScoped
    @Named("PASSWORD")
    fun providePassword(sharedPreferences: SharedPreferences): String =
        sharedPreferences.getPassword()

    @Provides
    @ViewModelScoped
    @Named("DOMAIN")
    fun provideDomain(sharedPreferences: SharedPreferences): String = sharedPreferences.getDomain()


    @Provides
    @ViewModelScoped
    fun provideClient(
        @Named("USERNAME") username: String,
        @Named("PASSWORD") password: String,
        @Named("DOMAIN") domain: String
    ): OkHttpClient {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .authenticator(
                NTLMAuthenticator(username, password, domain),
            )
            .build()
    }

    @Provides
    @ViewModelScoped
    fun provideApiService(client: OkHttpClient, @Named("BASE_URL") baseUrl: String): MasariAPI {
        var retrofit: Retrofit? = null
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
        return retrofit!!.create(MasariAPI::class.java)
    }


}