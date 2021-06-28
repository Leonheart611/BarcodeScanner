package dynamia.com.barcodescanner.ui.receipt.receiptinput

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.history.HistoryInputViewModel
import dynamia.com.barcodescanner.ui.history.adapter.HistoryInputImportAdapter
import dynamia.com.barcodescanner.ui.history.adapter.HistoryInputLocalAdapter
import dynamia.com.core.data.model.ReceiptImportScanEntriesValue
import dynamia.com.core.data.model.ReceiptLocalScanEntriesValue
import dynamia.com.core.util.Constant
import kotlinx.android.synthetic.main.bottom_sheet_picking_history_fragment.*
import kotlinx.android.synthetic.main.delete_confirmation_dialog.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReceiptHistoryBottomSheet : BottomSheetDialogFragment(),
    HistoryInputImportAdapter.OnImportClicklistener, HistoryInputLocalAdapter.OnLocalClicklistener {

    private val viewModel: HistoryInputViewModel by viewModel()
    private val historyImportAdapter = HistoryInputImportAdapter(mutableListOf(), this)
    private val historyLocalAdapter = HistoryInputLocalAdapter(mutableListOf(), this)

    private val receiptNo by lazy { arguments?.getString(ARGS_RECEIPT_NO) }
    private val receiptType by lazy { arguments?.getString(ARGS_RECEIPT_TYPE) }

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
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
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
        setupView()
        setObseverable()
    }

    private fun setObseverable() {
        when (receiptType) {
            Constant.RECEIPT_IMPORT -> {
                viewModel.receiptImportRepository.getReceiptImportScanEntries(receiptNo.toString())
                    .observe(viewLifecycleOwner, {
                        historyImportAdapter.updateData(it.toMutableList())
                    })
            }
            Constant.RECEIPT_LOCAL -> {
                viewModel.receiptLocalRepository.getReceiptLocalScanEntries(receiptNo.toString())
                    .observe(viewLifecycleOwner, {
                        historyLocalAdapter.updateData(it.toMutableList())
                    })
            }
        }
    }

    fun setupView() {
        when (receiptType) {
            Constant.RECEIPT_LOCAL -> {
                tv_history_title.text = getString(R.string.receipt_local_history_title)
                with(rv_picking_history) {
                    layoutManager =
                        LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
                    adapter = historyLocalAdapter
                }
            }
            Constant.RECEIPT_IMPORT -> {
                tv_history_title.text = getString(R.string.receipt_import_history_title)
                with(rv_picking_history) {
                    layoutManager =
                        LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
                    adapter = historyImportAdapter
                }
            }
        }
        iv_picking_history_close.setOnClickListener {
            dismiss()
        }
    }

    override fun onLocalClicklistener(value: ReceiptImportScanEntriesValue) {
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
                    viewModel.receiptImportRepository.deleteReceiptImportScanEntry(value)
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

    override fun onLocalClicklistener(value: ReceiptLocalScanEntriesValue) {
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
                    viewModel.receiptLocalRepository.deleteReceiptLocalScanEntry(value)
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
        const val ARGS_RECEIPT_NO = "args_receipt_no"
        const val ARGS_RECEIPT_TYPE = "args_receipt_type"
        fun newInstance(receiptId: String, receiptType: String): ReceiptHistoryBottomSheet {
            val argument = Bundle().apply {
                putString(ARGS_RECEIPT_NO, receiptId)
                putString(ARGS_RECEIPT_TYPE, receiptType)
            }
            return ReceiptHistoryBottomSheet().apply {
                arguments = argument
            }
        }
    }
}