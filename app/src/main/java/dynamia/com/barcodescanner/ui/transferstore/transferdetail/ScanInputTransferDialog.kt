package dynamia.com.barcodescanner.ui.transferstore.transferdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.transferstore.TransferType
import dynamia.com.core.util.showLongToast
import kotlinx.android.synthetic.main.item_input_header.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScanInputTransferDialog : BottomSheetDialogFragment() {
    val viewModel: TransferDetailViewModel by viewModel()

    private val documentNo by lazy { arguments?.getString(ARGS_DOCUMENT_NO) }
    private val inputType by lazy { arguments?.getSerializable(ARGS_INPUT_TYPE) as TransferType }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.item_input_header, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setObserverable()
    }

    fun setupView() {
        btn_save.isVisible = false
        btn_reset.isVisible = false
        cv_transfer_input_detail.isVisible = false
        til_transferinput_name.isVisible = false
        et_tranferinput_qty.setText("1")
        et_tranferinput_qty.isEnabled = false
        et_transfer_input_barcode.requestFocus()

        et_transfer_input_barcode.doAfterTextChanged {
            documentNo?.let { data -> viewModel.insertDataValue(data, it.toString(), inputType) }
        }
    }

    private fun setObserverable() {
        viewModel.transferInputViewState.observe(viewLifecycleOwner, {
            when (it) {
                is TransferDetailViewModel.TransferDetailInputViewState.ErrorGetData -> {
                    context?.showLongToast(it.message)
                }
                TransferDetailViewModel.TransferDetailInputViewState.ErrorSaveData -> {
                    context?.showLongToast(getString(R.string.qty_alreadyscan_qty_fromline_error_mssg))
                }
                TransferDetailViewModel.TransferDetailInputViewState.SuccessSaveData -> {
                    et_transfer_input_barcode.apply {
                        text?.clear()
                        requestFocus()
                    }
                    context?.showLongToast("Success Save Data")
                }
            }
        })
    }


    companion object {
        private const val ARGS_DOCUMENT_NO = "args_document_no"
        private const val ARGS_INPUT_TYPE = "args_input_type"
        fun newInstance(documentNo: String, transferType: TransferType): ScanInputTransferDialog {
            val argument = Bundle().apply {
                putString(ARGS_DOCUMENT_NO, documentNo)
                putSerializable(ARGS_INPUT_TYPE, transferType)
            }
            return ScanInputTransferDialog().apply {
                arguments = argument
            }
        }
    }


}