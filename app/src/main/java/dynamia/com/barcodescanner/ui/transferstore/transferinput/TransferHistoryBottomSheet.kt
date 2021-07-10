package dynamia.com.barcodescanner.ui.transferstore.transferinput

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.history.HistoryType
import dynamia.com.barcodescanner.ui.history.HistoryType.*
import dynamia.com.barcodescanner.ui.history.adapter.HistoryTransferInputAdapter
import dynamia.com.core.data.entinty.TransferInputData
import dynamia.com.core.util.showLongToast
import kotlinx.android.synthetic.main.bottom_sheet_picking_history_fragment.*
import kotlinx.android.synthetic.main.delete_confirmation_dialog.*
import kotlinx.android.synthetic.main.item_input_header.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TransferHistoryBottomSheet : BottomSheetDialogFragment(),
    HistoryTransferInputAdapter.OnHistorySelected {
    private val viewModel: TransferInputViewModel by viewModel()
    private val no by lazy { arguments?.getInt(ARGS_TRANSFER_ID) }
    private val historyType by lazy { arguments?.getSerializable(ARGS_HISTORY_TYPE) as HistoryType }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            setupFullHeight(bottomSheetDialog)
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
        val layoutParams = bottomSheet.layoutParams
        val windowHeight = getWindowHeight()
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }

    private fun getWindowHeight(): Int {
        val displayMetrics = DisplayMetrics()
        (context as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_picking_history_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (historyType) {
            SHIPMENT -> no?.let { viewModel.getHistoryValueDetail(it) }
            RECEIPT -> no?.let { viewModel.getHistoryReceiptDetail(it) }
            PURCHASE -> no?.let { viewModel.getPurchaseHistoryDetail(it) }
        }
        setupView()
        setObseverable()
    }

    private fun setObseverable() {
        viewModel.transferInputViewState.observe(viewLifecycleOwner, {
            when (it) {
                is TransferInputViewModel.TransferInputViewState.SuccessGetHistoryValue -> {
                    with(it.data) {
                        et_transferinput_name.apply {
                            setText(this@with.itemNo)
                            isEnabled = false
                        }
                        et_tranferinput_qty.setText(this.quantity.toString())
                    }
                }
                is TransferInputViewModel.TransferInputViewState.ErrorDeleteData -> {
                    context?.showLongToast(it.message)
                }
                TransferInputViewModel.TransferInputViewState.SuccessDeleteData -> {
                    context?.showLongToast("Data Deleted")
                    dismiss()
                }
                is TransferInputViewModel.TransferInputViewState.ErrorUpdateData -> {
                    context?.showLongToast(it.message)
                }
                TransferInputViewModel.TransferInputViewState.SuccessUpdateData -> {
                    context?.showLongToast("Data Updated")
                    dismiss()
                }
                is TransferInputViewModel.TransferInputViewState.SuccessGetReceiptHistoryValue -> {
                    with(it.data) {
                        et_transferinput_name.apply {
                            setText(this@with.itemNo)
                            isEnabled = false
                        }
                        et_tranferinput_qty.setText(this.quantity.toString())
                    }
                }
                is TransferInputViewModel.TransferInputViewState.SuccessGetPurchaseHistory -> {
                    with(it.data) {
                        et_transferinput_name.apply {
                            setText(this@with.itemNo)
                            isEnabled = false
                        }
                        et_tranferinput_qty.setText(this.quantity.toString())
                    }
                }
            }
        })
    }

    fun setupView() {
        til_transferinput_barcode.isVisible = false
        cv_transfer_input_detail.isVisible = false
        btn_reset.apply {
            text = "Delete"
            setOnClickListener {
                when (historyType) {
                    SHIPMENT -> no?.let { no -> viewModel.deleteTransferShipmentEntry(no) }
                    RECEIPT -> no?.let { no -> viewModel.deleteTransferReceiptEntry(no) }
                    PURCHASE -> no?.let { no -> viewModel.deletePurchaseOrderEntry(no) }
                }

            }
        }
        btn_save.apply {
            text = "Update"
            setOnClickListener {
                when (historyType) {
                    SHIPMENT -> no?.let { no ->
                        viewModel.updateTransferShipmentEntry(no,
                            et_tranferinput_qty.text.toString().toInt())
                    }
                    RECEIPT -> no?.let { no ->
                        viewModel.updateTransferReceiptEntry(no,
                            et_tranferinput_qty.text.toString().toInt())
                    }
                    PURCHASE -> {
                        no?.let { no ->
                            viewModel.updatePurchaseInputData(no,
                                et_tranferinput_qty.text.toString().toInt())
                        }
                    }
                }
            }
        }
        iv_picking_history_close.setOnClickListener {
            dismiss()
        }
    }

    override fun onHistorySelectDelete(value: TransferInputData) {
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

    companion object {
        fun newInstance(id: Int, historyType: HistoryType): TransferHistoryBottomSheet {
            val argument = Bundle().apply {
                putInt(ARGS_TRANSFER_ID, id)
                putSerializable(ARGS_HISTORY_TYPE, historyType)
            }
            return TransferHistoryBottomSheet().apply {
                arguments = argument
            }
        }

        const val ARGS_TRANSFER_ID = "id_input_transfer"
        const val ARGS_HISTORY_TYPE = "history_type"
    }


}