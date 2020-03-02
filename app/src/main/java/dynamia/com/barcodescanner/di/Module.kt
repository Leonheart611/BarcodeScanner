package dynamia.com.barcodescanner.di

import dynamia.com.barcodescanner.data.LocalDatabase
import dynamia.com.barcodescanner.data.repository.*
import dynamia.com.barcodescanner.ui.home.HomeViewModel
import dynamia.com.barcodescanner.ui.login.LoginViewModel
import dynamia.com.barcodescanner.ui.pickinglist.PickingListViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val injectionModule = module {
    single { LocalDatabase.getDatabase(androidApplication()) }

    single { get<LocalDatabase>().pickingListDao() }
    single { get<LocalDatabase>().receiptImportDao() }
    single { get<LocalDatabase>().receiptLocalHeaderDao() }

    single<PickingListRepository> { PickingListRepositoryImpl(get()) }
    single<ReceiptImportRepository> { ReceiptImportRepositoryImpl(get()) }
    single<ReceiptLocalRepository> { ReceiptLocalRepositoryImpl(get()) }

    viewModel { LoginViewModel(get(),get(),get()) }
    viewModel { HomeViewModel(get(),get(),get()) }
    viewModel { PickingListViewModel(get()) }
}