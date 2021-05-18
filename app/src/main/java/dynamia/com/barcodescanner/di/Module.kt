package dynamia.com.barcodescanner.di

import dynamia.com.barcodescanner.ui.history.HistoryInputViewModel
import dynamia.com.barcodescanner.ui.home.HomeViewModel
import dynamia.com.barcodescanner.ui.login.LoginViewModel
import dynamia.com.barcodescanner.ui.pickinglist.PickingListViewModel
import dynamia.com.barcodescanner.ui.pickinglist.pickingdetail.PickingDetailViewModel
import dynamia.com.barcodescanner.ui.pickinglist.pickinginput.PickingListInputViewModel
import dynamia.com.barcodescanner.ui.receipt.ReceiptViewModel
import dynamia.com.barcodescanner.ui.receipt.detail.ReceiptDetailViewModel
import dynamia.com.barcodescanner.ui.receipt.receiptinput.ReceiptInputViewModel
import dynamia.com.barcodescanner.ui.search.SearchViewModel
import dynamia.com.barcodescanner.ui.stockcounting.StockCountingViewModel
import dynamia.com.core.data.LocalDatabase
import dynamia.com.core.data.repository.*
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val injectionModule = module {
    single { App().provideSettingsPreferences(androidApplication()) }
    single { LocalDatabase.getDatabase(androidApplication()) }

    single { get<LocalDatabase>().userDao() }
    single { get<LocalDatabase>().transferShipment() }

    single<PickingListRepository> { PickingListRepositoryImpl(get()) }
    single<ReceiptImportRepository> { ReceiptImportRepositoryImpl(get()) }
    single<ReceiptLocalRepository> { ReceiptLocalRepositoryImpl(get()) }
    single<StockCountRepository> { StockCountRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<NetworkRepository> { NetworkRepositoryImpl(get()) }
    single<TransferShipmentRepository> { TransferShipmentImpl(get(), get()) }

    viewModel { LoginViewModel(get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { PickingListViewModel(get(), get()) }
    viewModel { PickingDetailViewModel(get(), get(), get(), get()) }
    viewModel { PickingListInputViewModel(get(), get()) }
    viewModel { ReceiptViewModel(get(), get(), get()) }
    viewModel { HistoryInputViewModel(get(), get(), get(), get()) }
    viewModel { ReceiptDetailViewModel(get(), get(), get(), get()) }
    viewModel { ReceiptInputViewModel(get(), get(), get()) }
    viewModel { StockCountingViewModel(get(), get(), get()) }
    viewModel { SearchViewModel(get(), get(), get()) }
}