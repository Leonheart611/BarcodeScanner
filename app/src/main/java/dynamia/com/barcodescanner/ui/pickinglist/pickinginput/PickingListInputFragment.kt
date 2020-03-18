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
import dynamia.com.core.data.model.PickingListLineValue
import dynamia.com.core.data.model.PickingListScanEntriesValue
import dynamia.com.barcodescanner.ui.pickinglist.adapter.PickingMultipleLineAdapter
import dynamia.com.core.util.showToast
import kotlinx.android.synthetic.main.dialog_multiple_item.*
import kotlinx.android.synthetic.main.item_input_header.*
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
        toolbar_picking_list_input.title = args.pickingListNo
    }

    private fun setupListener() {
        toolbar_picking_list_input.setNavigationOnClickListener {
            view?.findNavController()?.popBackStack()
        }
        et_part_no.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (et_part_no.text.toString().length > 3) {
                    val data = viewModel.pickingListRepository.getAllPickingListLineFromInsert(
                        partNo = et_part_no.text.toString(),
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
        fab_save_new.setOnClickListener {
            if (checkMandatoryDataEmpty().not()){
                viewModel.pickingListRepository.insertPickingListScanEntries(
                    getPickingScanEntriesModel()
                )
                clearAllView()
                context?.showToast("Data Saved")
            }else{
                context?.showToast("harap lengkapi data yang mandatory")
            }
        }
        fab_save_exit.setOnClickListener {
            if (checkMandatoryDataEmpty().not()){
                viewModel.pickingListRepository.insertPickingListScanEntries(
                    getPickingScanEntriesModel()
                )
                context?.showToast("Data Saved")
                view?.findNavController()?.popBackStack()
            }else{
                context?.showToast("harap lengkapi data yang mandatory")
            }
        }
    }

    private fun getPickingScanEntriesModel(): PickingListScanEntriesValue {
        return PickingListScanEntriesValue(
            description = et_description.text.toString(),
            documentNo = pickListValue?.documentNo ?: "",
            lineNo = pickListValue?.lineNo ?: 0,
            pickingListNo = args.pickingListNo,
            itemNo = et_nks_no.text.toString(),
            partNo = et_part_no.text.toString(),
            serialNumber = et_sn.text.toString(),
            macAddress = et_mac_address.text.toString(),
            note = et_note.text.toString()
        )
    }

    private fun clearAllView(){
        et_part_no.text?.clear()
        et_sn.text?.clear()
        et_mac_address.text?.clear()
        et_nks_no.text?.clear()
        et_so.text?.clear()
        et_description.text?.clear()
        et_pl_dor.text?.clear()
        et_note.text?.clear()
    }

    fun checkOnDB(data: List<PickingListLineValue>) {
        if (data.isNotEmpty()) {
            if (data.size == 1) {
                displayAutocompleteData(data[0])
            } else {
                showMultipleDataDialog(data)
            }
        } else {
            context?.showToast("DATA TIDAK DI TEMUKAN")
        }
    }

    private fun displayAutocompleteData(data: PickingListLineValue) {
        with(data) {
            et_description.setText(description)
            et_nks_no.setText(no)
            et_so.setText(documentNo)
            et_pl_dor.setText(pickingListNo)
        }
        pickListValue = data
    }

    private fun checkMandatoryDataEmpty():Boolean{
        var anyEmpty = false
        if (et_part_no.text.toString().isEmpty()){
            anyEmpty = true
        }
        if (et_mac_address.text.toString().isEmpty()){
            anyEmpty = true
        }
        if (et_nks_no.text.toString().isEmpty()){
            anyEmpty = true
        }
        if (et_sn.text.toString().isEmpty()){
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
