package dynamia.com.barcodescanner.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : MultiDexApplication() {
    private val PREFERENCES_FILE_KEY = "STRYGWR"
    companion object {
        private lateinit var instance: App

        val context: Context
            get() = instance
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            koin.loadModules(listOf(injectionModule))
        }
    }

    fun provideSettingsPreferences(app: Application): SharedPreferences =
        app.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)


    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}