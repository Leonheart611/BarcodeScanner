package dynamia.com.barcodescanner.di

import dynamia.com.barcodescanner.ui.history.HistoryInputViewModel
import dynamia.com.core.data.LocalDatabase
import dynamia.com.barcodescanner.ui.home.HomeViewModel
import dynamia.com.barcodescanner.ui.login.LoginViewModel
import dynamia.com.barcodescanner.ui.pickinglist.PickingListViewModel
import dynamia.com.barcodescanner.ui.pickinglist.pickingdetail.PickingDetailViewModel
import dynamia.com.barcodescanner.ui.pickinglist.pickinginput.PickingListInputViewModel
import dynamia.com.barcodescanner.ui.receipt.ReceiptViewModel
import dynamia.com.barcodescanner.ui.receipt.detail.ReceiptDetailViewModel
import dynamia.com.barcodescanner.ui.receipt.receiptinput.ReceiptInputViewModel
import dynamia.com.core.data.repository.PickingListRepository
import dynamia.com.core.data.repository.PickingListRepositoryImpl
import dynamia.com.core.data.repository.ReceiptImportRepository
import dynamia.com.core.data.repository.ReceiptImportRepositoryImpl
import dynamia.com.core.data.repository.ReceiptLocalRepository
import dynamia.com.core.data.repository.ReceiptLocalRepositoryImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val injectionModule = module {
    single { App().provideSettingsPreferences(androidApplication()) }
    single { LocalDatabase.getDatabase(androidApplication()) }

    single { get<LocalDatabase>().pickingListDao() }
    single { get<LocalDatabase>().receiptImportDao() }
    single { get<LocalDatabase>().receiptLocalHeaderDao() }

    single<PickingListRepository> { PickingListRepositoryImpl(get()) }
    single<ReceiptImportRepository> { ReceiptImportRepositoryImpl(get()) }
    single<ReceiptLocalRepository> { ReceiptLocalRepositoryImpl(get()) }

    viewModel { LoginViewModel(get(),get(),get(),get()) }
    viewModel { HomeViewModel(get(),get(),get(),get()) }
    viewModel { PickingListViewModel(get(),get()) }
    viewModel { PickingDetailViewModel(get()) }
    viewModel { PickingListInputViewModel(get(),get()) }
    viewModel { ReceiptViewModel(get(),get(),get()) }
    viewModel { HistoryInputViewModel(get(),get(),get(),get()) }
    viewModel { ReceiptDetailViewModel(get(),get(),get()) }
    viewModel { ReceiptInputViewModel(get(),get(),get()) }
}