package dynamia.com.barcodescanner.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.netcosports.ntlm.NTLMAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dynamia.com.barcodescanner.BuildConfig
import dynamia.com.core.data.LocalDatabase
import dynamia.com.core.data.dao.*
import dynamia.com.core.data.repository.UserRepository
import dynamia.com.core.data.repository.UserRepositoryImpl
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
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DataSourceModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(
            PREFERENCES_FILE_KEY, Context.MODE_PRIVATE
        )

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): LocalDatabase {
        return Room.databaseBuilder(
            appContext,
            LocalDatabase::class.java,
            "MasariDB${BuildConfig.FLAVOR}.sqlite"
        ).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideBinreclassDao(appDatabase: LocalDatabase): BinreclassDao {
        return appDatabase.binreclassDao()
    }

    @Singleton
    @Provides
    fun providePurchaseOrderDao(db: LocalDatabase): PurchaseOrderDao {
        return db.purchaseOrder()
    }

    @Singleton
    @Provides
    fun provideInventoryDao(db: LocalDatabase): InventoryDao {
        return db.inventoryDao()
    }

    @Singleton
    @Provides
    fun provideStockOpnameDao(db: LocalDatabase): StockOpnameDao {
        return db.stockOpnameDao()
    }

    @Singleton
    @Provides
    fun provideTransferReceiptDao(db: LocalDatabase): TransferReceiptDao {
        return db.transferReceipt()
    }

    @Singleton
    @Provides
    fun provideTransferShipment(db: LocalDatabase): TransferShipmentDao {
        return db.transferShipment()
    }

    @Singleton
    @Provides
    fun provideUserDao(db: LocalDatabase): UserDao {
        return db.userDao()
    }

    @Singleton
    @Provides
    fun provideUserRepository(dao: UserDao): UserRepository = UserRepositoryImpl(dao)

    companion object {
        private const val PREFERENCES_FILE_KEY = "STRYGWR${BuildConfig.FLAVOR}"
    }

}