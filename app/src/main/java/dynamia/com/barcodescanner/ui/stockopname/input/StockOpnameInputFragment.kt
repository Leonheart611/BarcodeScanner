package dynamia.com.barcodescanner.ui.stockopname.input

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import dynamia.com.barcodescanner.BuildConfig
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.databinding.StockOpnameInputFragmentBinding
import dynamia.com.barcodescanner.ui.MainActivity
import dynamia.com.barcodescanner.ui.history.HistoryType
import dynamia.com.core.base.BaseFragmentBinding
import dynamia.com.core.data.entinty.StockOpnameData
import dynamia.com.core.util.Constant
import dynamia.com.core.util.showLongToast

@AndroidEntryPoint
class StockOpnameInputFragment :
    BaseFragmentBinding<StockOpnameInputFragmentBinding>(StockOpnameInputFragmentBinding::inflate) {
    var activity: MainActivity? = null
    private val args: StockOpnameInputFragmentArgs by navArgs()

    companion object {
        fun newInstance() = StockOpnameInputFragment()
    }

    private val viewModel: StockOpnameInputViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity = requireActivity() as MainActivity
        setupView()
        setupListener()
        setupObserverable()
    }

    fun setupView() {
        with(viewBinding) {
            toolbarPickingListInput.title = viewModel.getCompanyName()
            tvTransferInputTitle.text = getString(R.string.stock_opname_title)
            when (BuildConfig.FLAVOR) {
                Constant.APP_STORE -> {
                    includeTransferInput.tilTransferBincode.isVisible = false
                }
            }
            if (args.id != 0) {
                includeTransferInput.etTransferInputBarcode.setText(args.barcode)
                includeTransferInput.etTransferInputBarcode.isEnabled = false
                getPickingListLineData(args.barcode)
                includeTransferInput.etTranferinputQty.requestFocus()
            }
            includeTransferInput.etTransferInputBarcode.setOnEditorActionListener { _, keyCode, event ->
                if (((event?.action ?: -1) == KeyEvent.ACTION_DOWN)
                    || keyCode == EditorInfo.IME_ACTION_NEXT
                ) {
                    getPickingListLineData(
                        includeTransferInput.etTransferInputBarcode.text.toString(),
                        binCode = includeTransferInput.etTransferinputBincode.text.toString()
                    )
                    includeTransferInput.etTranferinputQty.requestFocus()
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

            includeTransferInput.etTransferinputBincode.setOnEditorActionListener { _, actionId, event ->
                if (((event?.action ?: -1) == KeyEvent.ACTION_DOWN)
                    || actionId == EditorInfo.IME_ACTION_NEXT
                ) {
                    includeTransferInput.etTransferInputBarcode.requestFocus()
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
        }
    }

    private fun showSuccessStockOpname(data: StockOpnameData) {
        with(viewBinding.includeTransferInput) {
            tvTransferItemName.text = data.itemNo
            tilTransferinputName.editText?.setText(data.itemNo)
            etTransferinputBincode.setText(data.binCode)
        }
    }

    private fun setupListener() {
        with(viewBinding) {
            toolbarPickingListInput.setOnClickListener { view?.findNavController()?.popBackStack() }
            includeTransferInput.btnReset.setOnClickListener {
                args.id.let { includeTransferInput.etTranferinputQty.text?.clear() }
                    ?: kotlin.run { clearData() }
            }
            includeTransferInput.btnSave.setOnClickListener {
                viewModel.checkUserInputValidation(
                    includeTransferInput.etTransferInputBarcode.text.toString(),
                    includeTransferInput.etTranferinputQty.text.toString(),
                    box = includeTransferInput.etBoxInput.text.toString(),
                    bin = includeTransferInput.etTransferinputBincode.text.toString()
                )
            }
            toolbarPickingListInput.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.history_data -> {
                        val action =
                            StockOpnameInputFragmentDirections.actionStockOpnameInputFragment2ToHistoryInputFragment(
                                documentNo = null,
                                historyType = HistoryType.STOCKOPNAME
                            )
                        view?.findNavController()?.navigate(action)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun setupObserverable() {
        with(viewModel) {
            viewState.observe(viewLifecycleOwner, {
                when (it) {
                    is StockOpnameInputViewModel.StockOpnameViewState.Error -> {
                        context?.showLongToast(it.message)
                    }
                    is StockOpnameInputViewModel.StockOpnameViewState.Loading -> {
                        activity?.showLoading(it.boolean)
                    }
                    is StockOpnameInputViewModel.StockOpnameViewState.SuccessGetValue -> {
                        showSuccessStockOpname(it.data)
                    }
                    StockOpnameInputViewModel.StockOpnameViewState.SuccessSaveValue -> {
                        context?.showLongToast("Success Save Data")
                    }
                }
            })
            inputValidation.observe(viewLifecycleOwner, {
                when (it) {
                    StockOpnameInputViewModel.InputValidation.BarcodeEmpty -> {
                        viewBinding.includeTransferInput.tilTransferinputBarcode.error =
                            "Must Fill this Field"
                    }
                    StockOpnameInputViewModel.InputValidation.QtyEmpty -> {
                        viewBinding.includeTransferInput.tilTransferinputQty.error =
                            "Must Fill this Field"
                    }
                }
            })
            stockOpnameResultQty.observe(viewLifecycleOwner, {
                viewBinding.includeTransferInput.tvTransferQty.text = "$it"
            })
        }
    }

    private fun getPickingListLineData(barcode: String, binCode: String = "") {
        viewModel.getStockOpnameValue(barcode, args.id, binCode)
    }

    private fun clearData() {
        with(viewBinding.includeTransferInput) {
            etTransferInputBarcode.text?.clear()
            etTransferinputName.text?.clear()
            etTranferinputQty.text?.clear()
            etTransferInputBarcode.requestFocus()
        }
    }


}