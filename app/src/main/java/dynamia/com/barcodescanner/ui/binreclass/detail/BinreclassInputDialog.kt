package dynamia.com.barcodescanner.ui.binreclass.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.entinty.BinreclassInputData
import dynamia.com.core.util.EventObserver
import dynamia.com.core.util.gone
import dynamia.com.core.util.showLongToast
import kotlinx.android.synthetic.main.input_rebin_class_dialog.*
import kotlinx.android.synthetic.main.item_input_header.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class BinreclassInputDialog : BottomSheetDialogFragment() {
    val viewModel: BinreclassDetailViewModel by viewModel()
    private val fromBin by lazy { arguments?.getString(FROMBINCODE) }
    private val toBin by lazy { arguments?.getString(TOBINCODE) }
    private val idInput by lazy { arguments?.getInt(INPUTID) ?: 0 }

    private var animateDuration: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        animateDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
        return inflater.inflate(R.layout.input_rebin_class_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setObseverable()
        setClicklistener()
    }

    fun setupView() {
        til_transfer_bincode.isVisible = false
        tv_transfer_item_name.text = "From Bin: $fromBin"
        tv_transfer_qty.text = "To Bin: $toBin"
        til_transferinput_name.isVisible = false
        btn_reset.text = "Cancel"
        if (idInput == 0) {
            fab_delete_rebin_class.gone()
        } else {
            viewModel.getLocalInputdata(idInput)
            fab_delete_rebin_class.show()
            btn_save.text = "Update"
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
        et_transfer_input_barcode.text?.clear()
        et_tranferinput_qty.text?.clear()
        et_transfer_input_barcode.requestFocus()
    }

    private fun setupHistoryData(data: BinreclassInputData) {
        et_transfer_input_barcode.isFocusable = false
        et_transfer_input_barcode.setText(data.itemNo)
        et_tranferinput_qty.setText(data.quantity.toString())
    }

    private fun setClicklistener() {
        btn_save.setOnClickListener {
            if (idInput == 0) {
                viewModel.checkUserInputValidation(
                    et_transfer_input_barcode.text.toString(),
                    et_tranferinput_qty.text.toString(), fromBin ?: "", toBin ?: ""
                )
            } else {
                viewModel.updateDataBin(idInput, et_tranferinput_qty.text.toString().toInt())
            }
        }
        fab_delete_rebin_class.setOnClickListener {
            viewModel.deleteDataBin(idInput)
        }
        btn_reset.setOnClickListener {
            dismiss()
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