package dynamia.com.barcodescanner.ui.transferstore.transferdetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.databinding.TransferDetailFragmentBinding
import dynamia.com.barcodescanner.ui.transferstore.TransferType.*
import dynamia.com.barcodescanner.ui.transferstore.adapter.InventoryLineListAdapter
import dynamia.com.barcodescanner.ui.transferstore.adapter.PurchaseDetailLineAdapter
import dynamia.com.barcodescanner.ui.transferstore.adapter.TransferDetailLineAdapter
import dynamia.com.core.base.BaseFragmentBinding
import dynamia.com.core.data.entinty.*
import dynamia.com.core.util.showLongToast

@AndroidEntryPoint
class TransferDetailFragment :
    BaseFragmentBinding<TransferDetailFragmentBinding>(TransferDetailFragmentBinding::inflate),
    TransferDetailLineAdapter.OnTransferLineCLicklistener,
    PurchaseDetailLineAdapter.OnPurchaseLineClicklistener,
    InventoryLineListAdapter.OnclickInventoryLineAdapter {
    private val viewModel: TransferDetailViewModel by viewModels()
    private val args: TransferDetailFragmentArgs by navArgs()
    private val transferReceiptAdapter = TransferDetailLineAdapter()
    private val purchaseDetailLineAdapter = PurchaseDetailLineAdapter()
    private val inventoryLineAdapter = InventoryLineListAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewBinding) {
            toolbarTransferDetail.title = viewModel.getCompanyName()
            includeTransferDetail.tvTransferdetailNo.text =
                getString(R.string.transfer_store_no, args.transferNo)
            rvPickingDetail.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)

            rvPickingDetail.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisible: Int = layoutManager.findLastVisibleItemPosition()
                    val endHasBeenReached = lastVisible >= totalItemCount

                    if (totalItemCount > 0 && endHasBeenReached) {
                        when (args.transferType) {
                            SHIPMENT, RECEIPT -> viewModel.transferShipmentRepository.getLineListFromHeaderLiveData(
                                args.transferNo, totalItemCount + 20
                            ).observe(viewLifecycleOwner, {
                                transferReceiptAdapter.submitList(it)
                            })
                            PURCHASE -> {
                                viewModel.purchaseOrderRepository.getPurchaseOrderLineByNo(
                                    args.transferNo,
                                    totalItemCount + 20
                                )
                                    .observe(viewLifecycleOwner, {
                                        purchaseDetailLineAdapter.submitList(it)
                                    })
                            }
                            INVENTORY -> {
                                viewModel.inventoryRepository.getAllInventoryPickLine(
                                    args.transferNo,
                                    totalItemCount + 20
                                )
                                    .observe(viewLifecycleOwner, {
                                        inventoryLineAdapter.submitList(it)
                                    })
                            }
                        }
                    }
                }
            })

        }
        when (args.transferType) {
            SHIPMENT -> setupShipmentView()
            RECEIPT -> setupReceiptView()
            PURCHASE -> setupPurchaseView()
            INVENTORY -> setInventoryView()
        }
        setupListener()
        setObseverable()
    }

    private fun setInventoryView() {
        viewModel.getInventoryHeader(args.transferNo)
        viewModel.getInventoryScanQty(args.transferNo)
        viewBinding.rvPickingDetail.adapter = inventoryLineAdapter
    }

    private fun setupShipmentView() {
        viewModel.getTransferShipingDetail(args.transferNo)
        viewModel.getTransferDetailScanQty(args.transferNo)
        transferReceiptAdapter.setTransferType(args.transferType)
        viewBinding.rvPickingDetail.adapter = transferReceiptAdapter
        transferReceiptAdapter.setOnClickListener(this)
    }

    private fun setupReceiptView() {
        viewModel.getTransferReceiptDetail(args.transferNo)
        viewModel.getTransferReceiptScanQty(args.transferNo)
        transferReceiptAdapter.setTransferType(args.transferType)
        viewBinding.rvPickingDetail.adapter = transferReceiptAdapter
        transferReceiptAdapter.setOnClickListener(this)
    }

    private fun setupPurchaseView() {
        viewModel.getPurchaseOrderDetail(args.transferNo)
        viewModel.getPurchaseScanQty(args.transferNo)
        viewBinding.rvPickingDetail.adapter = purchaseDetailLineAdapter
        purchaseDetailLineAdapter.setOnClickListener(this)
    }


    private fun setObseverable() {
        viewModel.transferListViewState.observe(viewLifecycleOwner, {
            when (it) {
                is TransferDetailViewModel.TransferListViewState.SuccessGetLocalData -> {
                    setupMainViewShipment(it.value)
                }
                is TransferDetailViewModel.TransferListViewState.ErrorGetLocalData -> {
                    context?.showLongToast(it.message)
                }
                is TransferDetailViewModel.TransferListViewState.SuccessGetPickingLineData -> {
                    transferReceiptAdapter.submitList(it.values)
                }
                is TransferDetailViewModel.TransferListViewState.SuccessGetReceiptLocalData -> {
                    setupViewReceipt(it.values)
                }
                is TransferDetailViewModel.TransferListViewState.SuccessGetPurchaseData -> {
                    setupMainViewPurchase(it.value)
                }
                is TransferDetailViewModel.TransferListViewState.SuccessGetInventoryData -> {
                    setupMainViewInventory(it.value)
                }
                is TransferDetailViewModel.TransferListViewState.SuccessGetQtyTotal -> {
                    viewBinding.includeTransferDetail.tvDetailTotalScanCount.text =
                        "${it.data.totalAlreadyQty} / ${it.data.totalQty}"
                }
            }
        })
    }

    private fun setupMainViewInventory(value: InventoryPickHeader) {
        with(value) {
            viewBinding.includeTransferDetail.tvTransferdetailFrom.text =
                getString(R.string.transfer_store_from, destinationNo)
            viewBinding.includeTransferDetail.tvTransferdetailTo.text =
                getString(R.string.transfer_store_to, transferToCode)
            viewBinding.includeTransferDetail.tvTransferdetailDate.text =
                getString(R.string.bin_reclass_detail_date, postingDate)
        }
    }

    private fun setupMainViewPurchase(value: PurchaseOrderHeader) {
        with(value) {
            viewBinding.includeTransferDetail.tvTransferdetailTo.text =
                getString(R.string.transfer_store_status, status)
            viewBinding.includeTransferDetail.tvTransferdetailDate.text =
                getString(R.string.transfer_store_name, viewModel.getCompanyName())

        }
    }

    private fun setupMainViewShipment(value: TransferShipmentHeader) {
        with(value) {
            viewBinding.includeTransferDetail.tvTransferdetailFrom.text =
                getString(R.string.transfer_store_from, transferFromCode)
            viewBinding.includeTransferDetail.tvTransferdetailTo.text =
                getString(R.string.transfer_store_to, transferToCode)
            viewBinding.includeTransferDetail.tvTransferdetailDate.text =
                getString(R.string.bin_reclass_detail_date, postingDate)
        }
    }

    private fun setupViewReceipt(value: TransferReceiptHeader) {
        with(value) {
            viewBinding.includeTransferDetail.tvTransferdetailFrom.text =
                getString(R.string.transfer_store_from, transferFromCode)
            viewBinding.includeTransferDetail.tvTransferdetailTo.text =
                getString(R.string.transfer_store_to, transferToCode)
            viewBinding.includeTransferDetail.tvTransferdetailDate.text =
                getString(R.string.bin_reclass_detail_date, postingDate)
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
                        args.transferNo, null, args.transferType
                    )
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
                args.transferNo, pickingListLineValue.itemIdentifier, args.transferType
            )
        view?.findNavController()?.navigate(action)
    }

    override fun onclicklistener(value: PurchaseOrderLine) {
        val action =
            TransferDetailFragmentDirections.actionTransferDetailFragmentToTransferInputFragment(
                args.transferNo, value.itemIdentifier, args.transferType
            )
        view?.findNavController()?.navigate(action)
    }

    override fun onclicklistener(data: InventoryPickLine) {
        val action =
            TransferDetailFragmentDirections.actionTransferDetailFragmentToTransferInputFragment(
                args.transferNo, data.itemRefNo, args.transferType
            )
        view?.findNavController()?.navigate(action)
    }
}
