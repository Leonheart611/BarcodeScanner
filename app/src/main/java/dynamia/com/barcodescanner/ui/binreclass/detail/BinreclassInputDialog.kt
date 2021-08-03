package dynamia.com.barcodescanner.ui.binreclass.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dynamia.com.barcodescanner.databinding.InputRebinClassDialogBinding
import dynamia.com.core.data.entinty.BinreclassInputData
import dynamia.com.core.util.EventObserver
import dynamia.com.core.util.gone
import dynamia.com.core.util.showLongToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class BinreclassInputDialog : BottomSheetDialogFragment() {
    private var _viewBinding: InputRebinClassDialogBinding? = null
    val viewBinding by lazy { _viewBinding!! }

    val viewModel: BinreclassDetailViewModel by viewModel()
    private val fromBin by lazy { arguments?.getString(FROMBINCODE) }
    private val toBin by lazy { arguments?.getString(TOBINCODE) }
    private val idInput by lazy { arguments?.getInt(INPUTID) ?: 0 }

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
                    clearData()
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
                    dismiss()
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
            etTransferInputBarcode.setText(data.itemNo)
            etTranferinputQty.setText(data.quantity.toString())
        }
    }

    private fun setClicklistener() {
        with(viewBinding) {
            includeInputForm.btnSave.setOnClickListener {
                if (idInput == 0) {
                    viewModel.checkUserInputValidation(
                        includeInputForm.etTransferInputBarcode.text.toString(),
                        includeInputForm.etTranferinputQty.text.toString(),
                        fromBin ?: "",
                        toBin ?: ""
                    )
                } else {
                    viewModel.updateDataBin(idInput,
                        includeInputForm.etTranferinputQty.text.toString().toInt())
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
        fun newInstance(toBin: String, fromBin: String, id: Int = 0): BinreclassInputDialog {
            val argument = Bundle().apply {
                putString(FROMBINCODE, fromBin)
                putString(TOBINCODE, toBin)
                putInt(INPUTID, id)
            }
            return BinreclassInputDialog().apply {
                arguments = argument
            }
        }

        const val FROMBINCODE = "from_bin_code"
        const val TOBINCODE = "to_bin_code"
        const val INPUTID = "input_id"
    }
}