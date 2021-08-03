package dynamia.com.barcodescanner.ui.transferstore.transferdetail

import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.databinding.TransferDetailFragmentBinding
import dynamia.com.barcodescanner.ui.transferstore.TransferType.*
import dynamia.com.barcodescanner.ui.transferstore.adapter.PurchaseDetailLineAdapter
import dynamia.com.barcodescanner.ui.transferstore.adapter.TransferDetailLineAdapter
import dynamia.com.core.base.BaseFragmentBinding
import dynamia.com.core.data.entinty.*
import dynamia.com.core.util.showLongToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class TransferDetailFragment :
    BaseFragmentBinding<TransferDetailFragmentBinding>(TransferDetailFragmentBinding::inflate),
    TransferDetailLineAdapter.OnTransferLineCLicklistener,
    PurchaseDetailLineAdapter.OnPurchaseLineClicklistener {
    private val viewModel: TransferDetailViewModel by viewModel()
    private val args: TransferDetailFragmentArgs by navArgs()
    private val transferReceiptAdapter = TransferDetailLineAdapter(mutableListOf())
    private val purchaseDetailLineAdapter = PurchaseDetailLineAdapter(mutableListOf())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewBinding) {
            includeTransferDetail.tvTransferdetailStore.text =
                getString(R.string.transfer_store_name, viewModel.getCompanyName())
            toolbarTransferDetail.title = viewModel.getCompanyName()
            includeTransferDetail.tvTransferdetailNo.text =
                getString(R.string.transfer_store_no, args.transferNo)
            rvPickingDetail.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
        when (args.transferType) {
            SHIPMENT -> setupShipmentView()
            RECEIPT -> setupReceiptView()
            PURCHASE -> setupPurchaseView()
        }
        setupListener()
        setObseverable()
    }

    private fun setupShipmentView() {
        viewModel.getTransferShipingDetail(args.transferNo)
        transferReceiptAdapter.setTransferType(args.transferType)
        viewBinding.rvPickingDetail.adapter = transferReceiptAdapter
        transferReceiptAdapter.setOnClickListener(this)
    }

    private fun setupReceiptView() {
        viewModel.getTransferReceiptDetail(args.transferNo)
        transferReceiptAdapter.setTransferType(args.transferType)
        viewBinding.rvPickingDetail.adapter = transferReceiptAdapter
        transferReceiptAdapter.setOnClickListener(this)
    }

    private fun setupPurchaseView() {
        viewModel.getPurchaseOrderDetail(args.transferNo)
        viewBinding.rvPickingDetail.adapter = purchaseDetailLineAdapter
        purchaseDetailLineAdapter.setOnClickListener(this)
    }

    private fun setObseverable() {
        when (args.transferType) {
            SHIPMENT, RECEIPT -> viewModel.transferShipmentRepository.getLineListFromHeaderLiveData(
                args.transferNo)
                .observe(viewLifecycleOwner, {
                    transferReceiptAdapter.update(it.toMutableList())
                })
            PURCHASE -> {
                viewModel.purchaseOrderRepository.getPurchaseOrderLineByNo(args.transferNo)
                    .observe(viewLifecycleOwner, {
                        purchaseDetailLineAdapter.update(it.toMutableList())
                    })
            }
        }
        viewModel.transferListViewState.observe(viewLifecycleOwner, {
            when (it) {
                is TransferDetailViewModel.TransferListViewState.SuccessGetLocalData -> {
                    setupMainViewShipment(it.value)
                }
                is TransferDetailViewModel.TransferListViewState.ErrorGetLocalData -> {
                    context?.showLongToast(it.message)
                }
                is TransferDetailViewModel.TransferListViewState.SuccessGetPickingLineData -> {
                    transferReceiptAdapter.update(it.values)
                }
                is TransferDetailViewModel.TransferListViewState.SuccessGetReceiptLocalData -> {
                    setupViewReceipt(it.values)
                }
                is TransferDetailViewModel.TransferListViewState.SuccessGetPurchaseData -> {
                    setupMainViewPurchase(it.value)
                }
            }
        })
    }

    private fun setupMainViewPurchase(value: PurchaseOrderHeader) {
        with(value) {
            viewBinding.includeTransferDetail.tvTransferdetailStatus.text =
                getString(R.string.transfer_store_status, status)
        }
    }

    private fun setupMainViewShipment(value: TransferShipmentHeader) {
        with(value) {
            viewBinding.includeTransferDetail.tvTransferdetailStatus.text =
                getString(R.string.transfer_store_status, status)
        }
    }

    private fun setupViewReceipt(value: TransferReceiptHeader) {
        with(value) {
            viewBinding.includeTransferDetail.tvTransferdetailStatus.text =
                getString(R.string.transfer_store_status, status)
        }
    }

    private fun setupListener() {
        with(viewBinding) {
            toolbarTransferDetail.setNavigationOnClickListener {
                view?.findNavController()?.popBackStack()
            }
            fabInputTransfer.setOnClickListener {
                val bottomSheetFragment =
                    ScanInputTransferDialog.newInstance(args.transferNo, args.transferType)
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            }
            fabManualInputTransfer.setOnClickListener {
                val action =
                    TransferDetailFragmentDirections.actionTransferDetailFragmentToTransferInputFragment(
                        args.transferNo, null, args.transferType)
                view?.findNavController()?.navigate(action)
            }
            includeTransferDetail.btnSubmit.setOnClickListener {
                showPostDialog()
            }
        }
    }

    private fun showPostDialog() {
        val dialog = PickingPostDialog.newInstance(args.transferType)
        dialog.show(requireActivity().supportFragmentManager, dialog.tag)
    }

    override fun onclicklistener(pickingListLineValue: TransferShipmentLine) {
        val action =
            TransferDetailFragmentDirections.actionTransferDetailFragmentToTransferInputFragment(
                args.transferNo, pickingListLineValue.itemIdentifier, args.transferType)
        view?.findNavController()?.navigate(action)
    }

    override fun onclicklistener(value: PurchaseOrderLine) {
        val action =
            TransferDetailFragmentDirections.actionTransferDetailFragmentToTransferInputFragment(
                args.transferNo, value.itemIdentifier, args.transferType)
        view?.findNavController()?.navigate(action)
    }
}
