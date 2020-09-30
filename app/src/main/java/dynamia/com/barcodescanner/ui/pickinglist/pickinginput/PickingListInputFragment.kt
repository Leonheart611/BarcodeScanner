package dynamia.com.barcodescanner.ui.pickinglist.pickinginput

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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

class PickingListInputFragment : Fragment(), PickingMultipleLineAdapter.OnMultipleLineSelected {
    private val viewModel: PickingListInputViewModel by viewModel()
    private val args: PickingListInputFragmentArgs by navArgs()
    private var pickListValue: PickingListLineValue? = null
    private var inputHistoryAdapter = InsertHistoryAdapter(mutableListOf())
    private var dialog: Dialog? = null
    private var poNoDialog: Dialog? = null
    private var purchaseNo: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.receiving_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupView()
        setupListener()
        setupObserverable()
    }

    private fun setupObserverable() {
        viewModel.pickingListRepository.getPickingListScanEntries(args.pickingListNo, 5)
            .observe(viewLifecycleOwner,
                Observer {
                    inputHistoryAdapter.update(it.toMutableList())
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

    override fun onResume() {
        super.onResume()
        clearAllView()
    }

    private fun setupListener() {
        cv_back.setOnClickListener {
            view?.findNavController()?.popBackStack()
        }
        et_part_no.addTextWatcher(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (et_part_no.getTextLength() > 3) {
                    val data = viewModel.pickingListRepository.getAllPickingListLineFromInsert(
                        partNo = et_part_no.getTextAsString(),
                        picking_List_No = args.pickingListNo
                    )
                    checkOnDB(data)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })
        cv_save_new.setOnClickListener {
            saveDataLocal(false)
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
                if (p0.toString().length > 3)
                    saveDataLocal(true)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        et_po_no_picking.addTextWatcher(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (purchaseNo.isNotEmpty()) {
                    if (checkPONo(et_po_no_picking.getTextAsString()).not()) {
                        et_po_no_picking.setError(getString(R.string.error_po_no_not_same))
                    }
                }
            }
        })
    }


    private fun saveDataLocal(clearSnAndMac: Boolean) {
        if (checkMandatoryDataEmpty().not()) {
            if (viewModel.pickingListRepository.checkPickingListNoandSN(
                    args.pickingListNo,
                    et_sn_picking.getTextAsString(),
                    partNo = et_part_no.getTextAsString()
                )
            ) {
                val input = viewModel.pickingListRepository.insertPickingListScanEntries(
                    getPickingScanEntriesModel()
                )
                if (input) {
                    context?.showLongToast(getString(R.string.success_save_data_local))
                    if (clearSnAndMac) {
                        clearSn()
                    } else {
                        clearAllView()
                    }
                } else {
                    context?.showLongToast(getString(R.string.error_qty_over_outstanding))
                }
            } else {
                context?.showLongToast(getString(R.string.error_sn_on_same_pickinglistno))
                et_sn_picking.clearText()
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
            showErrorPoNoDialog()
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
        } else if (checkPONo(et_po_no_picking.getTextAsString()).not()) {
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

    private fun showErrorPoNoDialog() {
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

    private fun clearPartNo() {
        et_part_no.clearText()
        et_part_no.requestFocus()
    }

    override fun onMultiplelineSelected(data: PickingListLineValue) {
        displayAutocompleteData(data)
        dialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}
