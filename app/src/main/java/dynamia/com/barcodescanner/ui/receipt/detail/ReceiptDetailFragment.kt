package dynamia.com.barcodescanner.ui.receipt.detail

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.receipt.adapter.ReceiptImportLineAdapter
import dynamia.com.barcodescanner.ui.receipt.adapter.ReceiptLocalLineAdapter
import dynamia.com.core.data.model.ReceiptImportHeaderValue
import dynamia.com.core.data.model.ReceiptLocalHeaderValue
import dynamia.com.core.util.Constant
import dynamia.com.core.util.toNormalDate
import kotlinx.android.synthetic.main.dialog_validate_s.*
import kotlinx.android.synthetic.main.receipt_detail_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReceiptDetailFragment : Fragment() {

    private val args: ReceiptDetailFragmentArgs by navArgs()
    private val viewModel: ReceiptDetailViewModel by viewModel()
    private var receiptImportHeader: ReceiptImportHeaderValue? = null
    private var receiptLocalHeader: ReceiptLocalHeaderValue? = null
    private var settingDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.receipt_detail_fragment, container, false)


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupView()
        setupListener()
    }

    private fun setupView() {
        tv_receipt_detail_po.text = getString(R.string.po_title_header, args.documentNo)
        when (args.source) {
            Constant.RECEIPT_LOCAL -> {
                viewModel.receiptLocalRepository.getReceiptLocalHeader(args.documentNo)
                    .observe(viewLifecycleOwner, {
                        nil_vendor_name.setText(it.buyFromVendorName)
                        nil_expected_receipt_date.setText(it.expectedReceiptDate.toNormalDate())
                        nil_project_code.setText(it.buyFromVendorNo)
                        receiptLocalHeader = it
                    })
                viewModel.receiptLocalRepository.getAllReceiptLocalLine(args.documentNo)
                    .observe(viewLifecycleOwner, { receiptListLines ->
                        with(rv_receipt_line) {
                            adapter = ReceiptLocalLineAdapter(receiptListLines.toMutableList())
                            layoutManager =
                                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                        }
                    })
            }
            Constant.RECEIPT_IMPORT -> {
                viewModel.receiptImportRepository.getReceiptImportHeader(args.documentNo)
                    .observe(viewLifecycleOwner, {
                        nil_vendor_name.setText(it.buyFromVendorName)
                        nil_expected_receipt_date.setText(it.postingDate.toNormalDate())
                        nil_project_code.setText(it.purchaseOrderNo)
                        receiptImportHeader = it
                    })
                viewModel.receiptImportRepository.getAllReceiptImportLine(args.documentNo)
                    .observe(viewLifecycleOwner,
                        { receiptImportLines ->
                            with(rv_receipt_line) {
                                adapter =
                                    ReceiptImportLineAdapter(receiptImportLines.toMutableList())
                                layoutManager =
                                    LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                            }
                        })

            }
        }
    }

    private fun showPostDialog(isFrom: String) {
        val dialog = ReceiptPostDialog.newInstance(isFrom)
        dialog.show(requireActivity().supportFragmentManager, dialog.tag)
    }

    private fun setupListener() {
        cv_post.setOnClickListener {
            when (args.source) {
                Constant.RECEIPT_IMPORT -> {
                    showPostDialog(Constant.RECEIPT_IMPORT)
                }
                Constant.RECEIPT_LOCAL -> {
                    showPostDialog(Constant.RECEIPT_LOCAL)
                }
            }
        }
        cv_back.setOnClickListener {
            findNavController().popBackStack()
        }
        cv_receipt.setOnClickListener {
            receiptDetailDialog()
        }
        tb_receipt_detail.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_search -> {
                    val action =
                        ReceiptDetailFragmentDirections.actionReceiptDetailFragmentToReceiptSearchFragment(
                            PoNo = args.documentNo,
                            source = args.source
                        )
                    view?.findNavController()?.navigate(action)
                    true
                }
                R.id.menu_history -> {
                    val action =
                        ReceiptDetailFragmentDirections.actionReceiptDetailFragmentToHistoryInputFragment(
                            args.documentNo, args.source
                        )
                    view?.findNavController()?.navigate(action)
                    true
                }
                else -> false
            }
        }
    }

    fun receiptDetailDialog() {
        context?.let { context ->
            settingDialog = Dialog(context)
            settingDialog?.let { dialog ->
                with(dialog) {
                    setContentView(dynamia.com.barcodescanner.R.layout.dialog_validate_s)
                    window
                        ?.setLayout(
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
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

                    btn_setting_continue.setOnClickListener { // Check Validate S
                        dismiss()
                        val action = receiptImportHeader?.let {
                            if (cb_check_s_true.isChecked) {
                                ReceiptDetailFragmentDirections.actionReceiptDetailFragmentToReceiptInputFragment(
                                    documentNo = args.documentNo,
                                    source = args.source,
                                    poNo = it.purchaseOrderNo,
                                    validateS = true
                                )
                            } else {
                                ReceiptDetailFragmentDirections.actionReceiptDetailFragmentToReceiptInputFragment(
                                    documentNo = args.documentNo,
                                    source = args.source,
                                    poNo = it.purchaseOrderNo,
                                    validateS = false
                                )
                            }
                        } ?: run {
                            receiptLocalHeader?.let {
                                if (cb_check_s_true.isChecked) {
                                    ReceiptDetailFragmentDirections.actionReceiptDetailFragmentToReceiptInputFragment(
                                        documentNo = args.documentNo,
                                        source = args.source,
                                        poNo = it.no,
                                        validateS = true
                                    )
                                } else {
                                    ReceiptDetailFragmentDirections.actionReceiptDetailFragmentToReceiptInputFragment(
                                        documentNo = args.documentNo,
                                        source = args.source,
                                        poNo = it.no,
                                        validateS = false
                                    )
                                }
                            }
                        }
                        action?.let { findNavController().navigate(it) }
                    }
                    show()
                }
            }
        }
    }

}
