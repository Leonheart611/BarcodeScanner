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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.history.adapter.HistoryTransferInputAdapter
import dynamia.com.core.data.model.PickingListScanEntriesValue
import kotlinx.android.synthetic.main.bottom_sheet_picking_history_fragment.*
import kotlinx.android.synthetic.main.delete_confirmation_dialog.*
import org.koin.androidx.viewmodel.ext.android.viewModel

const val ARGS_PICKING_NO = "args_picking_no"

class TransferHistoryBottomSheet : BottomSheetDialogFragment(),
    HistoryTransferInputAdapter.OnHistorySelected {
    private val viewModel: TransferInputViewModel by viewModel()
    private var scanEntriesAdapter = HistoryTransferInputAdapter(mutableListOf(), this)
    private val no by lazy { arguments?.getString(ARGS_PICKING_NO) }

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
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_picking_history_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        no?.let { viewModel.getHistoryValue(it) }
        setupView()
        setObseverable()
    }

    private fun setObseverable() {
        viewModel.transferInputViewState.observe(viewLifecycleOwner, {
            when (it) {
                is TransferInputViewModel.TransferInputViewState.SuccessGetHistoryValue -> {
                    scanEntriesAdapter.updateData(it.data)
                }
            }
        })
    }

    fun setupView() {
        with(rv_picking_history) {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            adapter = scanEntriesAdapter
        }
        iv_picking_history_close.setOnClickListener {
            dismiss()
        }
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

    companion object {

        fun newInstance(pickingId: String): TransferHistoryBottomSheet {
            val argument = Bundle().apply {
                putString(ARGS_PICKING_NO, pickingId)
            }
            return TransferHistoryBottomSheet().apply {
                arguments = argument
            }
        }
    }


}