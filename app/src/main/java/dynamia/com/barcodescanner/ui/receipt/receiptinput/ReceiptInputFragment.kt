package dynamia.com.barcodescanner.ui.receipt.receiptinput

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.pickinglist.adapter.LocalHistoryAdapter
import dynamia.com.barcodescanner.ui.receipt.adapter.ImportHistoryAdapter
import dynamia.com.barcodescanner.ui.receipt.adapter.ReceiptMultipleImportLineAdapter
import dynamia.com.barcodescanner.ui.receipt.adapter.ReceiptMultipleLocalLineAdapter
import dynamia.com.core.data.model.ReceiptImportLineValue
import dynamia.com.core.data.model.ReceiptImportScanEntriesValue
import dynamia.com.core.data.model.ReceiptLocalLineValue
import dynamia.com.core.data.model.ReceiptLocalScanEntriesValue
import dynamia.com.core.util.*
import kotlinx.android.synthetic.main.dialog_multiple_item.*
import kotlinx.android.synthetic.main.dialog_part_no_not_found.*
import kotlinx.android.synthetic.main.receipt_form_item.*
import kotlinx.android.synthetic.main.receipt_input_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReceiptInputFragment : Fragment(),
    ReceiptMultipleImportLineAdapter.OnMultipleImportLineListener,
    ReceiptMultipleLocalLineAdapter.OnMultipleLocalLineListener {

    private val viewModel: ReceiptInputViewModel by viewModel()
    private val args: ReceiptInputFragmentArgs by navArgs()
    private var dialog: Dialog? = null
    private var partNoDialog: Dialog? = null
    private var snNoDialog: Dialog? = null
    private var lineNo = 0
    private var receiptImportScanEntriesValue: ReceiptImportScanEntriesValue? = null
    private var receiptLocalScanEntriesValue: ReceiptLocalScanEntriesValue? = null
    private var historyImportAdapter = ImportHistoryAdapter(mutableListOf())
    private var historyLocalAdapter = LocalHistoryAdapter(mutableListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.receipt_input_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupView()
        setupListener()
        setObseverable()
    }

    private fun setObseverable() {
        when (args.source) {
            Constant.RECEIPT_IMPORT -> {
                viewModel.receiptImportRepository.getReceiptImportScanEntries(args.documentNo, 5)
                    .observe(viewLifecycleOwner,
                        Observer {
                            historyImportAdapter.update(it.toMutableList())
                        })
            }
            Constant.RECEIPT_LOCAL -> {
                viewModel.receiptLocalRepository.getReceiptLocalScanEntries(args.documentNo, 5)
                    .observe(viewLifecycleOwner,
                        Observer {
                            historyLocalAdapter.update(it.toMutableList())
                        })
            }
        }
        viewModel.receiptInputViewState.observe(viewLifecycleOwner, {
            when (it) {
                is ReceiptInputViewModel.ReceiptInputViewState.SuccessGetImportLine -> {
                    checkDBImport(dataImport = it.data)
                }
                is ReceiptInputViewModel.ReceiptInputViewState.SuccessGetLocalLine -> {
                    checkDBLocal(dataLocal = it.data)
                }
                is ReceiptInputViewModel.ReceiptInputViewState.ErrorGetReceiptLine -> {
                    context?.showLongToast(it.message)
                }
                is ReceiptInputViewModel.ReceiptInputViewState.CheckLocalSNResult -> {
                    createLocalScanEntry()
                    receiptLocalScanEntriesValue?.let { data ->
                        if (it.result) {
                            val input =
                                viewModel.receiptLocalRepository.insertReceiptLocalScanEntries(data)
                            if (input) {
                                context?.showLongToast(getString(R.string.success_save_data_local))
                                if (et_mac_address.isEmpty().not()) {
                                    clearSnAndMac()
                                } else {
                                    clearSN()
                                }
                            } else {
                                context?.showLongToast(getString(R.string.error_qty_over_outstanding))
                            }
                        } else {
                            showErroSnDialog(getString(R.string.sn_no_already_inputed))
                        }
                    }
                }
                is ReceiptInputViewModel.ReceiptInputViewState.CheckImportSNResult -> {
                    createImportScanEntry()
                    receiptImportScanEntriesValue?.let { data ->
                        if (it.result) {
                            val input =
                                viewModel.receiptImportRepository.insertReceiptImportScanEntries(
                                    data
                                )
                            if (input) {
                                context?.showLongToast(getString(R.string.success_save_data_local))
                                if (et_mac_address.isEmpty().not()) {
                                    clearSnAndMac()
                                } else {
                                    clearSN()
                                }
                            } else {
                                context?.showLongToast(getString(R.string.error_qty_over_outstanding))
                            }
                        } else {
                            showErroSnDialog(getString(R.string.sn_no_already_inputed))
                        }
                    }
                }
            }
        })
    }


    private fun setupView() {
        et_packingid.requestFocus()
        tv_receipt_detail_po.text = args.poNo
        et_part_no.addTextWatcher(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().length > 3) {
                    when (args.source) {
                        Constant.RECEIPT_LOCAL -> {
                            viewModel.getReceiptLocalLine(args.documentNo, p0.toString())
                        }

                        Constant.RECEIPT_IMPORT -> {
                            viewModel.getReceiptImportLine(args.documentNo, p0.toString())
                        }
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        })
        et_po_no.addTextWatcher(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty()) {
                    if (checkPONo(p0.toString().checkFirstCharacter("K")).not()) {
                        context?.showLongToast(getString(R.string.error_po_no_not_same))
                        et_po_no.clearText()
                        et_po_no.requestFocus()
                    }
                }
            }
        })
        et_sn_no.addTextWatcher(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().length > 3)
                    saveData()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
        when (args.source) {
            Constant.RECEIPT_IMPORT -> {
                with(rv_receipt_history) {
                    layoutManager =
                        LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
                    adapter = historyImportAdapter
                }
            }
            Constant.RECEIPT_LOCAL -> {
                with(rv_receipt_history) {
                    layoutManager =
                        LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
                    adapter = historyLocalAdapter
                }
            }
        }
    }

    fun checkPONo(value: String): Boolean {
        return args.poNo?.let { value == it } ?: kotlin.run { value == args.documentNo }
    }

    override fun onPause() {
        super.onPause()
        when (args.source) {
            Constant.RECEIPT_LOCAL -> {
                createLocalScanEntry()
            }
            Constant.RECEIPT_IMPORT -> {
                createImportScanEntry()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        clearAllText()
    }

    fun checkDBImport(dataImport: List<ReceiptImportLineValue>? = null) {
        dataImport?.let { data ->
            if (data.isNotEmpty()) {
                if (data.size == 1) {
                    displayAutocompleteDataImport(dataImport = data[0])
                } else {
                    showMultipleDataDialog(dataImport = data)
                }
            } else {
                showErrorPoNoDialog()
            }
        }
    }

    fun checkDBLocal(dataLocal: List<ReceiptLocalLineValue>? = null) {
        dataLocal?.let { data ->
            if (data.isNotEmpty()) {
                if (data.size == 1) {
                    displayAutocompleteDataLocal(dataLocal = data[0])
                } else {
                    showMultipleDataDialog(dataLocal = data)
                }
            } else {
                showErrorPoNoDialog()
            }
        }
    }

    private fun displayAutocompleteDataImport(dataImport: ReceiptImportLineValue? = null) {
        dataImport?.let { data ->
            et_description.setText(data.description)
            et_item_no.setText(data.itemNo)
            lineNo = data.lineNo
        }

    }

    private fun displayAutocompleteDataLocal(dataLocal: ReceiptLocalLineValue? = null) {
        dataLocal?.let { data ->
            et_description.setText(data.description)
            et_item_no.setText(data.no)
            lineNo = data.lineNo
        }
    }

    private fun showMultipleDataDialog(
        dataLocal: List<ReceiptLocalLineValue>? = null,
        dataImport: List<ReceiptImportLineValue>? = null
    ) {
        context?.let { context ->
            dialog = Dialog(context)
            dialog?.let { dialog ->
                with(dialog) {
                    setContentView(R.layout.dialog_multiple_item)
                    window
                        ?.setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    dataImport?.let { data ->
                        val adapter = ReceiptMultipleImportLineAdapter(
                            data.toMutableList(),
                            this@ReceiptInputFragment
                        )
                        rv_muliple_line.layoutManager =
                            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                        rv_muliple_line.adapter = adapter
                        show()
                    }
                    dataLocal?.let { data ->
                        val adapter = ReceiptMultipleLocalLineAdapter(
                            data.toMutableList(),
                            this@ReceiptInputFragment
                        )
                        rv_muliple_line.layoutManager =
                            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                        rv_muliple_line.adapter = adapter
                        show()
                    }
                }
            }

        }
    }

    private fun setupListener() {
        cv_save_new.setOnClickListener {
            saveData()
        }
        cv_clear_data_recipt.setOnClickListener {
            clearAllText()
            et_packingid.requestFocus()
        }
        cv_back_and_save.setOnClickListener {
            view?.findNavController()?.popBackStack()
        }
        tb_receipt_history.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.history_data -> {
                    val action =
                        ReceiptInputFragmentDirections.actionReceiptInputFragmentToHistoryInputFragment(
                            args.documentNo, args.source, showAll = false
                        )
                    view?.findNavController()?.navigate(action)
                    true
                }
                else -> false
            }
        }
    }

    private fun saveData() {
        if (checkMandatoryData()) {
            when (args.source) {
                Constant.RECEIPT_LOCAL -> {
                    viewModel.checkLocalSN(et_sn_no.getTextAsString())
                }
                Constant.RECEIPT_IMPORT -> {
                    viewModel.checkImportSn(et_sn_no.getTextAsString())
                }
            }
        }
    }

    private fun clearSnAndMac() {
        et_sn_no.clearText()
        et_mac_address.clearText()
        et_mac_address.requestFocus()
    }

    private fun checkMandatoryData(): Boolean {
        var result = true
        if (et_packingid.isEmpty()) {
            result = false
            et_packingid.setError(getString(R.string.error_input_message))
        }
        if (et_po_no.isEmpty()) {
            result = false
            et_po_no.setError(getString(R.string.error_input_message))
        } else if (checkPONo(et_po_no.getTextAsString().checkFirstCharacter("K")).not()) {
            result = false
            et_po_no.setError(getString(R.string.error_po_no_not_same))
        }
        if (et_part_no.isEmpty()) {
            result = false
            et_part_no.setError(getString(R.string.error_input_message))
        }
        if (et_sn_no.isEmpty()) {
            result = false
            et_sn_no.setError(getString(R.string.error_input_message))
        }
        if (et_trackingid.isEmpty()) {
            result = false
            et_trackingid.setError(getString(R.string.error_input_message))
        }
        if (et_description.isEmpty()) {
            result = false
            et_description.setError(getString(R.string.error_input_message))
        }
        if (et_item_no.isEmpty()) {
            result = false
            et_item_no.setError(getString(R.string.error_input_message))
        }
        return result
    }

    private fun setDataLocal(dataLocal: ReceiptLocalScanEntriesValue?) {
        dataLocal?.let { data ->
            et_mac_address.setText(data.macAddress)
            et_part_no.setText(data.partNo)
            et_packingid.setText(data.packingID)
            et_po_no.setText(data.pONo)
            et_sn_no.setText(data.serialNo)
            et_trackingid.setText(data.trackingID)
            lineNo = data.lineNo
        }
    }

    private fun setDataImport(dataImport: ReceiptImportScanEntriesValue?) {
        dataImport?.let { data ->
            et_mac_address.setText(data.macAddress)
            et_part_no.setText(data.partNo)
            et_packingid.setText(data.packingID)
            et_po_no.setText(data.pONo)
            et_sn_no.setText(data.serialNo)
            et_trackingid.setText(data.trackingID)
            lineNo = data.lineNo
        }
    }

    private fun createLocalScanEntry() {
        receiptLocalScanEntriesValue = ReceiptLocalScanEntriesValue(
            documentNo = args.documentNo,
            employeeCode = viewModel.getEmployeeName(),
            macAddress = et_mac_address.getTextAsString().emptySetZero(),
            partNo = et_part_no.getTextAsString(),
            packingID = et_packingid.getTextAsString(),
            shipset = "-",
            serialNo = et_sn_no.getTextAsString(),
            lineNo = lineNo,
            date = "${context?.getCurrentDate()}T${context?.getCurrentTime()}",
            time = "${context?.getCurrentTime()}",
            trackingID = et_trackingid.getTextAsString(),
            pONo = et_po_no.getTextAsString()
        )
    }

    private fun createImportScanEntry() {
        receiptImportScanEntriesValue = ReceiptImportScanEntriesValue(
            documentNo = args.documentNo,
            employeeCode = viewModel.getEmployeeName() ?: "",
            macAddress = et_mac_address.getTextAsString().emptySetZero(),
            partNo = et_part_no.getTextAsString(),
            packingID = et_packingid.getTextAsString(),
            shipset = "-",
            serialNo = et_sn_no.getTextAsString(),
            lineNo = lineNo,
            date = "${context?.getCurrentDate()}T${context?.getCurrentTime()}",
            time = "${context?.getCurrentTime()}",
            trackingID = et_trackingid.getTextAsString(),
            pONo = et_po_no.getTextAsString()
        )
    }

    override fun onMultipleImportLineSelected(data: ReceiptImportLineValue) {
        displayAutocompleteDataImport(dataImport = data)
        dialog?.dismiss()
    }

    override fun onMultipleLocalLineSelected(data: ReceiptLocalLineValue) {
        displayAutocompleteDataLocal(dataLocal = data)
        dialog?.dismiss()
    }

    private fun clearAllText() {
        et_packingid.clearText()
        et_po_no.clearText()
        et_part_no.clearText()
        et_sn_no.clearText()
        et_trackingid.clearText()
        et_mac_address.clearText()
        et_description.clearText()
        et_item_no.clearText()
    }

    private fun showErrorPoNoDialog() {
        context?.let { context ->
            partNoDialog = Dialog(context)
            partNoDialog?.let { dialog ->
                with(dialog) {
                    setContentView(R.layout.dialog_part_no_not_found)
                    window
                        ?.setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    btn_ok.setOnClickListener {
                        dismiss()
                        clearPartNo()
                    }
                    show()
                }
            }

        }
    }

    private fun showErroSnDialog(message: String) {
        context?.let {
            snNoDialog = Dialog(it)
            snNoDialog?.let { dialog ->
                with(dialog) {
                    setContentView(R.layout.dialog_part_no_not_found)
                    window
                        ?.setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    tv_error_message.text = message
                    btn_ok.setOnClickListener {
                        dismiss()
                        clearSnAndFocus()
                    }
                    show()
                }
            }
        }
    }

    private fun clearPartNo() {
        et_part_no.clearText()
        et_part_no.requestFocus()
    }


    private fun clearSN() {
        et_sn_no.clearText()
    }

    private fun clearSnAndFocus() {
        clearSN()
        et_sn_no.requestFocus()
    }

}
