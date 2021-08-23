package dynamia.com.barcodescanner.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.netcosports.ntlm.NTLMAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dynamia.com.core.data.LocalDatabase
import dynamia.com.core.data.dao.*
import dynamia.com.core.data.repository.*
import dynamia.com.core.domain.MasariAPI
import dynamia.com.core.domain.MasariRetrofit
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
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataSourceModule {
    private const val PREFERENCES_FILE_KEY = "STRYGWR"

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context) =
        context.getSharedPreferences(
            PREFERENCES_FILE_KEY, Context.MODE_PRIVATE
        )

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): LocalDatabase {
        return Room.databaseBuilder(
            appContext,
            LocalDatabase::class.java,
            "MasariDB.sqlite"
        ).build()
    }

    @Provides
    fun provideBinreclassDao(appDatabase: LocalDatabase): BinreclassDao {
        return appDatabase.binreclassDao()
    }

    @Provides
    fun providePurchaseOrderDao(db: LocalDatabase): PurchaseOrderDao {
        return db.purchaseOrder()
    }

    @Provides
    fun provideStockOpnameDao(db: LocalDatabase): StockOpnameDao {
        return db.stockOpnameDao()
    }

    @Provides
    fun provideTransferReceiptDao(db: LocalDatabase): TransferReceiptDao {
        return db.transferReceipt()
    }

    @Provides
    fun provideTransferShipment(db: LocalDatabase): TransferShipmentDao {
        return db.transferShipment()
    }

    @Provides
    fun provideUserDao(db: LocalDatabase): UserDao {
        return db.userDao()
    }


    @Provides
    fun provideApiService(sharedPreferences: SharedPreferences): MasariAPI {
        var retrofit: Retrofit? = null
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

    @Provides
    fun provideBinReclassRepository(
        dao: BinreclassDao,
        masariAPI: MasariAPI,
    ): BinreclassRepository = BinreclassRepositoryImpl(dao, masariAPI)

    @Provides
    fun providePurchaseOrderRepository(
        dao: PurchaseOrderDao,
        api: MasariAPI,
    ): PurchaseOrderRepository = PurchaseOrderRepositoryImpl(dao, api)

    @Provides
    fun provideStockOpnameRepository(
        dao: StockOpnameDao,
        api: MasariAPI,
    ): StockOpnameRepository = StockOpnameRepositoryImpl(dao, api)

    @Provides
    fun provideTransferReceiptRepository(
        dao: TransferReceiptDao,
        api: MasariAPI,
        lineDao: TransferShipmentDao,
    ): TransferReceiptRepository = TransferReceiptRepositoryImpl(dao, api, lineDao)

    @Provides
    fun provideShipmentRepository(
        dao: TransferShipmentDao,
        api: MasariAPI,
    ): TransferShipmentRepository = TransferShipmentImpl(dao, api)

    @Provides
    fun provideUserRepository(dao: UserDao): UserRepository = UserRepositoryImpl(dao)
}