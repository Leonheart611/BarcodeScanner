package dynamia.com.barcodescanner.ui.receipt

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
import dynamia.com.barcodescanner.ui.receipt.adapter.ReceiptImportItemAdapter
import dynamia.com.barcodescanner.ui.receipt.adapter.ReceiptLocalItemAdapter
import dynamia.com.core.util.Constant
import dynamia.com.core.util.EventObserver
import dynamia.com.core.util.showToast
import kotlinx.android.synthetic.main.header_layout.*
import kotlinx.android.synthetic.main.receipt_detail_fragment.*
import kotlinx.android.synthetic.main.receipt_fragment.*
import kotlinx.android.synthetic.main.receipt_fragment.cv_back
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReceiptFragment : Fragment(), ReceiptImportItemAdapter.ReceiptImportListener,
    ReceiptLocalItemAdapter.OnReceiptLocalListener {
    private val viewModel: ReceiptViewModel by viewModel()
    private val args: ReceiptFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.receipt_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupView()
        setListener()
        setRecylerView()
    }

    private fun setupView() {
        tv_employee_name.text = viewModel.getEmployeeName()
        when (args.source) {
            Constant.RECEIPT_LOCAL -> {
                tv_title_header.text = getString(R.string.receipt_local_header)
            }
            Constant.RECEIPT_IMPORT -> {
                tv_title_header.text = getString(R.string.receipt_import_header)
            }
        }
    }

    private fun setListener() {
        cv_back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setRecylerView() {
        when (args.source) {
            Constant.RECEIPT_LOCAL -> {
                viewModel.receiptLocalRepository.getAllReceiptLocalHeader()
                    .observe(viewLifecycleOwner,
                        Observer { receiptLocalHeaders ->
                            with(rv_receipt_list) {
                                adapter = ReceiptLocalItemAdapter(
                                    receiptLocalHeaders.toMutableList(),
                                    this@ReceiptFragment
                                )
                                layoutManager =
                                    LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                            }
                        })
            }
            Constant.RECEIPT_IMPORT -> {
                viewModel.receiptImportRepository.getAllReceiptImportHeader()
                    .observe(viewLifecycleOwner,
                        Observer { receiptImportHeaders ->
                            with(rv_receipt_list) {
                                adapter = ReceiptImportItemAdapter(
                                    receiptImportHeaders.toMutableList(),
                                    this@ReceiptFragment
                                )
                                layoutManager =
                                    LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                            }
                        })
            }
        }
    }

    override fun onReceiptImportClickListener(documentNo: String) {
        val action = ReceiptFragmentDirections.actionReceiptFragmentToReceiptDetailFragment(
            Constant.RECEIPT_IMPORT,
            documentNo
        )
        findNavController().navigate(action)
    }

    override fun onReceiptLocalClicklistener(documentNo: String) {
        val action = ReceiptFragmentDirections.actionReceiptFragmentToReceiptDetailFragment(
            Constant.RECEIPT_LOCAL,
            documentNo
        )
        findNavController().navigate(action)
    }

}
