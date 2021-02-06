package dynamia.com.barcodescanner.ui.history

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.history.adapter.HistoryInputAdapter
import dynamia.com.barcodescanner.ui.history.adapter.HistoryInputImportAdapter
import dynamia.com.barcodescanner.ui.history.adapter.HistoryInputLocalAdapter
import dynamia.com.core.data.model.PickingListScanEntriesValue
import dynamia.com.core.data.model.ReceiptImportScanEntriesValue
import dynamia.com.core.data.model.ReceiptLocalScanEntriesValue
import dynamia.com.core.util.Constant
import kotlinx.android.synthetic.main.delete_confirmation_dialog.*
import kotlinx.android.synthetic.main.history_input_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryInputFragment : Fragment(), HistoryInputAdapter.OnHistorySelected,
    HistoryInputLocalAdapter.OnLocalClicklistener, HistoryInputImportAdapter.OnImportClicklistener {

    private val viewModel: HistoryInputViewModel by viewModel()
    private val args: HistoryInputFragmentArgs by navArgs()
    private val pickingListAdapter = HistoryInputAdapter(mutableListOf(), this)
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
                    tv_picking_detail_so.text = getString(R.string.pickinglist_all_history)
                    viewModel.pickingListRepository.getAllPickingListScanLiveData()
                        .observe(viewLifecycleOwner, Observer {
                            pickingListAdapter.updateData(it.toMutableList())
                        })
                } else {
                    tv_picking_detail_so.text =
                        getString(R.string.picklistno_title, args.pickingListNo)
                    if (args.partNo.trim().isEmpty()) {
                        viewModel.pickingListRepository.getPickingListScanEntries(args.pickingListNo)
                            .observe(viewLifecycleOwner, Observer {
                                pickingListAdapter.updateData(it.toMutableList())
                            })
                    } else {
                        viewModel.pickingListRepository.getPickingListandPartNo(
                            args.pickingListNo,
                            args.partNo
                        ).observe(viewLifecycleOwner, Observer {
                            pickingListAdapter.updateData(it.toMutableList())
                        })
                    }

                }
            }
            Constant.RECEIPT_IMPORT -> {
                if (args.showAll) {
                    tv_picking_detail_so.text = getString(R.string.import_and_local_all_history)
                    viewModel.receiptImportRepository.getAllReceiptImportScanEntries()
                        .observe(
                            viewLifecycleOwner,
                            Observer { importListAdapter.updateData(it.toMutableList()) })
                } else {
                    tv_picking_detail_so.text =
                        getString(R.string.receipthistory_title, args.pickingListNo)
                    viewModel.receiptImportRepository.getReceiptImportScanEntries(args.pickingListNo)
                        .observe(viewLifecycleOwner,
                            Observer { importListAdapter.updateData(it.toMutableList()) })
                }
            }
            Constant.RECEIPT_LOCAL -> {
                if (args.showAll) {
                    tv_picking_detail_so.text = getString(R.string.import_and_local_all_history)
                    viewModel.receiptLocalRepository.getAllReceiptLocalScanEntries()
                        .observe(
                            viewLifecycleOwner,
                            Observer { localListAdapter.updateData(it.toMutableList()) })
                } else {
                    tv_picking_detail_so.text =
                        getString(R.string.receipthistory_title, args.pickingListNo)
                    viewModel.receiptLocalRepository.getReceiptLocalScanEntries(args.pickingListNo)
                        .observe(viewLifecycleOwner,
                            Observer { localListAdapter.updateData(it.toMutableList()) })
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
