package dynamia.com.barcodescanner.ui.receipt.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.receipt.adapter.ReceiptImportLineAdapter
import dynamia.com.barcodescanner.ui.receipt.adapter.ReceiptLocalLineAdapter
import dynamia.com.core.util.Constant
import dynamia.com.core.util.EventObserver
import dynamia.com.core.util.showToast
import dynamia.com.core.util.toNormalDate
import kotlinx.android.synthetic.main.receipt_detail_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReceiptDetailFragment : Fragment() {
    private val args: ReceiptDetailFragmentArgs by navArgs()
    private val viewModel: ReceiptDetailViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.receipt_detail_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupView()
        setupListener()
    }

    private fun setupView() {
        tv_receipt_detail_po.text = getString(R.string.po_title_header, args.poNo)
        when (args.source) {
            Constant.RECEIPT_LOCAL -> {
                viewModel.receiptLocalRepository.getReceiptLocalHeader(args.poNo)
                    .observe(viewLifecycleOwner,
                        Observer {
                            nil_vendor_name.setText(it.buyFromVendorName)
                            nil_expected_receipt_date.setText(it.expectedReceiptDate.toNormalDate())
                            nil_project_code.setText(it.buyFromVendorNo)
                        })
                viewModel.receiptLocalRepository.getAllReceiptLocalLine(args.poNo)
                    .observe(viewLifecycleOwner,
                        Observer { receiptListLines ->
                            with(rv_receipt_line) {
                                adapter = ReceiptLocalLineAdapter(receiptListLines.toMutableList())
                                layoutManager =
                                    LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                            }
                        })
            }
            Constant.RECEIPT_IMPORT -> {
                viewModel.receiptImportRepository.getReceiptImportHeader(args.poNo)
                    .observe(viewLifecycleOwner,
                        Observer {
                            nil_vendor_name.setText(it.buyFromVendorName)
                            nil_expected_receipt_date.setText(it.postingDate.toNormalDate())
                            nil_project_code.setText(it.purchaseOrderNo)
                        })
                viewModel.receiptImportRepository.getAllReceiptImportLine(args.poNo)
                    .observe(viewLifecycleOwner,
                        Observer { receiptImportLines ->
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

    private fun setupListener() {
        cv_post.setOnClickListener {
            when (args.source) {
                Constant.RECEIPT_IMPORT -> {
                    viewModel.postReceiptImportData()
                    viewModel.postImportMessage.observe(viewLifecycleOwner, EventObserver {
                        context?.showToast(it)
                    })
                }
                Constant.RECEIPT_LOCAL -> {
                    viewModel.postReceiptLocalData()
                    viewModel.postLocalMessage.observe(viewLifecycleOwner, EventObserver {
                        context?.showToast(it)
                    })
                }
            }
        }
        cv_back.setOnClickListener {
            findNavController().popBackStack()
        }
        cv_receipt.setOnClickListener {
            val action =
                ReceiptDetailFragmentDirections.actionReceiptDetailFragmentToReceiptInputFragment(
                    args.poNo,
                    args.source
                )
            findNavController().navigate(action)
        }
    }


}
