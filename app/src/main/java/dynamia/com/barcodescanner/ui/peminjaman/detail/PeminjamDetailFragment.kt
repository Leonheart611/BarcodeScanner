package dynamia.com.barcodescanner.ui.peminjaman.detail

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.peminjaman.adapter.DorDetailListAdapter
import dynamia.com.barcodescanner.ui.peminjaman.adapter.PeminjamanDetailListAdapter
import dynamia.com.barcodescanner.ui.pickinglist.pickinginput.InputType.DOR
import dynamia.com.barcodescanner.ui.pickinglist.pickinginput.InputType.PEMINJAMAN
import dynamia.com.barcodescanner.ui.pickinglist.pickinginput.PickingHistoryBottomSheet
import dynamia.com.core.data.model.DorPickingDetail
import dynamia.com.core.data.model.DorPickingHeader
import dynamia.com.core.data.model.PeminjamanDetail
import dynamia.com.core.data.model.PeminjamanHeader
import dynamia.com.core.util.toNormalDate
import kotlinx.android.synthetic.main.dialog_validate_s.*
import kotlinx.android.synthetic.main.picking_detail_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PeminjamDetailFragment : Fragment(), PeminjamanDetailListAdapter.PeminjamOnClicklistener,
	DorDetailListAdapter.OnDorDetailClicklistener {
	private val viewModel: PeminjamDetailViewModel by viewModel()
	private val args: PeminjamDetailFragmentArgs by navArgs()
	private val peminjamAdapter = PeminjamanDetailListAdapter(this)
	private val dorAdapter = DorDetailListAdapter(this)
	private var settingDialog: Dialog? = null
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(R.layout.picking_detail_fragment, container, false)
	}
	
	override fun onResume() {
		super.onResume()
		et_customer_po_no.setText(viewModel.getEmployeeName())
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewModel.updateDocumentNo(args.documentNo)
		setupView()
		setupListener()
		setObseverable()
	}
	
	private fun setupView() {
		tv_picking_detail_so.text = getString(R.string.peminjamlistno_title, args.documentNo)
		et_customer_po_no.setText(viewModel.getEmployeeName())
	}
	
	private fun setObseverable() {
		when (args.inputType) {
			PEMINJAMAN -> {
				rv_picking_detail.layoutManager =
					LinearLayoutManager(context, RecyclerView.VERTICAL, false)
				rv_picking_detail.adapter = peminjamAdapter
				viewModel.peminjamanHeader.observe(viewLifecycleOwner, { setupMainView(it) })
				viewModel.peminjamDetailList.observe(viewLifecycleOwner,
					{ peminjamAdapter.submitList(it) })
			}
			DOR -> {
				rv_picking_detail.layoutManager =
					LinearLayoutManager(context, RecyclerView.VERTICAL, false)
				rv_picking_detail.adapter = dorAdapter
				viewModel.dorHeader.observe(viewLifecycleOwner, { setupDorView(it) })
				viewModel.dorDetailList.observe(viewLifecycleOwner,
					{ dorAdapter.submitList(it) })
			}
			else -> {}
		}
	}
	
	private fun setupMainView(value: PeminjamanHeader) {
		with(value) {
			et_customer_name.setText(transferToName)
			et_order_date.setText(postingDate.toNormalDate())
			et_so_no.setText(no)
			et_project_code.setText(projectCode)
		}
	}
	
	private fun setupDorView(value: DorPickingHeader) {
		with(value) {
			et_customer_name.setText(transferToName)
			et_order_date.setText(reqDeliveryDate.toNormalDate())
			et_so_no.setText(no)
			et_project_code.setText(projectCode)
		}
	}
	
	private fun setupListener() {
		cv_pick.setOnClickListener {
			pickingInputDialog()
		}
		cv_back.setOnClickListener {
			view?.findNavController()?.popBackStack()
		}
		cv_post.setOnClickListener {
			showPostDialog()
		}
		toolbar_picking_detail.setOnMenuItemClickListener {
			when (it.itemId) {
				R.id.menu_search -> {
					/*val action =
						PickingDetailFragmentDirections.actionPickingDetailFragmentToReceiptSearchFragment(
							args.documentNo, Constant.PICKING_LIST
						)
					view?.findNavController()?.navigate(action)*/
					true
				}
				R.id.menu_history -> {
					val bottomSheetFragment =
						PickingHistoryBottomSheet.newInstance(args.documentNo, args.inputType)
					bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
					true
				}
				else -> false
			}
		}
	}
	
	private fun showPostDialog() {
		val dialog = PeminjamPostDialog.newInstance(args.inputType)
		dialog.show(requireActivity().supportFragmentManager, dialog.tag)
	}
	
	override fun onDestroy() {
		super.onDestroy()
		rv_picking_detail?.adapter = null
	}
	
	private fun pickingInputDialog() {
		context?.let { context ->
			settingDialog = Dialog(context)
			settingDialog?.let { dialog ->
				with(dialog) {
					setContentView(R.layout.dialog_validate_s)
					window
						?.setLayout(
							ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT
						)
					cb_check_s_false.setOnClickListener {
						when (cb_check_s_false.isChecked) {
							true -> {
								cb_check_s_true.isChecked = false
							}
							else -> {}
						}
					}
					
					cb_check_s_true.setOnClickListener {
						when (cb_check_s_true.isChecked) {
							true -> {
								cb_check_s_false.isChecked = false
							}
							else -> {}
						}
					}
					
					btn_setting_continue.setOnClickListener {
						dismiss()
						val action = if (cb_check_s_true.isChecked) {
							PeminjamDetailFragmentDirections.actionPeminjamDetailFragmentToReceivingFragment(
								args.documentNo,
								validateS = true,
								inputType = args.inputType
							)
						} else {
							PeminjamDetailFragmentDirections.actionPeminjamDetailFragmentToReceivingFragment(
								args.documentNo,
								validateS = false,
								inputType = args.inputType
							)
						}
						view?.findNavController()?.navigate(action)
					}
					show()
				}
			}
		}
	}
	
	override fun onclicklistener(data: PeminjamanDetail) {
	
	}
	
	override fun onclicklistener(data: DorPickingDetail) {
	
	}
}