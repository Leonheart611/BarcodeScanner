package dynamia.com.barcodescanner.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.history.HistoryType.*
import dynamia.com.barcodescanner.ui.history.adapter.HistoryPurchaseInputAdapter
import dynamia.com.barcodescanner.ui.history.adapter.HistoryTransferInputAdapter
import dynamia.com.barcodescanner.ui.history.adapter.HistoryTransferReceiptInputAdapter
import dynamia.com.barcodescanner.ui.transferstore.transferinput.TransferHistoryBottomSheet
import dynamia.com.core.data.entinty.PurchaseInputData
import dynamia.com.core.data.entinty.TransferInputData
import dynamia.com.core.data.entinty.TransferReceiptInput
import kotlinx.android.synthetic.main.history_input_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryInputFragment : Fragment(), HistoryTransferInputAdapter.OnHistorySelected,
    HistoryTransferReceiptInputAdapter.OnHistorySelected,
    HistoryPurchaseInputAdapter.OnPurchaseHistoryClicklistener {

    private val viewModel: HistoryInputViewModel by viewModel()
    private val args: HistoryInputFragmentArgs by navArgs()
    private var scanEntriesAdapter = HistoryTransferInputAdapter(mutableListOf(), this)
    private var scanTransferReceiptAdapter =
        HistoryTransferReceiptInputAdapter(mutableListOf(), this)
    private var scanInputPurchaseAdapter = HistoryPurchaseInputAdapter(mutableListOf(), this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.history_input_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecylerView()
        setupView()
        setupListener()
    }

    private fun setupRecylerView() {
        with(rv_input_history) {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = when (args.historyType) {
                SHIPMENT -> scanEntriesAdapter
                RECEIPT -> scanTransferReceiptAdapter
                PURCHASE -> scanInputPurchaseAdapter
            }
        }
    }

    private fun setupView() {
        when (args.historyType) {
            SHIPMENT -> {
                tv_transfer_input.text = getString(R.string.transfer_shipment_history_title)
                args.documentNo?.let { documentNo ->
                    viewModel.transferShipmentRepository.getTransferInputHistoryLiveData(documentNo)
                        .observe(viewLifecycleOwner, {
                            scanEntriesAdapter.updateData(it.toMutableList())
                        })
                }
            }
            RECEIPT -> {
                tv_transfer_input.text = getString(R.string.transfer_receipt_history_title)
                args.documentNo?.let { documentNo ->
                    viewModel.transferReceiptRepository.getTransferInputHistoryLiveData(documentNo)
                        .observe(viewLifecycleOwner, {
                            scanTransferReceiptAdapter.updateData(it.toMutableList())
                        })
                }
            }
            PURCHASE -> {
                tv_transfer_input.text = getString(R.string.purchase_order_history_title)
                args.documentNo?.let { documentNo ->
                    viewModel.purchaseOrderRepository.getAllPurchaseInputByNo(documentNo)
                        .observe(viewLifecycleOwner, {
                            scanInputPurchaseAdapter.updateData(it.toMutableList())
                        })
                }
            }
        }
    }

    private fun setupListener() {
        tb_history.title = viewModel.getCompanyName()
        tb_history.setNavigationOnClickListener {
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

}
