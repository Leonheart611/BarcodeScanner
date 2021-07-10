package dynamia.com.barcodescanner.ui.transferstore

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
import dynamia.com.barcodescanner.ui.MainActivity
import dynamia.com.barcodescanner.ui.transferstore.TransferType.*
import dynamia.com.barcodescanner.ui.transferstore.adapter.PurchaseHeaderListAdapter
import dynamia.com.barcodescanner.ui.transferstore.adapter.TransferListAdapter
import dynamia.com.barcodescanner.ui.transferstore.adapter.TransferReceiptListAdapter
import dynamia.com.core.data.entinty.PurchaseOrderHeader
import dynamia.com.core.data.entinty.TransferReceiptHeader
import dynamia.com.core.data.entinty.TransferShipmentHeader
import dynamia.com.core.util.showLongToast
import kotlinx.android.synthetic.main.header_layout.*
import kotlinx.android.synthetic.main.transferlist_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TransferListFragment : Fragment(), TransferListAdapter.OnTransferListClicklistener,
    TransferReceiptListAdapter.OnTransferReceiptListCLicklistener,
    PurchaseHeaderListAdapter.OnPurchaseHeaderClicklistener {

    private val viewModel: TransferListViewModel by viewModel()
    private val transferListAdapter = TransferListAdapter(mutableListOf(), this)
    private val transferReceiptListAdapter = TransferReceiptListAdapter(mutableListOf(), this)
    private val purchaseOrderAdapter = PurchaseHeaderListAdapter(mutableListOf(), this)
    private val args: TransferListFragmentArgs by navArgs()
    private var activity: MainActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.transferlist_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity = requireActivity() as MainActivity
        setupRecylerView()
        setOnclicklistener()
        setupListener()
    }

    private fun setOnclicklistener() {
        fab_transaction_refresh.setOnClickListener {
            viewModel.updateTransferShipment()
        }
        tb_headerlist.setNavigationOnClickListener { view?.findNavController()?.popBackStack() }
    }

    private fun setupRecylerView() {
        tb_headerlist.title = viewModel.getCompanyName()
        when (args.transferType) {
            SHIPMENT -> setShipmentView()
            RECEIPT -> setReceiptView()
            PURCHASE -> setPurchaseView()
        }
    }

    private fun setPurchaseView() {
        tv_title_header.text = getString(R.string.purchase_order_title)
        with(rv_pickinglist) {
            layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = purchaseOrderAdapter
        }
        viewModel.purchaseOrderRepository.getAllPurchaseOrderHeader()
            .observe(viewLifecycleOwner, {
                purchaseOrderAdapter.updateData(it.toMutableList())
            })
    }

    private fun setReceiptView() {
        tv_title_header.text = getString(R.string.transfer_receipt_title)
        with(rv_pickinglist) {
            layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = transferReceiptListAdapter
        }
        viewModel.transferReceiptRepository.getAllTransferReceiptHeader()
            .observe(viewLifecycleOwner, {
                transferReceiptListAdapter.updateData(it.toMutableList())
            })
    }

    private fun setShipmentView() {
        tv_title_header.text = getString(R.string.transfer_store)
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

    private fun setupListener() {
        cv_back.setOnClickListener {
            view?.findNavController()?.popBackStack()
        }
    }

    override fun clickListener(data: TransferShipmentHeader) {
        val action =
            TransferListFragmentDirections.actionTransferListFragmentToTransferDetailFragment(data.no,
                SHIPMENT)
        view?.findNavController()?.navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        rv_pickinglist?.adapter = null
    }

    override fun clickListener(data: TransferReceiptHeader) {
        val action =
            TransferListFragmentDirections.actionTransferListFragmentToTransferDetailFragment(data.no,
                RECEIPT)
        view?.findNavController()?.navigate(action)
    }

    override fun clickListener(data: PurchaseOrderHeader) {
        val action =
            TransferListFragmentDirections.actionTransferListFragmentToTransferDetailFragment(data.no,
                PURCHASE)
        view?.findNavController()?.navigate(action)
    }
}
