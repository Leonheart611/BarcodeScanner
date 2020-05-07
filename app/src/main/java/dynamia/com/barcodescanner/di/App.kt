package dynamia.com.barcodescanner.di

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.content.SharedPreferences
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : MultiDexApplication() {
    private val PREFERENCES_FILE_KEY = "STRYGWR"
    companion object {
        private lateinit var instance: App

        val context: Context
            get() = instance
        private lateinit var firebaseAnalytics: FirebaseAnalytics
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
             androidFileProperties()
            modules(injectionModule)
        }
    }
    fun provideSettingsPreferences(app: Application): SharedPreferences =
        app.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)


    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}