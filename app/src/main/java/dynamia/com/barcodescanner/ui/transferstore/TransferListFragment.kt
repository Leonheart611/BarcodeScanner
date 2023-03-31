package dynamia.com.barcodescanner.ui.transferstore

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.databinding.TransferlistFragmentBinding
import dynamia.com.barcodescanner.ui.MainActivity
import dynamia.com.barcodescanner.ui.transferstore.TransferType.*
import dynamia.com.barcodescanner.ui.transferstore.adapter.InventoryHeaderListAdapter
import dynamia.com.barcodescanner.ui.transferstore.adapter.PurchaseHeaderListAdapter
import dynamia.com.barcodescanner.ui.transferstore.adapter.TransferListAdapter
import dynamia.com.barcodescanner.ui.transferstore.adapter.TransferReceiptListAdapter
import dynamia.com.core.base.BaseFragmentBinding
import dynamia.com.core.data.entinty.InventoryPickHeader
import dynamia.com.core.data.entinty.PurchaseOrderHeader
import dynamia.com.core.data.entinty.TransferReceiptHeader
import dynamia.com.core.data.entinty.TransferShipmentHeader
import dynamia.com.core.util.showLongToast

@AndroidEntryPoint
class TransferListFragment :
    BaseFragmentBinding<TransferlistFragmentBinding>(TransferlistFragmentBinding::inflate),
    TransferListAdapter.OnTransferListClicklistener,
    TransferReceiptListAdapter.OnTransferReceiptListCLicklistener,
    PurchaseHeaderListAdapter.OnPurchaseHeaderClicklistener,
    InventoryHeaderListAdapter.InventoryHeaderClicklistener {

    private val viewModel: TransferListViewModel by viewModels()
    private val transferListAdapter = TransferListAdapter(this)
    private val transferReceiptListAdapter = TransferReceiptListAdapter(this)
    private val purchaseOrderAdapter = PurchaseHeaderListAdapter(this)
    private val inventoryHeaderListAdapter = InventoryHeaderListAdapter(this)
    private val args: TransferListFragmentArgs by navArgs()
    private var activity: MainActivity? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity = requireActivity() as MainActivity
        setupRecylerView()
        setOnclicklistener()
        setupListener()
    }

    private fun setOnclicklistener() {
        with(viewBinding) {
            fabTransactionRefresh.setOnClickListener {
                viewModel.updateTransferShipment()
            }
            includeTransferlistHeader.tbHeaderlist.setNavigationOnClickListener {
                view?.findNavController()?.popBackStack()
            }
        }
    }

    private fun setupRecylerView() {
        viewBinding.includeTransferlistHeader.tbHeaderlist.title = viewModel.getCompanyName()
        viewModel.updatePager(20)
        when (args.transferType) {
            SHIPMENT -> setShipmentView()
            RECEIPT -> setReceiptView()
            PURCHASE -> setPurchaseView()
            INVENTORY -> setInventoryView()
            else -> {}
        }
        viewBinding.rvPickinglist.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisible: Int = layoutManager.findLastVisibleItemPosition()
                val endHasBeenReached = lastVisible >= (totalItemCount - 1)

                if (totalItemCount > 0 && endHasBeenReached) {
                    viewModel.updatePager(
                        totalItemCount + 20
                    )
                }
            }
        })
    }

    private fun setInventoryView() {
        with(viewBinding) {
            includeTransferlistHeader.tvTitleHeader.text = getString(R.string.inventory_pick_title)
            with(rvPickinglist) {
                layoutManager =
                    LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = inventoryHeaderListAdapter
            }
            viewModel.inventoryHeaderData.observe(viewLifecycleOwner) {
                inventoryHeaderListAdapter.submitList(it)
            }
        }
    }

    private fun setPurchaseView() {
        with(viewBinding) {
            includeTransferlistHeader.tvTitleHeader.text = getString(R.string.purchase_order_title)
            with(rvPickinglist) {
                layoutManager =
                    LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = purchaseOrderAdapter
            }
            viewModel.purchaseHeaderData.observe(viewLifecycleOwner) {
                purchaseOrderAdapter.submitList(it.toMutableList())
            }
        }
    }

    private fun setReceiptView() {
        with(viewBinding) {
            includeTransferlistHeader.tvTitleHeader.text =
                getString(R.string.transfer_receipt_title)
            with(rvPickinglist) {
                layoutManager =
                    LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = transferReceiptListAdapter
            }
            viewModel.transferReceiptHeader.observe(viewLifecycleOwner) {
                transferReceiptListAdapter.submitList(it.toMutableList())
            }
        }
    }

    private fun setShipmentView() {
        with(viewBinding) {
            includeTransferlistHeader.tvTitleHeader.text = getString(R.string.transfer_store)
            with(rvPickinglist) {
                layoutManager =
                    LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = transferListAdapter
            }
            viewModel.transferShipmentHeader.observe(viewLifecycleOwner) {
                transferListAdapter.submitList(it.toMutableList())
            }
            viewModel.transferViewState.observe(viewLifecycleOwner) {
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
            }
        }
    }

    private fun setupListener() {
        viewBinding.cvBack.setOnClickListener {
            view?.findNavController()?.popBackStack()
        }
    }

    override fun clickListener(data: TransferShipmentHeader) {
        val action =
            TransferListFragmentDirections.actionTransferListFragmentToTransferDetailFragment(
                data.no,
                SHIPMENT, null
            )
        view?.findNavController()?.navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewBinding.rvPickinglist.adapter = null
    }

    override fun clickListener(data: TransferReceiptHeader) {
        val action =
            TransferListFragmentDirections.actionTransferListFragmentToTransferDetailFragment(
                data.no,
                RECEIPT, null
            )
        view?.findNavController()?.navigate(action)
    }

    override fun clickListener(data: PurchaseOrderHeader) {
        val action =
            TransferListFragmentDirections.actionTransferListFragmentToTransferDetailFragment(
                data.no,
                PURCHASE, data.vendorInvoiceNo
            )
        view?.findNavController()?.navigate(action)
    }

    override fun onclicklistener(data: InventoryPickHeader) {
        val action =
            TransferListFragmentDirections.actionTransferListFragmentToTransferDetailFragment(
                data.no,
                INVENTORY, null
            )
        view?.findNavController()?.navigate(action)
    }
}
