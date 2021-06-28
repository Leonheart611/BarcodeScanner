package dynamia.com.barcodescanner.ui.transferstore.transferdetail

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
import dynamia.com.barcodescanner.ui.transferstore.adapter.TransferDetailLineAdapter
import dynamia.com.core.data.entinty.TransferShipmentHeader
import dynamia.com.core.data.entinty.TransferShipmentLine
import dynamia.com.core.util.showLongToast
import kotlinx.android.synthetic.main.dialog_validate_s.*
import kotlinx.android.synthetic.main.transfer_detail_fragment.*
import kotlinx.android.synthetic.main.transfer_header_layout.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TransferDetailFragment : Fragment(), TransferDetailLineAdapter.OnTransferLineCLicklistener {
    private val viewModel: TransferDetailViewModel by viewModel()
    private val args: TransferDetailFragmentArgs by navArgs()
    val adapter = TransferDetailLineAdapter(mutableListOf())
    private var settingDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.transfer_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getTransferDetail(args.transferNo)
        //viewModel.getTransferLine(args.transferNo)
        setupView()
        setupListener()
        setObseverable()
    }

    private fun setupView() {
        tv_transferdetail_store.text =
            getString(R.string.transfer_store_name, viewModel.getCompanyName())
        toolbar_transfer_detail.title = viewModel.getCompanyName()
        tv_transferdetail_no.text = getString(R.string.transfer_store_no, args.transferNo)
        rv_picking_detail.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rv_picking_detail.adapter = adapter
        adapter.setOnClickListener(this)
    }

    private fun setObseverable() {
        viewModel.transferListViewState.observe(viewLifecycleOwner, {
            when (it) {
                is TransferDetailViewModel.TransferListViewState.SuccessGetLocalData -> {
                    setupMainView(it.value)
                }
                is TransferDetailViewModel.TransferListViewState.ErrorGetLocalData -> {
                    context?.showLongToast(it.message)
                }
                is TransferDetailViewModel.TransferListViewState.SuccessGetPickingLineData -> {
                    adapter.update(it.values)
                }
            }
        })
        viewModel.transferShipmentRepository.getLineListFromHeaderLiveData(args.transferNo)
            .observe(viewLifecycleOwner, {
                adapter.update(it.toMutableList())
            })
    }

    private fun setupMainView(value: TransferShipmentHeader) {
        with(value) {
            tv_transferdetail_status.text = getString(R.string.transfer_store_status, status)
        }
    }

    private fun setupListener() {
        toolbar_transfer_detail.setNavigationOnClickListener {
            view?.findNavController()?.popBackStack()
        }
        fab_input_transfer.setOnClickListener {
            val bottomSheetFragment = ScanInputTransferDialog.newInstance(args.transferNo)
            bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
        }
        fab_manual_input_transfer.setOnClickListener {
            val action =
                TransferDetailFragmentDirections.actionTransferDetailFragmentToTransferInputFragment(
                    args.transferNo, null)
            view?.findNavController()?.navigate(action)
        }
        btn_submit.setOnClickListener {
            showPostDialog()
        }
    }

    private fun showPostDialog() {
        val dialog = PickingPostDialog()
        dialog.show(requireActivity().supportFragmentManager, dialog.tag)
    }

    fun pickingInputDialog() {
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
                        }
                    }

                    cb_check_s_true.setOnClickListener {
                        when (cb_check_s_true.isChecked) {
                            true -> {
                                cb_check_s_false.isChecked = false
                            }
                        }
                    }

                    btn_setting_continue.setOnClickListener {
                        dismiss()
                    }
                    show()
                }
            }
        }
    }

    override fun onclicklistener(pickingListLineValue: TransferShipmentLine) {
        val action =
            TransferDetailFragmentDirections.actionTransferDetailFragmentToTransferInputFragment(
                args.transferNo, pickingListLineValue.no)
        view?.findNavController()?.navigate(action)
    }
}
