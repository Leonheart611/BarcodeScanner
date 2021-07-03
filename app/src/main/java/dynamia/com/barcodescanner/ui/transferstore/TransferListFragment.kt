package dynamia.com.barcodescanner.ui.transferstore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.MainActivity
import dynamia.com.barcodescanner.ui.transferstore.adapter.TransferListAdapter
import dynamia.com.core.data.entinty.TransferShipmentHeader
import dynamia.com.core.util.showLongToast
import kotlinx.android.synthetic.main.header_layout.*
import kotlinx.android.synthetic.main.transferlist_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TransferListFragment : Fragment(), TransferListAdapter.OnTransferListClicklistener {

    private val viewModel: TransferListViewModel by viewModel()
    private val transferListAdapter = TransferListAdapter(mutableListOf(), this)
    private var activity: MainActivity? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.transferlist_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity = requireActivity() as MainActivity
        setupRecylerView()
        setOnclicklistener()
        initView()
        setupListener()
    }

    private fun setOnclicklistener() {
        fab_transaction_refresh.setOnClickListener {
            viewModel.updateTransferShipment()
        }
        tb_headerlist.setNavigationOnClickListener { view?.findNavController()?.popBackStack() }
    }

    private fun setupRecylerView() {
        with(rv_pickinglist) {
            layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = transferListAdapter
        }
        viewModel.transferShipmentRepository.getAllTransferHeader().observe(viewLifecycleOwner, {
            transferListAdapter.updateData(it.toMutableList())
        })
        viewModel.transferViewState.observe(viewLifecycleOwner, {
            when (it) {
                is TransferListViewModel.TransferListViewState.Error -> {
                    context?.showLongToast(it.message)
                }
                is TransferListViewModel.TransferListViewState.ShowLoading -> {
                    activity?.showLoading(it.boolean)
                }
                TransferListViewModel.TransferListViewState.SuccessUpdateData -> {
                    context?.showLongToast(getString(R.string.qty_alreadyscan_qty_fromline_error_mssg))
                }
            }
        })
    }

    private fun initView() {
        tb_headerlist.title = viewModel.getCompanyName()
        tv_title_header.text = getString(R.string.transfer_store)
    }

    private fun setupListener() {
        cv_back.setOnClickListener {
            view?.findNavController()?.popBackStack()
        }
        tb_headerlist.setOnMenuItemClickListener {
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


    override fun clickListener(data: TransferShipmentHeader) {
        val action =
            TransferListFragmentDirections.actionTransferListFragmentToTransferDetailFragment(data.no)
        view?.findNavController()?.navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        rv_pickinglist?.adapter = null
    }
}
