package dynamia.com.barcodescanner.ui.transferstore.transferinput

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.MainActivity
import dynamia.com.barcodescanner.ui.history.HistoryType
import dynamia.com.barcodescanner.ui.transferstore.TransferType.*
import dynamia.com.barcodescanner.ui.transferstore.adapter.PickingMultipleLineAdapter
import dynamia.com.core.data.entinty.PurchaseOrderLine
import dynamia.com.core.data.entinty.StockOpnameData
import dynamia.com.core.data.entinty.TransferShipmentLine
import dynamia.com.core.data.model.PickingListLineValue
import dynamia.com.core.util.*
import kotlinx.android.synthetic.main.delete_confirmation_dialog.*
import kotlinx.android.synthetic.main.dialog_multiple_item.*
import kotlinx.android.synthetic.main.dialog_part_no_not_found.*
import kotlinx.android.synthetic.main.dialog_part_no_not_found.tv_error_message
import kotlinx.android.synthetic.main.header_layout.*
import kotlinx.android.synthetic.main.item_input_header.*
import kotlinx.android.synthetic.main.transfer_input_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class TransferInputFragment : Fragment(), PickingMultipleLineAdapter.OnMultipleLineSelected {
    private val viewModel: TransferInputViewModel by viewModel()
    private val args: TransferInputFragmentArgs by navArgs()
    private var dialog: Dialog? = null
    private var poNoDialog: Dialog? = null
    var activity: MainActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.transfer_input_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        activity = requireActivity() as MainActivity
        setupListener()
        setupObserverable()
    }

    private fun setupObserverable() {
        with(viewModel) {
            transferInputViewState.observe(viewLifecycleOwner, {
                when (it) {
                    is TransferInputViewModel.TransferInputViewState.ErrorGetData -> {
                        context?.showLongToast(it.message)
                    }
                    is TransferInputViewModel.TransferInputViewState.LoadingSearchPickingList -> {
                        activity?.showLoading(it.status)
                    }
                    is TransferInputViewModel.TransferInputViewState.SuccessGetValue -> {
                        showSuccessfulData(it.data)
                    }
                    is TransferInputViewModel.TransferInputViewState.ErrorSaveData -> {
                        context?.showLongToast(it.message)
                    }
                    TransferInputViewModel.TransferInputViewState.SuccessSaveData -> {
                        context?.showLongToast("Success Save Data")
                        args.barcodeNo?.let { et_tranferinput_qty.text?.clear() }
                            ?: kotlin.run { clearData() }
                    }
                    is TransferInputViewModel.TransferInputViewState.SuccessGetPurchaseValue -> {
                        showSuccessPurchaseData(it.data)
                    }
                    is TransferInputViewModel.TransferInputViewState.SuccessGetStockOpnameValue -> {
                        showSuccessStockOpname(it.data)
                    }
                }
            })
            inputValidation.observe(viewLifecycleOwner, {
                when (it) {
                    TransferInputViewModel.InputValidation.BarcodeEmpty -> {
                        til_transferinput_barcode.error = "Must Fill this Field"
                    }
                    TransferInputViewModel.InputValidation.QtyEmpty -> {
                        til_transferinput_qty.error = "Must Fill this Field"
                    }
                }
            })
        }
    }

    private fun showSuccessfulData(data: TransferShipmentLine) {
        tv_transfer_item_name.text = data.description
        til_transferinput_name.editText?.setText(data.no)
        when (args.transferType) {
            SHIPMENT -> tv_transfer_qty.text = data.quantity.toString()
            RECEIPT -> tv_transfer_qty.text = data.qtyInTransit.toString()
        }
    }

    private fun showSuccessPurchaseData(data: PurchaseOrderLine) {
        tv_transfer_item_name.text = data.description
        til_transferinput_name.editText?.setText(data.no)
        tv_transfer_qty.text = data.quantity.toString()
    }

    private fun showSuccessStockOpname(data: StockOpnameData) {
        tv_transfer_item_name.text = data.itemIdentifier
        til_transferinput_name.editText?.setText(data.itemNo)
        tv_transfer_qty.text = ""
    }

    private fun setupView() {
        toolbar_picking_list_input.title = viewModel.getCompanyName()
        et_transfer_input_barcode.requestFocus()
        args.barcodeNo?.let {
            et_transfer_input_barcode.setText(it)
            et_transfer_input_barcode.isEnabled = false
            getPickingListLineData(it)
            et_tranferinput_qty.requestFocus()
        }
        et_transfer_input_barcode.setOnEditorActionListener { _, keyCode, event ->
            if (((event?.action ?: -1) == KeyEvent.ACTION_DOWN)
                || keyCode == EditorInfo.IME_ACTION_NEXT
            ) {
                getPickingListLineData(et_transfer_input_barcode.text.toString())
                et_tranferinput_qty.requestFocus()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        tv_transfer_input_title.text = when (args.transferType) {
            SHIPMENT -> getString(R.string.transfer_store)
            RECEIPT -> getString(R.string.transfer_receipt_title)
            PURCHASE -> getString(R.string.purchase_order_title)
            STOCKOPNAME -> getString(R.string.stock_opname_title)
        }
    }


    private fun getPickingListLineData(barcode: String) {
        when (args.transferType) {
            SHIPMENT -> viewModel.getShipmentListLineValue(args.transferNo, barcode)
            RECEIPT -> viewModel.getReceiptListLineValue(args.transferNo, barcode)
            PURCHASE -> viewModel.getPurchaseLineValue(args.transferNo, barcode)
            STOCKOPNAME -> viewModel.getStockOpnameValue(barcode)

        }
    }

    private fun setupListener() {
        toolbar_picking_list_input.setOnClickListener { view?.findNavController()?.popBackStack() }
        btn_reset.setOnClickListener {
            args.barcodeNo?.let { et_tranferinput_qty.text?.clear() }
                ?: kotlin.run { clearData() }
        }
        btn_save.setOnClickListener {
            viewModel.checkUserInputValidation(
                et_transfer_input_barcode.text.toString(),
                et_tranferinput_qty.text.toString(),
                args.transferType
            )
        }
        toolbar_picking_list_input.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.history_data -> {
                    val action =
                        TransferInputFragmentDirections.actionReceivingFragmentToHistoryInputFragment(
                            documentNo = args.transferNo,
                            historyType = when (args.transferType) {
                                SHIPMENT -> HistoryType.SHIPMENT
                                RECEIPT -> HistoryType.RECEIPT
                                PURCHASE -> HistoryType.PURCHASE
                                STOCKOPNAME -> HistoryType.STOCKOPNAME
                            }
                        )
                    view?.findNavController()?.navigate(action)
                    true
                }
                else -> false
            }
        }
    }

    private fun clearData() {
        et_transfer_input_barcode.text?.clear()
        et_transferinput_name.text?.clear()
        et_tranferinput_qty.text?.clear()
        et_transfer_input_barcode.requestFocus()
    }

    private fun showMultipleDataDialog(data: List<PickingListLineValue>) {
        context?.let { context ->
            dialog = Dialog(context)
            dialog?.let { dialog ->
                with(dialog) {
                    setContentView(R.layout.dialog_multiple_item)
                    window
                        ?.setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    val adapter = PickingMultipleLineAdapter(
                        data.toMutableList(),
                        this@TransferInputFragment
                    )
                    rv_muliple_line.layoutManager =
                        LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                    rv_muliple_line.adapter = adapter

                    show()
                }
            }
        }
    }

    private fun showErrorPartNo() {
        context?.let { context ->
            poNoDialog = Dialog(context)
            poNoDialog?.let { dialog ->
                with(dialog) {
                    setContentView(R.layout.dialog_part_no_not_found)
                    window
                        ?.setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    btn_ok.setOnClickListener {
                        dismiss()
                    }
                    show()
                }
            }
        }
    }

    private fun showErrorPONoDialog(message: String) {
        context?.let { context ->
            poNoDialog = Dialog(context)
            poNoDialog?.let { dialog ->
                with(dialog) {
                    setContentView(R.layout.dialog_part_no_not_found)
                    window
                        ?.setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    tv_error_message.text = message
                    btn_ok.setOnClickListener {
                        dismiss()
                    }
                    show()
                }
            }
        }
    }

    override fun onMultiplelineSelected(data: PickingListLineValue) {
        dialog?.dismiss()
    }

}
