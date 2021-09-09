package dynamia.com.barcodescanner.ui.history

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.databinding.HistoryInputFragmentBinding
import dynamia.com.barcodescanner.ui.history.HistoryType.*
import dynamia.com.barcodescanner.ui.history.adapter.HistoryPurchaseInputAdapter
import dynamia.com.barcodescanner.ui.history.adapter.HistoryStockOpnameInputAdapter
import dynamia.com.barcodescanner.ui.history.adapter.HistoryTransferInputAdapter
import dynamia.com.barcodescanner.ui.history.adapter.HistoryTransferReceiptInputAdapter
import dynamia.com.barcodescanner.ui.transferstore.transferinput.TransferHistoryBottomSheet
import dynamia.com.core.base.BaseFragmentBinding
import dynamia.com.core.data.entinty.PurchaseInputData
import dynamia.com.core.data.entinty.StockOpnameInputData
import dynamia.com.core.data.entinty.TransferInputData
import dynamia.com.core.data.entinty.TransferReceiptInput

@AndroidEntryPoint
class HistoryInputFragment :
    BaseFragmentBinding<HistoryInputFragmentBinding>(HistoryInputFragmentBinding::inflate),
    HistoryTransferInputAdapter.OnHistorySelected,
    HistoryTransferReceiptInputAdapter.OnHistorySelected,
    HistoryPurchaseInputAdapter.OnPurchaseHistoryClicklistener,
    HistoryStockOpnameInputAdapter.OnHistorySelected {

    private val viewModel: HistoryInputViewModel by viewModels()
    private val args: HistoryInputFragmentArgs by navArgs()
    private var scanEntriesAdapter = HistoryTransferInputAdapter(mutableListOf(), this)
    private var scanTransferReceiptAdapter =
        HistoryTransferReceiptInputAdapter(mutableListOf(), this)
    private var scanInputPurchaseAdapter = HistoryPurchaseInputAdapter(mutableListOf(), this)
    private var scanStockInputAdapter = HistoryStockOpnameInputAdapter(mutableListOf(), this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecylerView()
        setupView()
        setupListener()
    }

    private fun setupRecylerView() {
        with(viewBinding.rvInputHistory) {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = when (args.historyType) {
                SHIPMENT -> scanEntriesAdapter
                RECEIPT -> scanTransferReceiptAdapter
                PURCHASE -> scanInputPurchaseAdapter
                STOCKOPNAME -> scanStockInputAdapter
            }
        }
    }

    private fun setupView() {
        when (args.historyType) {
            SHIPMENT -> {
                viewBinding.tvTransferInput.text =
                    getString(R.string.transfer_shipment_history_title)
                args.documentNo?.let { documentNo ->
                    viewModel.transferShipmentRepository.getTransferInputHistoryLiveData(documentNo)
                        .observe(viewLifecycleOwner, {
                            scanEntriesAdapter.updateData(it.toMutableList())
                        })
                }
            }
            RECEIPT -> {
                viewBinding.tvTransferInput.text =
                    getString(R.string.transfer_receipt_history_title)
                args.documentNo?.let { documentNo ->
                    viewModel.transferReceiptRepository.getTransferInputHistoryLiveData(documentNo)
                        .observe(viewLifecycleOwner, {
                            scanTransferReceiptAdapter.updateData(it.toMutableList())
                        })
                }
            }
            PURCHASE -> {
                viewBinding.tvTransferInput.text = getString(R.string.purchase_order_history_title)
                args.documentNo?.let { documentNo ->
                    viewModel.purchaseOrderRepository.getAllPurchaseInputByNo(documentNo)
                        .observe(viewLifecycleOwner, {
                            scanInputPurchaseAdapter.updateData(it.toMutableList())
                        })
                }
            }
            STOCKOPNAME -> {
                viewBinding.tvTransferInput.text = getString(R.string.stock_opname_history_title)
                args.documentNo?.let { documentNo ->
                    viewModel.stockOpnameRepository.getAllInputStockOpnameByDocumentNo(documentNo)
                        .observe(viewLifecycleOwner, {
                            scanStockInputAdapter.updateData(it.toMutableList())
                        })
                } ?: kotlin.run {
                    viewModel.stockOpnameRepository.getAllInputStockOpname()
                        .observe(viewLifecycleOwner, {
                            scanStockInputAdapter.updateData(it.toMutableList())
                        })
                }
            }
        }
    }

    private fun setupListener() {
        viewBinding.tbHistory.title = viewModel.getCompanyName()
        viewBinding.tbHistory.setNavigationOnClickListener {
            view?.findNavController()?.popBackStack()
        }
    }

    override fun onHistorySelectDelete(value: TransferInputData) {
        value.id?.let {
            val dialog = TransferHistoryBottomSheet.newInstance(it, SHIPMENT)
            dialog.show(requireActivity().supportFragmentManager, dialog.tag)
        }
    }

    override fun receiptHistoryCLicklistener(value: TransferReceiptInput) {
        value.id?.let {
            val dialog = TransferHistoryBottomSheet.newInstance(it, RECEIPT)
            dialog.show(requireActivity().supportFragmentManager, dialog.tag)
        }
    }

    override fun historyCLicklistener(value: PurchaseInputData) {
        value.id?.let {
            val dialog = TransferHistoryBottomSheet.newInstance(it, PURCHASE)
            dialog.show(requireActivity().supportFragmentManager, dialog.tag)
        }
    }

    override fun onStockOpnameCLicklistener(value: StockOpnameInputData) {
        value.id?.let {
            val dialog = TransferHistoryBottomSheet.newInstance(it, STOCKOPNAME)
            dialog.show(requireActivity().supportFragmentManager, dialog.tag)
        }
    }

}
