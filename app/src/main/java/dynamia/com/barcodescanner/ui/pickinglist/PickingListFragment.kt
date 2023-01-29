package dynamia.com.barcodescanner.ui.pickinglist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.pickinglist.adapter.PickingListAdapter
import dynamia.com.core.util.Constant
import kotlinx.android.synthetic.main.header_layout.*
import kotlinx.android.synthetic.main.pickinglist_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PickingListFragment : Fragment(), PickingListAdapter.OnPickinglistListener {
	
	private val viewModel: PickingListViewModel by viewModel()
	private val pickingListAdapter = PickingListAdapter(mutableListOf(), this)
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(R.layout.pickinglist_fragment, container, false)
	}
	
	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		setupRecylerView()
		initView()
		setupListener()
	}
	
	private fun setupRecylerView() {
		with(rv_pickinglist) {
			layoutManager =
				LinearLayoutManager(context, RecyclerView.VERTICAL, false)
			adapter = pickingListAdapter
		}
		
	}
	
	private fun initView() {
		tv_employee_name.text = viewModel.getEmployeeName()
		tv_title_header.text = getString(R.string.pickingList_header_title)
		viewModel.pickingListRepository.getAllPickingListHeader(viewModel.getEmployeeName())
			.observe(viewLifecycleOwner, {
				pickingListAdapter.updateData(it.toMutableList())
			})
	}
	
	private fun setupListener() {
		cv_back.setOnClickListener {
			view?.findNavController()?.popBackStack()
		}
		tb_posolist.setOnMenuItemClickListener {
			when (it.itemId) {
				R.id.history_data -> {
					val action =
						PickingListFragmentDirections.actionPickingListFragmentToHistoryInputFragment(
							"",
							Constant.PICKING_LIST,
							showAll = true,
							partNo = null,
							documentNo = null
						)
					view?.findNavController()?.navigate(action)
					true
				}
				else -> false
			}
		}
	}
	
	
	override fun onPickingListClickListener(pickingListNo: String) {
		val action = PickingListFragmentDirections.actionPickingListFragmentToPickingDetailFragment(
			pickingListNo
		)
		view?.findNavController()?.navigate(action)
	}
	
	override fun onDestroy() {
		super.onDestroy()
		rv_pickinglist?.adapter = null
	}
}
