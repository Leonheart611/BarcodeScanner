package dynamia.com.barcodescanner.ui.peminjaman

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
import dynamia.com.barcodescanner.ui.peminjaman.adapter.DorListAdapter
import dynamia.com.barcodescanner.ui.peminjaman.adapter.PeminjamanListAdapter
import dynamia.com.barcodescanner.ui.pickinglist.pickinginput.InputType.DOR
import dynamia.com.barcodescanner.ui.pickinglist.pickinginput.InputType.PEMINJAMAN
import dynamia.com.core.data.model.DorPickingHeader
import dynamia.com.core.data.model.PeminjamanHeader
import kotlinx.android.synthetic.main.header_layout.*
import kotlinx.android.synthetic.main.pickinglist_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PeminjamaanListFragment : Fragment(), PeminjamanListAdapter.OnPeminjamanClicklistener,
	DorListAdapter.OnDorListClicklistener {
	
	private val viewModel: PeminjamanListViewModel by viewModel()
	private val pinjamanAdapter = PeminjamanListAdapter(this)
	private val dorAdapter = DorListAdapter(this)
	private val args: PeminjamaanListFragmentArgs by navArgs()
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(R.layout.pickinglist_fragment, container, false)
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initView()
		setupListener()
	}
	
	private fun initView() {
		tv_employee_name.text = viewModel.getEmployeeName()
		tv_title_header.text = getString(R.string.peminjam_title_text)
		when (args.inputType) {
			PEMINJAMAN -> {
				viewModel.repository.getAllPeminjamHeader(viewModel.getEmployeeName())
					.observe(viewLifecycleOwner, {
						pinjamanAdapter.submitList(it)
					})
				with(rv_pickinglist) {
					layoutManager =
						LinearLayoutManager(context, RecyclerView.VERTICAL, false)
					adapter = pinjamanAdapter
					
				}
			}
			else -> {
				viewModel.dorPickingRepository.getAllDorHeader(viewModel.getEmployeeName())
					.observe(viewLifecycleOwner, {
						dorAdapter.submitList(it)
					})
				with(rv_pickinglist) {
					layoutManager =
						LinearLayoutManager(context, RecyclerView.VERTICAL, false)
					adapter = dorAdapter
				}
			}
		}
		
	}
	
	private fun setupListener() {
		cv_back.setOnClickListener {
			view?.findNavController()?.popBackStack()
		}
		tb_posolist.setOnMenuItemClickListener {
			when (it.itemId) {
				R.id.history_data -> {
					/*val action =
						PickingListFragmentDirections.actionPickingListFragmentToHistoryInputFragment(
							"", Constant.PICKING_LIST, true, null, null
						)
					view?.findNavController()?.navigate(action)*/
					true
				}
				else -> false
			}
		}
	}
	
	override fun onDestroy() {
		super.onDestroy()
		rv_pickinglist?.adapter = null
	}
	
	override fun onclicklister(data: PeminjamanHeader) {
		val action =
			PeminjamaanListFragmentDirections.actionPeminjamaanListFragmentToPeminjamDetailFragment(
				data.no, PEMINJAMAN
			)
		view?.findNavController()?.navigate(action)
	}
	
	override fun onclicklistener(data: DorPickingHeader) {
		val action =
			PeminjamaanListFragmentDirections.actionPeminjamaanListFragmentToPeminjamDetailFragment(
				data.no, DOR
			)
		view?.findNavController()?.navigate(action)
	}
}
