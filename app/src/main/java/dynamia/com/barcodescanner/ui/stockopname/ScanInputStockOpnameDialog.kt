package dynamia.com.barcodescanner.ui.stockopname

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dynamia.com.barcodescanner.BuildConfig
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.databinding.DialogPartNoNotFoundBinding
import dynamia.com.barcodescanner.databinding.ItemStockopnameInputBinding
import dynamia.com.barcodescanner.ui.transferstore.TransferType
import dynamia.com.barcodescanner.ui.transferstore.transferdetail.TransferDetailViewModel
import dynamia.com.core.util.Constant
import dynamia.com.core.util.gone
import dynamia.com.core.util.showLongToast

@AndroidEntryPoint
class ScanInputStockOpnameDialog : BottomSheetDialogFragment() {
    val viewModel: TransferDetailViewModel by viewModels()

    private val documentNo by lazy { arguments?.getString(ARGS_DOCUMENT_NO) }
    private val inputType by lazy { arguments?.getSerializable(ARGS_INPUT_TYPE) as TransferType }

    private lateinit var _viewBinding: ItemStockopnameInputBinding
    val viewBinding by lazy { _viewBinding }
    private var mpFail: MediaPlayer? = null
    private var mpSuccess: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _viewBinding = ItemStockopnameInputBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
        mpFail = MediaPlayer.create(context, R.raw.error)
        mpSuccess = MediaPlayer.create(context, R.raw.correct_sound)
        setupView()
        setObserverable()
    }

    fun setupView() {
        with(viewBinding) {
            ivStockopnameClose.isVisible = true
            ivStockopnameClose.setOnClickListener {
                dialog?.dismiss()
            }
            btnSave.isVisible = false
            btnReset.isVisible = false
            cvTransferInputDetail.isVisible = false
            tilTransferinputName.isVisible = false
            etTranferinputQty.isEnabled = false


            when (BuildConfig.FLAVOR) {
                Constant.APP_STORE -> {
                    tilTransferBincode.gone()
                    etBoxInput.requestFocus()
                    etBoxInput.doAfterTextChanged {
                        etTransferInputBarcode.requestFocus()
                    }
                }

            }
            etTranferinputQty.setText("1")
            etTransferInputBarcode.doAfterTextChanged {
                if (!it.isNullOrEmpty()) {
                    documentNo?.let { data ->
                        viewModel.insertDataValue(
                            no = data,
                            identifier = it.toString(),
                            transferType = inputType,
                            binCode = etTransferinputBincode.text.toString(),
                            box = etBoxInput.text.toString()
                        )
                    }
                }
            }
            etTransferinputBincode.doAfterTextChanged {
                etTransferInputBarcode.requestFocus()
            }
            etBoxInput.doAfterTextChanged {
                etTransferinputBincode.requestFocus()
            }
        }
    }

    private fun setObserverable() {
        viewModel.transferInputViewState.observe(viewLifecycleOwner, {
            when (it) {
                is TransferDetailViewModel.TransferDetailInputViewState.ErrorGetData -> {
                    mpFail?.start()
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
                        etTransferInputBarcode.text?.clear()
                        etTransferInputBarcode.requestFocus()
                        context?.showLongToast("Success Save Data")
                        mpSuccess?.start()
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

    override fun onStop() {
        super.onStop()
        mpFail?.release()
        mpSuccess?.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        mpFail?.release()
        mpSuccess?.release()
    }


    companion object {
        private const val ARGS_DOCUMENT_NO = "args_document_no"
        private const val ARGS_INPUT_TYPE = "args_input_type"
        fun newInstance(
            documentNo: String,
            transferType: TransferType
        ): ScanInputStockOpnameDialog {
            val argument = Bundle().apply {
                putString(ARGS_DOCUMENT_NO, documentNo)
                putSerializable(ARGS_INPUT_TYPE, transferType)
            }
            return ScanInputStockOpnameDialog().apply {
                arguments = argument
            }
        }
    }


}