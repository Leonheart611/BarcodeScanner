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
import dynamia.com.barcodescanner.ui.transferstore.adapter.PurchaseHeaderListAdapter
import dynamia.com.barcodescanner.ui.transferstore.adapter.TransferListAdapter
import dynamia.com.barcodescanner.ui.transferstore.adapter.TransferReceiptListAdapter
import dynamia.com.core.base.BaseFragmentBinding
import dynamia.com.core.data.entinty.PurchaseOrderHeader
import dynamia.com.core.data.entinty.TransferReceiptHeader
import dynamia.com.core.data.entinty.TransferShipmentHeader
import dynamia.com.core.util.showLongToast

@AndroidEntryPoint
class TransferListFragment :
    BaseFragmentBinding<TransferlistFragmentBinding>(TransferlistFragmentBinding::inflate),
    TransferListAdapter.OnTransferListClicklistener,
    TransferReceiptListAdapter.OnTransferReceiptListCLicklistener,
    PurchaseHeaderListAdapter.OnPurchaseHeaderClicklistener {

    private val viewModel: TransferListViewModel by viewModels()
    private val transferListAdapter = TransferListAdapter(mutableListOf(), this)
    private val transferReceiptListAdapter = TransferReceiptListAdapter(mutableListOf(), this)
    private val purchaseOrderAdapter = PurchaseHeaderListAdapter(mutableListOf(), this)
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
        when (args.transferType) {
            SHIPMENT -> setShipmentView()
            RECEIPT -> setReceiptView()
            PURCHASE -> setPurchaseView()
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
            viewModel.purchaseOrderRepository.getAllPurchaseOrderHeader()
                .observe(viewLifecycleOwner, {
                    purchaseOrderAdapter.updateData(it.toMutableList())
                })
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
            viewModel.transferReceiptRepository.getAllTransferReceiptHeader()
                .observe(viewLifecycleOwner, {
                    transferReceiptListAdapter.updateData(it.toMutableList())
                })
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
            viewModel.transferShipmentRepository.getAllTransferHeader()
                .observe(viewLifecycleOwner, {
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
    }

    private fun setupListener() {
        viewBinding.cvBack.setOnClickListener {
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
        viewBinding.rvPickinglist?.adapter = null
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
