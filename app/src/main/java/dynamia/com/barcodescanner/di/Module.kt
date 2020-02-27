package dynamia.com.barcodescanner.di

import dynamia.com.barcodescanner.data.LocalDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val injectionModule = module {
    single { LocalDatabase.getDatabase(androidApplication()) }



}