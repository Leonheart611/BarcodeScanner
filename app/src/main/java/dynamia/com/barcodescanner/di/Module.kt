package dynamia.com.barcodescanner.di

import dynamia.com.barcodescanner.data.LocalDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val injectionModule = module {
    single { LocalDatabase.getDatabase(androidApplication()) }

    single { get<LocalDatabase>().pickingListHeaderDao() }
    single { get<LocalDatabase>().pickingListLineDao() }
    single { get<LocalDatabase>().pickingListScanEntriesDao() }
    single { get<LocalDatabase>().receiptImportHeaderDao() }
    single { get<LocalDatabase>().receiptImportLineDao() }
    single { get<LocalDatabase>().receiptImportScanEntriesDao() }
    single { get<LocalDatabase>().receiptLocalHeaderDao() }
    single { get<LocalDatabase>().receiptLocalLineDao() }
    single { get<LocalDatabase>().receiptLocalScanEntriesDao() }


}