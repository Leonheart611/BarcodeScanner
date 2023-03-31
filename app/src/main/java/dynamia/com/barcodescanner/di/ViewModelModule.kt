package dynamia.com.barcodescanner.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dynamia.com.core.data.dao.*
import dynamia.com.core.data.repository.*
import dynamia.com.core.domain.MasariAPI


@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {

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
    fun provideInventoryRepository(
        dao: InventoryDao,
        api: MasariAPI
    ): InventoryRepository = InventoryRepositoryImpl(dao, api)
}