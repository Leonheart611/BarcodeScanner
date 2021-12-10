package dynamia.com.barcodescanner.ui.binreclass.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dynamia.com.barcodescanner.databinding.InputRebinClassDialogBinding
import dynamia.com.barcodescanner.ui.binreclass.detail.BinreclassInputDialog.ADD_TYPE.MANUAL
import dynamia.com.barcodescanner.ui.binreclass.detail.BinreclassInputDialog.ADD_TYPE.SCAN
import dynamia.com.core.data.entinty.BinreclassInputData
import dynamia.com.core.util.EventObserver
import dynamia.com.core.util.gone
import dynamia.com.core.util.showLongToast

@AndroidEntryPoint
class BinreclassInputDialog : BottomSheetDialogFragment() {
    private var _viewBinding: InputRebinClassDialogBinding? = null
    val viewBinding by lazy { _viewBinding!! }

    val viewModel: BinreclassDetailViewModel by viewModels()
    private val fromBin by lazy { arguments?.getString(FROMBINCODE) }
    private val toBin by lazy { arguments?.getString(TOBINCODE) }
    private val idInput by lazy { arguments?.getInt(INPUTID) ?: 0 }
    private val addType by lazy { arguments?.getSerializable(ADDTYPE) as ADD_TYPE }

    private var animateDuration: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        animateDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
        _viewBinding = InputRebinClassDialogBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setObseverable()
        setClicklistener()
    }

    fun setupView() {
        with(viewBinding) {
            when (addType) {
                SCAN -> {
                    includeInputForm.tilTransferBincode.isVisible = false
                    includeInputForm.tvTransferItemName.text = "From Bin: $fromBin"
                    includeInputForm.tvTransferQty.text = "To Bin: $toBin"
                    includeInputForm.tilTransferinputName.isVisible = false
                    includeInputForm.btnReset.text = "Cancel"
                    fabDeleteRebinClass.gone()
                    includeInputForm.btnSave.isVisible = false
                    includeInputForm.etTranferinputQty.setText("1")
                    includeInputForm.etTranferinputQty.isEnabled = false
                    includeInputForm.etTransferInputBarcode.doAfterTextChanged {
                        if (includeInputForm.etTransferInputBarcode.text.toString().isNotEmpty()) {
                            viewModel.checkUserInputValidation(
                                includeInputForm.etTransferInputBarcode.text.toString(),
                                includeInputForm.etTranferinputQty.text.toString(),
                                fromBin ?: "",
                                toBin ?: "",
                                includeInputForm.etBoxInput.text.toString()
                            )
                        }
                    }
                }
                MANUAL -> {
                    includeInputForm.tilTransferBincode.isVisible = false
                    includeInputForm.tvTransferItemName.text = "From Bin: $fromBin"
                    includeInputForm.tvTransferQty.text = "To Bin: $toBin"
                    includeInputForm.tilTransferinputName.isVisible = false
                    includeInputForm.btnReset.text = "Cancel"
                    if (idInput == 0) {
                        fabDeleteRebinClass.gone()
                    } else {
                        viewModel.getLocalInputdata(idInput)
                        fabDeleteRebinClass.show()
                        includeInputForm.btnSave.text = "Update"
                    }
                }
            }

        }
    }

    private fun setObseverable() {
        viewModel.inputValidation.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                BinreclassDetailViewModel.InputValidation.BarcodeEmpty -> {
                    context?.showLongToast("Empty Barcode")
                }
                BinreclassDetailViewModel.InputValidation.QtyEmpty -> {
                    context?.showLongToast("Empty QTY")
                }
            }
        })
        viewModel.inputRebinData.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                is BinreclassDetailViewModel.InputReclassViewState.OnErrorSaveData -> {
                    context?.showLongToast(it.error)
                }
                BinreclassDetailViewModel.InputReclassViewState.SuccessSaveData -> {
                    context?.showLongToast("Success Save Data")
                    when (addType) {
                        SCAN -> {
                            with(viewBinding.includeInputForm) {
                                etTransferInputBarcode.text?.clear()
                                etTransferInputBarcode.requestFocus()
                            }
                        }
                        MANUAL -> clearData()
                    }
                }
                BinreclassDetailViewModel.InputReclassViewState.SuccessUpdateData -> {
                    context?.showLongToast("Success Update Data")
                    dismiss()
                }
                is BinreclassDetailViewModel.InputReclassViewState.SuccessGetHistoryData -> {
                    setupHistoryData(it.data)
                }
                BinreclassDetailViewModel.InputReclassViewState.SuccessDeleteData -> {
                    context?.showLongToast("Success Delete Data")
                    when (addType) {
                        SCAN -> {
                            with(viewBinding.includeInputForm) {
                                etTransferInputBarcode.text?.clear()
                                etTransferInputBarcode.requestFocus()
                            }
                        }
                        MANUAL -> {
                            clearData()
                            dismiss()
                        }
                    }

                }
            }
        })
    }

    private fun clearData() {
        with(viewBinding.includeInputForm) {
            etTransferInputBarcode.text?.clear()
            etTranferinputQty.text?.clear()
            etTransferInputBarcode.requestFocus()
        }
    }

    private fun setupHistoryData(data: BinreclassInputData) {
        with(viewBinding.includeInputForm) {
            etTransferInputBarcode.isFocusable = false
            etTransferInputBarcode.setText(data.itemIdentifier)
            etBoxInput.setText(data.box)
            etTranferinputQty.setText(data.quantity.toString())
        }
    }

    private fun setClicklistener() {
        with(viewBinding) {
            includeInputForm.btnSave.setOnClickListener {
                if (idInput == 0) {
                    viewModel.checkUserInputValidation(
                        barcode = includeInputForm.etTransferInputBarcode.text.toString(),
                        qty = includeInputForm.etTranferinputQty.text.toString(),
                        fromBin = fromBin ?: "",
                        toBin = toBin ?: "",
                        box = includeInputForm.etBoxInput.text.toString()
                    )
                } else {
                    viewModel.updateDataBin(
                        idInput,
                        includeInputForm.etTranferinputQty.text.toString().toInt()
                    )
                }
            }
            fabDeleteRebinClass.setOnClickListener {
                viewModel.deleteDataBin(idInput)
            }
            includeInputForm.btnReset.setOnClickListener {
                dismiss()
            }
        }
    }

    companion object {
        fun newInstance(
            toBin: String,
            fromBin: String,
            id: Int = 0,
            addType: ADD_TYPE
        ): BinreclassInputDialog {
            val argument = Bundle().apply {
                putString(FROMBINCODE, fromBin)
                putString(TOBINCODE, toBin)
                putInt(INPUTID, id)
                putSerializable(ADDTYPE, addType)
            }
            return BinreclassInputDialog().apply {
                arguments = argument
            }
        }

        const val FROMBINCODE = "from_bin_code"
        const val TOBINCODE = "to_bin_code"
        const val INPUTID = "input_id"
        const val ADDTYPE = "add_type"
    }

    enum class ADD_TYPE {
        SCAN, MANUAL
    }
}