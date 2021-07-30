package dynamia.com.barcodescanner.di

import dynamia.com.barcodescanner.ui.binreclass.BinReclassViewModel
import dynamia.com.barcodescanner.ui.binreclass.detail.BinreclassDetailViewModel
import dynamia.com.barcodescanner.ui.checkstock.CheckStockViewModel
import dynamia.com.barcodescanner.ui.history.HistoryInputViewModel
import dynamia.com.barcodescanner.ui.home.HomeViewModel
import dynamia.com.barcodescanner.ui.login.LoginViewModel
import dynamia.com.barcodescanner.ui.stockopname.StockOpnameViewModel
import dynamia.com.barcodescanner.ui.transferstore.TransferListViewModel
import dynamia.com.barcodescanner.ui.transferstore.transferdetail.TransferDetailViewModel
import dynamia.com.barcodescanner.ui.transferstore.transferinput.TransferInputViewModel
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
    single { get<LocalDatabase>().transferReceipt() }
    single { get<LocalDatabase>().purchaseOrder() }
    single { get<LocalDatabase>().stockOpnameDao() }
    single { get<LocalDatabase>().binreclassDao() }

    single<UserRepository> { UserRepositoryImpl(get()) }
    single<TransferShipmentRepository> { TransferShipmentImpl(get(), get()) }
    single<TransferReceiptRepository> { TransferReceiptRepositoryImpl(get(), get(), get()) }
    single<PurchaseOrderRepository> { PurchaseOrderRepositoryImpl(get(), get()) }
    single<StockOpnameRepository> { StockOpnameRepositoryImpl(get(), get()) }
    single<BinreclassRepository> { BinreclassRepositoryImpl(get(), get()) }

    viewModel { LoginViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { TransferListViewModel(get(), get(), get(), get()) }
    viewModel { TransferDetailViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { TransferInputViewModel(get(), get(), get(), get(), get()) }
    viewModel { HistoryInputViewModel(get(), get(), get(), get(), get()) }
    viewModel { StockOpnameViewModel(get(), get()) }
    viewModel { CheckStockViewModel(get(), get()) }
    viewModel { BinReclassViewModel(get(), get()) }
    viewModel { BinreclassDetailViewModel(get(), get()) }
}