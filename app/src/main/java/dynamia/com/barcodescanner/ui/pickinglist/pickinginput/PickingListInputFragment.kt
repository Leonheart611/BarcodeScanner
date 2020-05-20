package dynamia.com.barcodescanner.ui.pickinglist.pickinginput

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.pickinglist.adapter.PickingMultipleLineAdapter
import dynamia.com.core.data.model.PickingListLineValue
import dynamia.com.core.data.model.PickingListScanEntriesValue
import dynamia.com.core.util.Constant.PICKING_LIST
import dynamia.com.core.util.getCurrentDate
import dynamia.com.core.util.getCurrentTime
import dynamia.com.core.util.showToast
import kotlinx.android.synthetic.main.dialog_multiple_item.*
import kotlinx.android.synthetic.main.item_input_header.*
import kotlinx.android.synthetic.main.item_input_header.et_part_no
import kotlinx.android.synthetic.main.receipt_form_item.*
import kotlinx.android.synthetic.main.receiving_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PickingListInputFragment : Fragment(), PickingMultipleLineAdapter.OnMultipleLineSelected {
    private val viewModel: PickingListInputViewModel by viewModel()
    private val args: PickingListInputFragmentArgs by navArgs()
    private var pickListValue: PickingListLineValue? = null
    private var dialog: Dialog? = null
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
    }

    private fun setupView() {
        tv_picking_detail_so.text = getString(R.string.picklistno_title, args.pickingListNo)
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
            if (checkMandatoryDataEmpty().not()) {
                viewModel.pickingListRepository.insertPickingListScanEntries(
                    getPickingScanEntriesModel()
                )
                clearAllView()
                context?.showToast(getString(R.string.success_save_data_local))
            }
        }
        cv_view.setOnClickListener {
            val action =
                PickingListInputFragmentDirections.actionReceivingFragmentToHistoryInputFragment(
                    args.pickingListNo,PICKING_LIST
                )
            view?.findNavController()?.navigate(action)
        }
    }

    private fun getPickingScanEntriesModel(): PickingListScanEntriesValue {
        return PickingListScanEntriesValue(
            documentNo = pickListValue?.documentNo ?: "",
            lineNo = pickListValue?.lineNo ?: 0,
            partNo = et_part_no.getTextAsString(),
            serialNo = et_sn_picking.getTextAsString(),
            macAddress = et_mac_address_picking.getTextAsString(),
            note = et_note.getTextAsString(),
            time = context?.getCurrentTime() ?: "",
            date = "${context?.getCurrentDate()}T${context?.getCurrentTime()}",
            employeeCode = viewModel.getEmployeeName() ?: "", qtyScan = "1"
        )
    }

    private fun clearAllView() {
        et_part_no.clearText()
        et_sn_picking.clearText()
        et_mac_address_picking.clearText()
        et_sn_picking.clearText()
        et_so.clearText()
        et_description.clearText()
        et_pl_no.clearText()
        et_note.clearText()
    }

    fun checkOnDB(data: List<PickingListLineValue>) {
        if (data.isNotEmpty()) {
            if (data.size == 1) {
                displayAutocompleteData(data[0])
            } else {
                showMultipleDataDialog(data)
            }
        }
    }

    private fun displayAutocompleteData(data: PickingListLineValue) {
        with(data) {
            et_description.setText(description)
            et_sn_picking.setText(no)
            et_so.setText(documentNo)
            et_pl_no.setText(pickingListNo)
        }
        pickListValue = data
    }

    private fun checkMandatoryDataEmpty(): Boolean {
        var anyEmpty = false
        if (et_part_no.isEmpty()) {
            anyEmpty = true
        }
        if (et_mac_address_picking.isEmpty()) {
            anyEmpty = true
        }
        if (et_sn_picking.isEmpty()) {
            anyEmpty = true
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

    override fun onMultiplelineSelected(data: PickingListLineValue) {
        displayAutocompleteData(data)
        dialog?.dismiss()
    }
}
