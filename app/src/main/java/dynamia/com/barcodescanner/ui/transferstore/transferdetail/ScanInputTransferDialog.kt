package dynamia.com.barcodescanner.ui.transferstore.transferdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.databinding.ItemInputHeaderBinding
import dynamia.com.barcodescanner.ui.transferstore.TransferType
import dynamia.com.core.util.showLongToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScanInputTransferDialog : BottomSheetDialogFragment() {
    val viewModel: TransferDetailViewModel by viewModel()

    private val documentNo by lazy { arguments?.getString(ARGS_DOCUMENT_NO) }
    private val inputType by lazy { arguments?.getSerializable(ARGS_INPUT_TYPE) as TransferType }

    private lateinit var _viewBinding: ItemInputHeaderBinding
    val viewBinding by lazy { _viewBinding }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
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
            etTransferInputBarcode.requestFocus()

            when (inputType) {
                TransferType.STOCKOPNAME -> {
                    etTransferInputBarcode.doAfterTextChanged {
                        etTransferinputBincode.requestFocus()
                    }
                    etTransferinputBincode.doAfterTextChanged {
                        documentNo?.let { data ->
                            viewModel.insertDataValue(data,
                                etTransferinputBincode.text.toString(),
                                inputType, it.toString())
                        }
                    }

                }
                else -> {
                    etTransferInputBarcode.doAfterTextChanged {
                        documentNo?.let { data ->
                            viewModel.insertDataValue(data,
                                it.toString(),
                                inputType)
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
                    context?.showLongToast(it.message)
                }
                TransferDetailViewModel.TransferDetailInputViewState.ErrorSaveData -> {
                    context?.showLongToast(getString(R.string.qty_alreadyscan_qty_fromline_error_mssg))
                }
                TransferDetailViewModel.TransferDetailInputViewState.SuccessSaveData -> {
                    with(viewBinding) {
                        etTransferinputBincode.apply {
                            text?.clear()
                        }
                        etTransferInputBarcode.apply {
                            text?.clear()
                            requestFocus()
                        }
                        context?.showLongToast("Success Save Data")
                    }
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