package dynamia.com.barcodescanner.ui.pickinglist.pickinginput

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
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
import dynamia.com.barcodescanner.ui.MainActivity
import dynamia.com.barcodescanner.ui.history.adapter.HistoryInputAdapter
import dynamia.com.barcodescanner.ui.peminjaman.adapter.DorDetailListAdapter
import dynamia.com.barcodescanner.ui.peminjaman.adapter.DorInsertAdapter
import dynamia.com.barcodescanner.ui.peminjaman.adapter.PeminjamInsertAdapter
import dynamia.com.barcodescanner.ui.peminjaman.adapter.PeminjamanDetailListAdapter
import dynamia.com.barcodescanner.ui.pickinglist.adapter.InsertHistoryAdapter
import dynamia.com.barcodescanner.ui.pickinglist.adapter.PickingMultipleLineAdapter
import dynamia.com.barcodescanner.ui.pickinglist.pickinginput.InputType.*
import dynamia.com.core.data.model.*
import dynamia.com.core.util.*
import kotlinx.android.synthetic.main.delete_confirmation_dialog.*
import kotlinx.android.synthetic.main.dialog_multiple_item.*
import kotlinx.android.synthetic.main.dialog_part_no_not_found.*
import kotlinx.android.synthetic.main.dialog_part_no_not_found.tv_error_message
import kotlinx.android.synthetic.main.history_input_fragment.*
import kotlinx.android.synthetic.main.item_input_header.*
import kotlinx.android.synthetic.main.receiving_fragment.*
import kotlinx.android.synthetic.main.receiving_fragment.cv_back
import kotlinx.android.synthetic.main.receiving_fragment.tv_picking_detail_so
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class PickingListInputFragment : Fragment(), PickingMultipleLineAdapter.OnMultipleLineSelected,
	HistoryInputAdapter.OnHistorySelected, PeminjamanDetailListAdapter.PeminjamOnClicklistener,
	DorDetailListAdapter.OnDorDetailClicklistener {
	private val viewModel: PickingListInputViewModel by viewModel()
	private val args: PickingListInputFragmentArgs by navArgs()
	private var inputHistoryAdapter = InsertHistoryAdapter(mutableListOf())
	private val peminjamanInsertAdapter = PeminjamInsertAdapter()
	private val dorInsertAdapter = DorInsertAdapter()
	
	private var dialog: Dialog? = null
	private var poNoDialog: Dialog? = null
	private var purchaseNo: String = ""
	private var pickingListValue: PickingListScanEntriesValue? = null
	private var mp: MediaPlayer? = null
	var activity: MainActivity? = null
	
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
	
	override fun onResume() {
		super.onResume()
		pickingListValue?.let { setAllDataView(it) }
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setupView()
		mp = MediaPlayer.create(context, R.raw.error)
		activity = requireActivity() as MainActivity
		setupListener()
		setupObserverable()
	}
	
	private fun setupObserverable() {
		when (args.inputType) {
			PICKING -> {
				viewModel.pickingListRepository.getPickingListScanEntries(args.pickingListNo, 5)
					.observe(viewLifecycleOwner) {
						inputHistoryAdapter.update(it.toMutableList())
					}
				viewModel.pickingInputViewState.observe(viewLifecycleOwner) {
					when (it) {
						is PickingListInputViewModel.PickingInputViewState.SuccessGetValue -> {
							checkOnDB(it.data)
						}
						is PickingListInputViewModel.PickingInputViewState.ErrorGetData -> {
							context?.showLongToast(it.message)
						}
						is PickingListInputViewModel.PickingInputViewState.CheckSNResult -> {
							if (it.boolean) {
								val input =
									viewModel.pickingListRepository.insertPickingListScanEntries(
										getPickingScanEntriesModel()
									)
								if (input) {
									context?.showLongToast(getString(R.string.success_save_data_local))
									if (et_mac_address_picking.isEmpty()) {
										if (switch_sn_mode.isChecked) {
											et_sn_picking.clearText()
											clearPartNo()
										} else {
											clearSn()
										}
									} else {
										clearSnAndMac()
									}
								} else {
									context?.showLongToast(getString(R.string.error_qty_over_outstanding))
									et_sn_picking.clearText()
									et_sn_picking.requestFocus()
									mp?.start()
								}
							} else {
								context?.showLongToast(getString(R.string.error_sn_on_same_pickinglistno))
								et_sn_picking.clearText()
								et_sn_picking.requestFocus()
								mp?.start()
							}
						}
						is PickingListInputViewModel.PickingInputViewState.LoadingSearchPickingList -> {
							activity?.showLoading(it.status)
						}
					}
				}
			}
			PEMINJAMAN -> {
				viewModel.peminjamInsertHistory.observe(viewLifecycleOwner) {
					peminjamanInsertAdapter.submitList(it)
				}
				viewModel.peminjamInputViewState.observe(viewLifecycleOwner) {
					when (it) {
						is PickingListInputViewModel.PeminjamanInputViewState.CheckSNResult -> {
							if (it.boolean) {
								val input =
									viewModel.peminjamanRepository.insertPeminjamScanEntires(
										getPeminjamScanEntriesValue()
									)
								if (input) {
									context?.showLongToast(getString(R.string.success_save_data_local))
									if (et_mac_address_picking.isEmpty()) {
										if (switch_sn_mode.isChecked) {
											et_sn_picking.clearText()
											clearPartNo()
										} else {
											clearSn()
										}
									} else {
										clearSnAndMac()
									}
								} else {
									context?.showLongToast(getString(R.string.error_qty_over_outstanding))
									et_sn_picking.clearText()
									et_sn_picking.requestFocus()
									mp?.start()
								}
							} else {
								context?.showLongToast(getString(R.string.error_sn_on_same_pickinglistno))
								et_sn_picking.clearText()
								et_sn_picking.requestFocus()
								mp?.start()
							}
						}
						is PickingListInputViewModel.PeminjamanInputViewState.ErrorGetData -> {
							context?.showLongToast(it.message)
						}
						is PickingListInputViewModel.PeminjamanInputViewState.LoadingSearchPeminjaman -> {
							activity?.showLoading(it.status)
						}
						is PickingListInputViewModel.PeminjamanInputViewState.SuccessGetValue -> {
							checkPeminjamDb(it.data)
						}
					}
				}
			}
			DOR -> {
				viewModel.dorInsertHistory.observe(viewLifecycleOwner) {
					dorInsertAdapter.submitList(it)
				}
				viewModel.dorInputViewState.observe(viewLifecycleOwner) {
					when (it) {
						is PickingListInputViewModel.DorInputViewState.CheckSNResult -> {
							if (it.boolean) {
								val input =
									viewModel.dorPickingRepository.insertDorScanEntry(
										getDorScanEntriesValue()
									)
								if (input) {
									context?.showLongToast(getString(R.string.success_save_data_local))
									if (et_mac_address_picking.isEmpty()) {
										if (switch_sn_mode.isChecked) {
											et_sn_picking.clearText()
											clearPartNo()
										} else {
											clearSn()
										}
									} else {
										clearSnAndMac()
									}
								} else {
									context?.showLongToast(getString(R.string.error_qty_over_outstanding))
									et_sn_picking.clearText()
									et_sn_picking.requestFocus()
									mp?.start()
								}
							} else {
								context?.showLongToast(getString(R.string.error_sn_on_same_pickinglistno))
								et_sn_picking.clearText()
								et_sn_picking.requestFocus()
								mp?.start()
							}
						}
						is PickingListInputViewModel.DorInputViewState.ErrorGetData -> {
							context?.showLongToast(it.message)
						}
						is PickingListInputViewModel.DorInputViewState.LoadingSearchDor -> {
							activity?.showLoading(it.status)
						}
						is PickingListInputViewModel.DorInputViewState.SuccessGetValue -> {
							checkDorDb(it.data)
						}
					}
				}
			}
		}
		
	}
	
	private fun setupView() {
		tv_picking_detail_so.text = getString(R.string.picklistno_title, args.pickingListNo)
		with(rv_history_input) {
			layoutManager =
				LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
			adapter = when (args.inputType) {
				PICKING -> inputHistoryAdapter
				PEMINJAMAN -> peminjamanInsertAdapter
				DOR -> dorInsertAdapter
			}
		}
		et_part_no.requestFocus()
		switch_sn_mode.setOnCheckedChangeListener { p0, isChecked ->
			if (isChecked)
				switch_sn_mode.text = getString(R.string.sn_pn_scan)
			else
				switch_sn_mode.text = getString(R.string.sn_normal_scan)
		}
	}
	
	private fun setupListener() {
		cv_back.setOnClickListener {
			view?.findNavController()?.popBackStack()
		}
		et_part_no.addTextWatcher(object : TextWatcher {
			
			override fun afterTextChanged(p0: Editable?) {
				if (switch_manual_picking.isChecked) {
					if (et_part_no.getTextLength() > 3) {
						when (args.inputType) {
							PICKING -> viewModel.getPickingListLineValue(
								partNo = et_part_no.getTextAsString(),
								pickingListNo = args.pickingListNo
							)
							PEMINJAMAN -> {
								viewModel.getPeminjamListLine(
									partNo = et_part_no.getTextAsString(),
									documentId = args.pickingListNo
								)
							}
							DOR -> {
								viewModel.getDorListLine(
									partNo = et_part_no.getTextAsString(),
									documentId = args.pickingListNo
								)
							}
						}
						
					}
				}
			}
			
			override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
			
			override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
			
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
					openHistoryDialog()
					true
				}
				else -> false
			}
		}
		et_sn_picking.addTextWatcher(object : TextWatcher {
			override fun afterTextChanged(p0: Editable?) {
				if (switch_manual_picking.isChecked) {
					if (p0.toString().length > 11)
						saveDataLocal()
				}
			}
			
			override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
			
			override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
		})
		et_po_no_picking.addTextWatcher(object : TextWatcher {
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
		
		et_part_no.addSetOnEditorClickListener { _, actionId, _ ->
			if (actionId == EditorInfo.IME_ACTION_NEXT) {
				viewModel.getPickingListLineValue(
					partNo = et_part_no.getTextAsString(),
					pickingListNo = args.pickingListNo
				)
			}
			false
		}
		
		et_po_no_picking.addSetOnEditorClickListener { _, actionId, _ ->
			if (actionId == EditorInfo.IME_ACTION_NEXT) {
				val currentPoNo =
					et_po_no_picking.getTextAsString().checkFirstCharacter("K")
				if (checkPONo(currentPoNo).not()) {
					showErrorPONoDialog(getString(R.string.error_po_no_not_same))
				} else {
					et_note.requestFocus()
				}
			}
			false
		}
	}
	
	private fun saveDataLocal() {
		if (checkMandatoryDataEmpty().not()) {
			when (args.validateS) {
				true -> {
					if (et_sn_picking.getTextAsString().startsWith("S", true)) {
						viewModel.checkSn(et_sn_picking.getTextAsString(), args.inputType)
					} else {
						context?.showLongToast("SN Harus di awali dengan S")
						clearSn()
						mp?.start()
					}
				}
				false -> {
					viewModel.checkSn(et_sn_picking.getTextAsString(), args.inputType)
				}
			}
		} else {
			context?.showLongToast("Some Thing wrongs")
		}
	}
	
	private fun getPickingScanEntriesModel(): PickingListScanEntriesValue {
		return PickingListScanEntriesValue(
			documentNo = et_pl_no.getTextAsString(),
			lineNo = viewModel.pickListValue?.lineNo ?: 0,
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
	
	private fun getPeminjamScanEntriesValue(): PeminjamScanEntries {
		return PeminjamScanEntries(
			documentNo = et_pl_no.getTextAsString(),
			lineNo = viewModel.peminjamanDetailData?.lineNo ?: 0,
			partNo = et_part_no.getTextAsString(),
			serialNo = et_sn_picking.getTextAsString(),
			macAddress = et_mac_address_picking.getTextAsString().emptySetZero(),
			time = context?.getCurrentTime() ?: "",
			date = "${context?.getCurrentDate()}T${context?.getCurrentTime()}",
			employeeCode = viewModel.getEmployeeName(),
			packingID = "-", pONo = "-", shipset = "-", trackingID = "-", sycn_status = false
		)
	}
	
	private fun getDorScanEntriesValue(): DorPickingScanEntries {
		return DorPickingScanEntries(
			documentNo = et_pl_no.getTextAsString(),
			lineNo = viewModel.dorDetailValue?.lineNo ?: 0,
			partNo = et_part_no.getTextAsString(),
			serialNo = et_sn_picking.getTextAsString(),
			macAddress = et_mac_address_picking.getTextAsString().emptySetZero(),
			time = context?.getCurrentTime() ?: "",
			date = "${context?.getCurrentDate()}T${context?.getCurrentTime()}",
			employeeCode = viewModel.getEmployeeName(),
			packingID = "-", pONo = "-", shipset = "-", trackingID = "-", sycn_status = false
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
	
	private fun checkOnDB(data: List<PickingListLineValue>) {
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
	
	private fun checkPeminjamDb(data: List<PeminjamanDetail>) {
		if (data.isNotEmpty()) {
			if (data.size == 1) {
				displayPeminjamData(data[0])
			} else {
				showMultiplePeminjamData(data)
			}
		} else {
			showErrorPartNo()
		}
	}
	
	private fun checkDorDb(data: List<DorPickingDetail>) {
		if (data.isNotEmpty()) {
			if (data.size == 1) {
				displayDorData(data[0])
			} else {
				showMultipleDorData(data)
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
		viewModel.pickListValue = data
		if (et_po_no_picking.getTextAsString().isNullOrEmpty()) {
			et_po_no_picking.requestFocus()
		} else {
			if (switch_sn_mode.isChecked) {
				et_sn_picking.requestFocus()
			}
		}
	}
	
	private fun displayPeminjamData(data: PeminjamanDetail) {
		with(data) {
			et_description_picking.setText(description)
			et_item_no.setText(itemNo)
			et_so.setText(documentNo)
			et_po_no_picking.setText("-")
			et_pl_no.setText(args.pickingListNo)
			viewModel.updateHistoryParam(
				PickingListInputViewModel.Param(
					documentNo = args.pickingListNo,
					partNo = partNo
				)
			)
		}
		viewModel.peminjamanDetailData = data
		et_sn_picking.requestFocus()
	}
	
	private fun displayDorData(data: DorPickingDetail) {
		with(data) {
			et_description_picking.setText(description)
			et_item_no.setText(itemNo)
			et_so.setText(documentNo)
			et_po_no_picking.setText("-")
			et_pl_no.setText(args.pickingListNo)
			viewModel.updateHistoryParam(
				PickingListInputViewModel.Param(
					documentNo = args.pickingListNo,
					partNo = partNo
				)
			)
		}
		viewModel.dorDetailValue = data
		et_sn_picking.requestFocus()
	}
	
	private fun checkPONo(poNO: String): Boolean {
		return (purchaseNo.isNotEmpty() && purchaseNo == poNO || args.inputType != PICKING)
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
	
	private fun showMultiplePeminjamData(data: List<PeminjamanDetail>) {
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
					val adapter = PeminjamanDetailListAdapter(this@PickingListInputFragment)
					rv_muliple_line.layoutManager =
						LinearLayoutManager(context, RecyclerView.VERTICAL, false)
					rv_muliple_line.adapter = adapter
					adapter.submitList(data)
					show()
				}
			}
		}
	}
	
	private fun showMultipleDorData(data: List<DorPickingDetail>) {
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
					val adapter = DorDetailListAdapter(this@PickingListInputFragment)
					rv_muliple_line.layoutManager =
						LinearLayoutManager(context, RecyclerView.VERTICAL, false)
					rv_muliple_line.adapter = adapter
					adapter.submitList(data)
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
					mp?.start()
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
					mp?.start()
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
	
	private fun openHistoryDialog() {
		val bottomSheetFragment =
			PickingHistoryBottomSheet.newInstance(args.pickingListNo, args.inputType)
		bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
	}
	
	override fun onHistorySelectDelete(value: PickingListScanEntriesValue) {
		context?.let { context ->
			val dialog = Dialog(context)
			with(dialog) {
				setContentView(R.layout.delete_confirmation_dialog)
				window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
				window
					?.setLayout(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT
					)
				btn_delete.setOnClickListener {
					viewModel.pickingListRepository.deletePickingListScanEntries(value)
					dismiss()
					setupView()
				}
				btn_cancel.setOnClickListener {
					dismiss()
				}
				show()
			}
		}
	}
	
	override fun onStop() {
		super.onStop()
		mp?.release()
	}
	
	override fun onDestroy() {
		super.onDestroy()
		mp?.release()
	}
	
	override fun onclicklistener(data: PeminjamanDetail) {
		displayPeminjamData(data)
		dialog?.dismiss()
	}
	
	override fun onclicklistener(data: DorPickingDetail) {
		displayDorData(data)
		dialog?.dismiss()
	}
	
}
