package dynamia.com.barcodescanner.ui.pickinglist.pickinginput

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.pickinglist.adapter.InsertHistoryAdapter
import dynamia.com.barcodescanner.ui.pickinglist.adapter.PickingMultipleLineAdapter
import dynamia.com.core.data.model.PickingListLineValue
import dynamia.com.core.data.model.PickingListScanEntriesValue
import dynamia.com.core.util.*
import dynamia.com.core.util.Constant.PICKING_LIST
import kotlinx.android.synthetic.main.dialog_multiple_item.*
import kotlinx.android.synthetic.main.dialog_part_no_not_found.*
import kotlinx.android.synthetic.main.item_input_header.*
import kotlinx.android.synthetic.main.receiving_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class PickingListInputFragment : Fragment(), PickingMultipleLineAdapter.OnMultipleLineSelected {
    private val viewModel: PickingListInputViewModel by viewModel()
    private val args: PickingListInputFragmentArgs by navArgs()
    private var pickListValue: PickingListLineValue? = null
    private var inputHistoryAdapter = InsertHistoryAdapter(mutableListOf())
    private var dialog: Dialog? = null
    private var poNoDialog: Dialog? = null
    private var purchaseNo: String = ""
    private val DELAY: Long = 2000
    private var pickingListValue: PickingListScanEntriesValue? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.receiving_fragment, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pickingListValue = getPickingScanEntriesModel()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupView()
    }

    override fun onResume() {
        super.onResume()
        pickingListValue?.let { setAllDataView(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
        setupObserverable()
    }

    private fun setupObserverable() {
        viewModel.pickingListRepository.getPickingListScanEntries(args.pickingListNo, 5)
            .observe(viewLifecycleOwner, {
                inputHistoryAdapter.update(it.toMutableList())
            })
        viewModel.pickingInputViewState.observe(viewLifecycleOwner, {
            when (it) {
                is PickingListInputViewModel.PickingInputViewState.SuccessGetValue -> {
                    checkOnDB(it.data)
                }
                is PickingListInputViewModel.PickingInputViewState.ErrorGetData -> {
                    context?.showLongToast(it.message)
                }
                is PickingListInputViewModel.PickingInputViewState.CheckSNResult -> {
                    if (it.boolean) {
                        val input = viewModel.pickingListRepository.insertPickingListScanEntries(
                            getPickingScanEntriesModel()
                        )
                        if (input) {
                            context?.showLongToast(getString(R.string.success_save_data_local))
                            if (et_mac_address_picking.isEmpty()) {
                                clearSn()
                            } else {
                                clearSnAndMac()
                            }
                        } else {
                            context?.showLongToast(getString(R.string.error_qty_over_outstanding))
                            et_sn_picking.clearText()
                            et_sn_picking.requestFocus()
                        }
                    } else {
                        context?.showLongToast(getString(R.string.error_sn_on_same_pickinglistno))
                        et_sn_picking.clearText()
                        et_sn_picking.requestFocus()
                    }
                }
            }
        })
    }

    private fun setupView() {
        tv_picking_detail_so.text = getString(R.string.picklistno_title, args.pickingListNo)
        with(rv_history_input) {
            layoutManager =
                LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false);
            adapter = inputHistoryAdapter
        }
        et_part_no.requestFocus()
    }

    private fun setupListener() {
        cv_back.setOnClickListener {
            view?.findNavController()?.popBackStack()
        }
        et_part_no.addTextWatcher(object : TextWatcher {
            private var timer = Timer()

            override fun afterTextChanged(p0: Editable?) {
                if (switch_manual_picking.isChecked) {
                    if (et_part_no.getTextLength() > 3) {
                        viewModel.getPickingListLineValue(
                            partNo = et_part_no.getTextAsString(),
                            pickingListNo = args.pickingListNo
                        )
                    }
                } else {
                    timer.cancel()
                    timer = Timer()
                    timer.schedule(
                        object : TimerTask() {
                            override fun run() {
                                if (et_part_no.getTextLength() > 3) {
                                    viewModel.getPickingListLineValue(
                                        partNo = et_part_no.getTextAsString(),
                                        pickingListNo = args.pickingListNo
                                    )
                                }
                            }
                        },
                        DELAY
                    )
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })
        cv_save_new.setOnClickListener {
            saveDataLocal()
        }
        cv_clear_data.setOnClickListener {
            clearAllView()
            et_part_no.requestFocus()
        }
        toolbar_picking_list_input.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.history_data -> {
                    val action =
                        PickingListInputFragmentDirections.actionReceivingFragmentToHistoryInputFragment(
                            args.pickingListNo, PICKING_LIST
                        )
                    view?.findNavController()?.navigate(action)
                    true
                }
                else -> false
            }
        }
        et_sn_picking.addTextWatcher(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (switch_manual_picking.isChecked) {
                    if (p0.toString().length > 3)
                        saveDataLocal()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        et_po_no_picking.addTextWatcher(object : TextWatcher {
            private var timer = Timer()

            override fun afterTextChanged(p0: Editable?) {
                if (switch_manual_picking.isChecked) {
                    if (purchaseNo.isNotEmpty() && et_po_no_picking.isEmpty().not()) {
                        val currentPoNo =
                            et_po_no_picking.getTextAsString().checkFirstCharacter("K")
                        if (checkPONo(currentPoNo).not()) {
                            showErrorPONoDialog(getString(R.string.error_po_no_not_same))
                        } else {
                            et_note.requestFocus()
                        }
                    }
                } else {
                    timer.cancel()
                    timer = Timer()
                    timer.schedule(
                        object : TimerTask() {
                            override fun run() {
                                if (purchaseNo.isNotEmpty() && et_po_no_picking.isEmpty().not()) {
                                    val currentPoNo =
                                        et_po_no_picking.getTextAsString().checkFirstCharacter("K")
                                    if (checkPONo(currentPoNo).not()) {
                                        showErrorPONoDialog(getString(R.string.error_po_no_not_same))
                                    } else {
                                        et_note.requestFocus()
                                    }
                                }
                            }
                        },
                        DELAY
                    )
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        et_sn_picking.addSetOnEditorClickListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveDataLocal()
            }
            false
        }
    }

    private fun saveDataLocal() {
        if (checkMandatoryDataEmpty().not()) {
            when (args.validateS) {
                true -> {
                    if (et_sn_picking.getTextAsString().startsWith("S", true)) {
                        viewModel.checkSn(et_sn_picking.getTextAsString())
                    } else {
                        context?.showLongToast("SN Harus di awali dengan S")
                        clearSn()
                    }
                }
                false -> {
                    viewModel.checkSn(et_sn_picking.getTextAsString())
                }
            }
        }
    }

    private fun getPickingScanEntriesModel(): PickingListScanEntriesValue {
        return PickingListScanEntriesValue(
            documentNo = et_pl_no.getTextAsString(),
            lineNo = pickListValue?.lineNo ?: 0,
            partNo = et_part_no.getTextAsString(),
            serialNo = et_sn_picking.getTextAsString(),
            macAddress = et_mac_address_picking.getTextAsString().emptySetZero(),
            note = et_note.getTextAsString(),
            time = context?.getCurrentTime() ?: "",
            date = "${context?.getCurrentDate()}T${context?.getCurrentTime()}",
            employeeCode = viewModel.getEmployeeName(), qtyScan = "1",
            pickingListNo = args.pickingListNo
        )
    }

    private fun setAllDataView(data: PickingListScanEntriesValue) {
        with(data) {
            et_part_no.setText(partNo)
            et_pl_no.setText(documentNo)
            et_sn_picking.setText(serialNo)
            et_mac_address_picking.setText(macAddress)
            et_note.setText(note)
        }
    }

    private fun clearAllView() {
        et_part_no.clearText()
        et_sn_picking.clearText()
        et_mac_address_picking.clearText()
        et_so.clearText()
        et_description_picking.clearText()
        et_pl_no.clearText()
        et_note.clearText()
        et_item_no.clearText()
        et_po_no_picking.clearText()
    }

    private fun clearSn() {
        et_sn_picking.clearText()
    }

    fun checkOnDB(data: List<PickingListLineValue>) {
        if (data.isNotEmpty()) {
            if (data.size == 1) {
                displayAutocompleteData(data[0])
            } else {
                showMultipleDataDialog(data)
            }
        } else {
            showErrorPartNo()
        }
    }

    private fun displayAutocompleteData(data: PickingListLineValue) {
        with(data) {
            et_description_picking.setText(description)
            et_item_no.setText(no)
            et_so.setText(documentNo)
            et_pl_no.setText(pickingListNo)
        }
        purchaseNo = data.purchOrderNo
        pickListValue = data
        et_po_no_picking.requestFocus()
    }

    private fun checkPONo(poNO: String): Boolean {
        return (purchaseNo.isNotEmpty() && purchaseNo == poNO)
    }

    private fun checkMandatoryDataEmpty(): Boolean {
        var anyEmpty = false
        if (et_part_no.isEmpty()) {
            anyEmpty = true
            et_part_no.setError(getString(R.string.error_input_message))
        }
        if (et_sn_picking.isEmpty()) {
            anyEmpty = true
            et_sn_picking.setError(getString(R.string.error_input_message))
        }
        if (et_item_no.isEmpty()) {
            anyEmpty = true
            et_item_no.setError(getString(R.string.error_input_message))
        }
        if (et_description_picking.isEmpty()) {
            anyEmpty = true
            et_description_picking.setError(getString(R.string.error_input_message))
        }
        if (et_so.isEmpty()) {
            anyEmpty = true
            et_so.setError(getString(R.string.error_input_message))
        }
        if (et_pl_no.isEmpty()) {
            anyEmpty = true
            et_pl_no.setError(getString(R.string.error_input_message))
        }
        if (et_po_no_picking.isEmpty()) {
            anyEmpty = true
            et_po_no_picking.setError(getString(R.string.error_input_message))
        } else if (checkPONo(et_po_no_picking.getTextAsString().checkFirstCharacter("K")).not()) {
            anyEmpty = true
            et_po_no_picking.setError(getString(R.string.error_po_no_not_same))
        }

        if (et_part_no.isNotEmpty() && et_item_no.isEmpty() && et_so.isEmpty() && et_pl_no.isEmpty()) {
            context?.showShortToast(resources.getString(R.string.error_nopart_not_found))
        }
        return anyEmpty
    }

    private fun showMultipleDataDialog(data: List<PickingListLineValue>) {
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
                    val adapter = PickingMultipleLineAdapter(
                        data.toMutableList(),
                        this@PickingListInputFragment
                    )
                    rv_muliple_line.layoutManager =
                        LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                    rv_muliple_line.adapter = adapter

                    show()
                }
            }

        }
    }

    private fun showErrorPartNo() {
        context?.let { context ->
            poNoDialog = Dialog(context)
            poNoDialog?.let { dialog ->
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

    private fun showErrorPONoDialog(message: String) {
        context?.let { context ->
            poNoDialog = Dialog(context)
            poNoDialog?.let { dialog ->
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
                        clearPONo()
                    }
                    show()
                }
            }
        }
    }

    private fun clearSnAndMac() {
        et_mac_address_picking.clearText()
        et_mac_address_picking.requestFocus()
        clearSn()

    }

    private fun clearPartNo() {
        et_part_no.clearText()
        et_part_no.requestFocus()
    }

    private fun clearPONo() {
        et_po_no_picking.clearText()
        et_po_no_picking.requestFocus()
    }

    override fun onMultiplelineSelected(data: PickingListLineValue) {
        displayAutocompleteData(data)
        dialog?.dismiss()
    }

}
