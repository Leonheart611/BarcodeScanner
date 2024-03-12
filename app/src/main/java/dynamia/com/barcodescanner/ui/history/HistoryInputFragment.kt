package dynamia.com.barcodescanner.ui.history

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.databinding.HistoryInputFragmentBinding
import dynamia.com.barcodescanner.ui.history.HistoryType.*
import dynamia.com.barcodescanner.ui.history.adapter.*
import dynamia.com.barcodescanner.ui.transferstore.transferinput.TransferHistoryBottomSheet
import dynamia.com.core.base.BaseFragmentBinding
import dynamia.com.core.data.entinty.*

@AndroidEntryPoint
class HistoryInputFragment :
    BaseFragmentBinding<HistoryInputFragmentBinding>(HistoryInputFragmentBinding::inflate),
    HistoryTransferInputAdapter.OnHistorySelected,
    HistoryTransferReceiptInputAdapter.OnHistorySelected,
    HistoryPurchaseInputAdapter.OnPurchaseHistoryClicklistener,
    HistoryStockOpnameInputAdapter.OnHistorySelected,
    HistoryInventoryInputAdapter.HistoryInventoryClicklistener {

    private val viewModel: HistoryInputViewModel by viewModels()
    private val args: HistoryInputFragmentArgs by navArgs()
    private var scanEntriesAdapter = HistoryTransferInputAdapter(this)
    private var scanTransferReceiptAdapter = HistoryTransferReceiptInputAdapter(this)
    private var scanInputPurchaseAdapter = HistoryPurchaseInputAdapter(this)
    private var scanStockInputAdapter = HistoryStockOpnameInputAdapter(this)
    private var scanInventoryInputAdapter = HistoryInventoryInputAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.updateViewModelVariable(args)
        setupRecylerView()
        setupView()
    }

    private fun setupRecylerView() {
        with(viewBinding.rvInputHistory) {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = when (args.historyType) {
                SHIPMENT -> scanEntriesAdapter
                RECEIPT -> scanTransferReceiptAdapter
                PURCHASE -> scanInputPurchaseAdapter
                STOCKOPNAME -> scanStockInputAdapter
                INVENTORY -> scanInventoryInputAdapter
            }
        }
        viewBinding.tbHistory.isGone = args.historyType != STOCKOPNAME
        viewBinding.etSearch.isGone = args.historyType != STOCKOPNAME
        viewBinding.etSearch.doOnTextChanged { text, _, _, _ ->
            viewModel.updateQuery(text.toString())
        }
    }

    private fun setupView() {
        viewBinding.tbHistory.setNavigationOnClickListener {
            view?.findNavController()?.popBackStack()
        }
        when (args.historyType) {
            SHIPMENT -> {
                viewBinding.tvTransferInput.text =
                    getString(R.string.transfer_shipment_history_title)
                args.documentNo?.let { documentNo ->
                    viewModel.transferShipmentRepository.getTransferInputHistoryLiveData(
                        documentNo,
                        !args.inputValidate
                    ).observe(viewLifecycleOwner) {
                        scanEntriesAdapter.submitList(it.toMutableList())
                    }
                }
            }

            RECEIPT -> {
                viewBinding.tvTransferInput.text =
                    getString(R.string.transfer_receipt_history_title)
                args.documentNo?.let { documentNo ->
                    viewModel.transferReceiptRepository.getTransferInputHistoryLiveData(
                        documentNo,
                        !args.inputValidate
                    )
                        .observe(viewLifecycleOwner) {
                            scanTransferReceiptAdapter.submitList(it.toMutableList())
                        }
                }
            }

            PURCHASE -> {
                viewBinding.tvTransferInput.text = getString(R.string.purchase_order_history_title)
                args.documentNo?.let { documentNo ->
                    viewModel.purchaseOrderRepository.getAllPurchaseInputByNo(
                        documentNo,
                        !args.inputValidate
                    )
                        .observe(viewLifecycleOwner) {
                            scanInputPurchaseAdapter.submitList(it)
                        }
                }
            }

            STOCKOPNAME -> {
                viewBinding.tvTransferInput.text = getString(R.string.stock_opname_history_title)
                viewModel.stockOpnameInputSearch.observe(viewLifecycleOwner) {
                    scanStockInputAdapter.submitList(it.toMutableList())
                }
            }

            INVENTORY -> {
                viewBinding.tvTransferInput.text = getString(R.string.inventory_pick_history)
                args.documentNo?.let { no ->
                    viewModel.inventoryRepository.getInventoryInputData(no)
                        .observe(viewLifecycleOwner) {
                            scanInventoryInputAdapter.submitList(it)
                        }
                }
            }
        }
    }

    override fun onHistorySelectDelete(value: TransferInputData) {
        if (args.inputValidate)
            value.id?.let {
                val dialog = TransferHistoryBottomSheet.newInstance(it, SHIPMENT)
                dialog.show(requireActivity().supportFragmentManager, dialog.tag)
            }
    }

    override fun receiptHistoryCLicklistener(value: TransferReceiptInput) {
        if (args.inputValidate)
            value.id?.let {
                val dialog = TransferHistoryBottomSheet.newInstance(it, RECEIPT)
                dialog.show(requireActivity().supportFragmentManager, dialog.tag)
            }
    }

    override fun historyCLicklistener(value: PurchaseInputData) {
        if (args.inputValidate)
            value.id?.let {
                val dialog = TransferHistoryBottomSheet.newInstance(it, PURCHASE)
                dialog.show(requireActivity().supportFragmentManager, dialog.tag)
            }
    }

    override fun onStockOpnameCLicklistener(value: StockOpnameInputData) {
        if (args.inputValidate)
            value.id?.let {
                val dialog = TransferHistoryBottomSheet.newInstance(it, STOCKOPNAME)
                dialog.show(requireActivity().supportFragmentManager, dialog.tag)
            }
    }

    override fun onclicklistener(value: InventoryInputData) {
        if (args.inputValidate)
            value.id?.let {
                val dialog = TransferHistoryBottomSheet.newInstance(it, INVENTORY)
                dialog.show(requireActivity().supportFragmentManager, dialog.tag)
            }
    }

    companion object {
        const val HISTORY_TYPE = "historyType"
        const val DOCUMENT_NO = "documentNo"
        const val INPUT_VALIDATE = "inputValidate"
        fun newInstance(historyType: HistoryType, documentNo: String, inputValidation: Boolean) =
            HistoryInputFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(HISTORY_TYPE, historyType)
                    putString(DOCUMENT_NO, documentNo)
                    putBoolean(INPUT_VALIDATE, inputValidation)
                }
            }

    }
}
