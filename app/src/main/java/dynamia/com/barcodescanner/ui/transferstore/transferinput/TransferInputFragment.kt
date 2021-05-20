package dynamia.com.barcodescanner.ui.transferstore.transferinput

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.MainActivity
import dynamia.com.barcodescanner.ui.history.adapter.HistoryTransferInputAdapter
import dynamia.com.barcodescanner.ui.transferstore.adapter.PickingMultipleLineAdapter
import dynamia.com.core.data.entinty.TransferShipmentLine
import dynamia.com.core.data.model.PickingListLineValue
import dynamia.com.core.data.model.PickingListScanEntriesValue
import dynamia.com.core.util.*
import kotlinx.android.synthetic.main.delete_confirmation_dialog.*
import kotlinx.android.synthetic.main.dialog_multiple_item.*
import kotlinx.android.synthetic.main.dialog_part_no_not_found.*
import kotlinx.android.synthetic.main.dialog_part_no_not_found.tv_error_message
import kotlinx.android.synthetic.main.header_layout.*
import kotlinx.android.synthetic.main.history_input_fragment.*
import kotlinx.android.synthetic.main.item_input_header.*
import kotlinx.android.synthetic.main.transfer_input_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class TransferInputFragment : Fragment(), PickingMultipleLineAdapter.OnMultipleLineSelected,
    HistoryTransferInputAdapter.OnHistorySelected {
    private val viewModel: TransferInputViewModel by viewModel()
    private val args: TransferInputFragmentArgs by navArgs()
    private var dialog: Dialog? = null
    private var poNoDialog: Dialog? = null
    private var purchaseNo: String = ""
    var activity: MainActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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
        viewModel.transferInputViewState.observe(viewLifecycleOwner, {
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
                }
            }
        })
        viewModel.inputValidation.observe(viewLifecycleOwner, {
            when (it) {
                TransferInputViewModel.InputValidation.AllValidationCorrect -> {

                }
                TransferInputViewModel.InputValidation.BarcodeEmpty -> {
                    til_transferinput_barcode.error = "Must Fill this Field"
                }
                TransferInputViewModel.InputValidation.QtyEmpty -> {
                    til_transferinput_qty.error = "Must Fill this Field"
                }
            }
        })
    }

    private fun showSuccessfulData(data: TransferShipmentLine) {
        tv_transfer_item_name.text = data.description
        tv_transfer_qty.text = data.quantity.toString()
        til_transferinput_name.editText?.setText(data.no)
    }

    private fun setupView() {
        toolbar_picking_list_input.title = viewModel.getCompanyName()
        et_transferinput_barcode.doAfterTextChanged {
            viewModel.getPickingListLineValue(args.transferNo, it.toString())
        }
    }

    private fun setupListener() {
        toolbar_picking_list_input.setOnClickListener { view?.findNavController()?.popBackStack() }
        btn_reset.setOnClickListener { clearData() }
        btn_save.setOnClickListener {
            viewModel.checkUserInputValidation(
                et_transferinput_barcode.text.toString(),
                et_tranferinput_qty.text.toString()
            )
        }
        toolbar_picking_list_input.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.history_data -> {
                    val dialog = TransferHistoryBottomSheet.newInstance(args.transferNo)
                    dialog.show(requireActivity().supportFragmentManager, dialog.tag)
                    true
                }
                else -> false
            }
        }
    }

    private fun clearData() {
        et_transferinput_barcode.text?.clear()
        et_transferinput_name.text?.clear()
        et_tranferinput_qty.text?.clear()
    }


    private fun checkPONo(poNO: String): Boolean {
        return (purchaseNo.isNotEmpty() && purchaseNo == poNO)
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

    private fun openHistoryDialog() {

    }

    override fun onHistorySelectDelete(value: PickingListScanEntriesValue) {
        context?.let { context ->
            val dialog = Dialog(context)
            with(dialog) {
                setContentView(R.layout.delete_confirmation_dialog)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                window
                    ?.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                btn_delete.setOnClickListener {

                    dismiss()
                    setupView()
                }
                btn_cancel.setOnClickListener {
                    dismiss()
                }
                show()
            }
        }
    }

}
