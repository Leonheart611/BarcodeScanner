package dynamia.com.barcodescanner.ui.history

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import dynamia.com.barcodescanner.ui.history.adapter.HistoryInputImportAdapter
import dynamia.com.barcodescanner.ui.history.adapter.HistoryInputLocalAdapter
import dynamia.com.barcodescanner.ui.history.adapter.HistoryTransferInputAdapter
import dynamia.com.core.data.model.PickingListScanEntriesValue
import dynamia.com.core.data.model.ReceiptImportScanEntriesValue
import dynamia.com.core.data.model.ReceiptLocalScanEntriesValue
import dynamia.com.core.util.Constant
import kotlinx.android.synthetic.main.delete_confirmation_dialog.*
import kotlinx.android.synthetic.main.history_input_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryInputFragment : Fragment(), HistoryTransferInputAdapter.OnHistorySelected,
    HistoryInputLocalAdapter.OnLocalClicklistener, HistoryInputImportAdapter.OnImportClicklistener {

    private val viewModel: HistoryInputViewModel by viewModel()
    private val args: HistoryInputFragmentArgs by navArgs()
    private val pickingListAdapter = HistoryTransferInputAdapter(mutableListOf(), this)
    private val importListAdapter = HistoryInputImportAdapter(mutableListOf(), this)
    private val localListAdapter = HistoryInputLocalAdapter(mutableListOf(), this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.history_input_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupRecylerView()
        setupView()
        setupListener()
    }

    private fun setupRecylerView() {
        with(rv_input_history) {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = when (args.source) {
                Constant.PICKING_LIST -> pickingListAdapter
                Constant.RECEIPT_IMPORT -> importListAdapter
                else -> localListAdapter
            }
        }
    }

    private fun setupView() {
        when (args.source) {
            Constant.PICKING_LIST -> {
                if (args.showAll) {
                    tv_transfer_input.text = getString(R.string.pickinglist_all_history)
                    viewModel.pickingListRepository.getAllPickingListScanLiveData()
                        .observe(viewLifecycleOwner, {
                            //pickingListAdapter.updateData(it.toMutableList())
                        })
                } else {
                    tv_transfer_input.text =
                        getString(R.string.picklistno_title, args.pickingListNo)
                    args.partNo?.let { partNo ->
                        tv_pn_no.text = getString(R.string.part_no_history, partNo)
                        viewModel.pickingListRepository.getPickingListandPartNo(
                            args.pickingListNo,
                            partNo, args.lineNo
                        ).observe(viewLifecycleOwner, {
                            //pickingListAdapter.updateData(it.toMutableList())
                        })
                    } ?: run {
                        viewModel.pickingListRepository.getPickingListScanEntries(args.pickingListNo)
                            .observe(viewLifecycleOwner, {
                                //pickingListAdapter.updateData(it.toMutableList())
                            })
                    }
                }
            }
            Constant.RECEIPT_IMPORT -> {
                if (args.showAll) {
                    tv_transfer_input.text = getString(R.string.import_and_local_all_history)
                    viewModel.receiptImportRepository.getAllReceiptImportScanEntries()
                        .observe(viewLifecycleOwner, {
                            importListAdapter.updateData(it.toMutableList())
                        })
                } else {
                    tv_transfer_input.text =
                        getString(R.string.receipthistory_title, args.pickingListNo)

                    args.documentNo?.let {
                        tv_pn_no.text = getString(R.string.part_no_history, args.partNo)
                        viewModel.receiptImportRepository.getFilteredImportScanEntries(
                            it,
                            args.lineNo, args.partNo ?: ""
                        ).observe(viewLifecycleOwner, { data ->
                            importListAdapter.updateData(data.toMutableList())
                        })
                    } ?: run {
                        viewModel.receiptImportRepository.getReceiptImportScanEntries(args.pickingListNo)
                            .observe(viewLifecycleOwner, { data ->
                                importListAdapter.updateData(data.toMutableList())
                            })
                    }
                }
            }
            Constant.RECEIPT_LOCAL -> {
                if (args.showAll) {
                    tv_transfer_input.text = getString(R.string.import_and_local_all_history)
                    viewModel.receiptLocalRepository.getAllReceiptLocalScanEntries()
                        .observe(viewLifecycleOwner, {
                            localListAdapter.updateData(it.toMutableList())
                        })
                } else {
                    tv_transfer_input.text =
                        getString(R.string.receipthistory_title, args.pickingListNo)

                    args.documentNo?.let {
                        tv_pn_no.text = getString(R.string.part_no_history, args.partNo)
                        viewModel.receiptLocalRepository.getFilteredLocalScanEntries(
                            it,
                            args.lineNo, args.partNo ?: ""
                        ).observe(viewLifecycleOwner, { data ->
                            localListAdapter.updateData(data.toMutableList())
                        })
                    } ?: run {
                        viewModel.receiptLocalRepository.getReceiptLocalScanEntries(args.pickingListNo)
                            .observe(viewLifecycleOwner, {
                                localListAdapter.updateData(it.toMutableList())
                            })
                    }
                }
            }
        }
    }

    private fun setupListener() {
        cv_back.setOnClickListener {
            view?.findNavController()?.popBackStack()
        }
    }

    override fun onHistorySelectDelete(value: PickingListScanEntriesValue) {
        context?.let { context ->
            val dialog = Dialog(context)
            with(dialog) {
                setContentView(R.layout.delete_confirmation_dialog)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                window
                    ?.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                btn_delete.setOnClickListener {
                    viewModel.pickingListRepository.deletePickingListScanEntries(value)
                    dismiss()
                    setupView()
                }
                btn_cancel.setOnClickListener {
                    dismiss()
                }
                show()
            }
        }
    }

    override fun onLocalClicklistener(value: ReceiptLocalScanEntriesValue) {
        context?.let { context ->
            val dialog = Dialog(context)
            with(dialog) {
                setContentView(R.layout.delete_confirmation_dialog)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                window
                    ?.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                btn_delete.setOnClickListener {
                    viewModel.receiptLocalRepository.deleteReceiptLocalScanEntry(value)
                    dismiss()
                    setupView()
                }
                btn_cancel.setOnClickListener {
                    dismiss()
                }
                show()
            }
        }
    }

    override fun onLocalClicklistener(value: ReceiptImportScanEntriesValue) {
        context?.let { context ->
            val dialog = Dialog(context)
            with(dialog) {
                setContentView(R.layout.delete_confirmation_dialog)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                window
                    ?.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                btn_delete.setOnClickListener {
                    viewModel.receiptImportRepository.deleteReceiptImportScanEntry(value)
                    dismiss()
                    setupView()
                }
                btn_cancel.setOnClickListener {
                    dismiss()
                }
                show()
            }
        }
    }

}
