package dynamia.com.barcodescanner.ui.transferstore.transferdetail

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AnimRes
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.databinding.DialogPartNoNotFoundBinding
import dynamia.com.barcodescanner.databinding.ItemInputHeaderBinding
import dynamia.com.barcodescanner.databinding.RefreshWarningDialogBinding
import dynamia.com.barcodescanner.ui.transferstore.TransferType
import dynamia.com.core.util.showLongToast

@AndroidEntryPoint
class ScanInputTransferDialog : BottomSheetDialogFragment() {
    val viewModel: TransferDetailViewModel by viewModels()

    private val documentNo by lazy { arguments?.getString(ARGS_DOCUMENT_NO) }
    private val inputType by lazy { arguments?.getSerializable(ARGS_INPUT_TYPE) as TransferType }

    private lateinit var _viewBinding: ItemInputHeaderBinding
    val viewBinding by lazy { _viewBinding }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _viewBinding = ItemInputHeaderBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setObserverable()
    }

    fun setupView() {
        with(viewBinding) {
            btnSave.isVisible = false
            btnReset.isVisible = false
            cvTransferInputDetail.isVisible = false
            tilTransferinputName.isVisible = false
            etTranferinputQty.setText("1")
            etTranferinputQty.isEnabled = false
            tilTransferBincode.isVisible = inputType == TransferType.STOCKOPNAME
            etBoxInput.requestFocus()
            etBoxInput.doAfterTextChanged {
                etTransferInputBarcode.requestFocus()
            }
            when (inputType) {
                TransferType.STOCKOPNAME -> {
                    etTransferInputBarcode.doAfterTextChanged {
                        etTransferinputBincode.requestFocus()
                    }
                    etTransferinputBincode.doAfterTextChanged {
                        if (!it.isNullOrEmpty()) {
                            documentNo?.let { data ->
                                viewModel.insertDataValue(
                                    data,
                                    etTransferinputBincode.text.toString(),
                                    inputType, it.toString(),
                                    etBoxInput.text.toString()
                                )
                            }
                        }
                    }
                }
                else -> {
                    etTransferInputBarcode.doAfterTextChanged {
                        if (it.isNullOrEmpty().not()) {
                            documentNo?.let { data ->
                                viewModel.insertDataValue(
                                    data,
                                    it.toString(),
                                    inputType,
                                    box = etBoxInput.text.toString()
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setObserverable() {
        viewModel.transferInputViewState.observe(viewLifecycleOwner, {
            when (it) {
                is TransferDetailViewModel.TransferDetailInputViewState.ErrorGetData -> {
                    showDialog {
                        with(viewBinding) {
                            etTransferInputBarcode.text?.clear()
                            etTransferInputBarcode.requestFocus()
                        }
                    }
                }
                TransferDetailViewModel.TransferDetailInputViewState.ErrorSaveData -> {
                    context?.showLongToast(getString(R.string.qty_alreadyscan_qty_fromline_error_mssg))
                }
                TransferDetailViewModel.TransferDetailInputViewState.SuccessSaveData -> {
                    with(viewBinding) {
                        etTransferinputBincode.text?.clear()
                        etTransferInputBarcode.text?.clear()
                        etTransferInputBarcode.requestFocus()
                        context?.showLongToast("Success Save Data")
                    }
                }
            }
        })
    }

    private fun showDialog(
        call: () -> Unit,
    ) {
        context?.let { context ->
            val dialog = Dialog(context)
            with(dialog) {
                val bind = DialogPartNoNotFoundBinding.inflate(layoutInflater)
                setContentView(bind.root)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                window
                    ?.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                with(bind) {
                    btnOk.setOnClickListener {
                        call()
                        dismiss()
                    }
                }
                show()
            }
        }
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