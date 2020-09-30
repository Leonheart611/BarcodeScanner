package dynamia.com.barcodescanner.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.pickinglist.adapter.PickingDetailAdapter
import dynamia.com.barcodescanner.ui.receipt.adapter.ReceiptImportLineAdapter
import dynamia.com.barcodescanner.ui.receipt.adapter.ReceiptLocalLineAdapter
import dynamia.com.core.util.Constant
import kotlinx.android.synthetic.main.receipt_search_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()
    private val arg: SearchFragmentArgs by navArgs()
    private val pickinglistAdapter = PickingDetailAdapter(mutableListOf())
    private val receiptImportLineAdapter = ReceiptImportLineAdapter(mutableListOf())
    private val receiptLocalLineAdapter = ReceiptLocalLineAdapter(mutableListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.receipt_search_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupView()
        setupRecylerView()
        setupListener()
        setupObserverable()
    }

    private fun setupView() {
        tb_search.title = getString(R.string.search_title_toolbar, arg.PoNo)
    }

    private fun setupObserverable() {
        when (arg.source) {
            Constant.PICKING_LIST -> {
                viewModel.pickingListRepository.getAllPickingListLine(arg.PoNo)
                    .observe(viewLifecycleOwner,
                        Observer {
                            pickinglistAdapter.update(it.toMutableList())
                        })
            }
            Constant.RECEIPT_LOCAL -> {
                viewModel.receiptLocalRepository.getAllReceiptLocalLine(arg.PoNo)
                    .observe(viewLifecycleOwner,
                        Observer {
                            receiptLocalLineAdapter.update(it.toMutableList())

                        })
            }
            Constant.RECEIPT_IMPORT -> {
                viewModel.receiptImportRepository.getAllReceiptImportLine(arg.PoNo)
                    .observe(viewLifecycleOwner,
                        Observer {
                            receiptImportLineAdapter.update(it.toMutableList())
                        })
            }
        }

    }

    private fun setupRecylerView() {
        with(rv_result) {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = when (arg.source) {
                Constant.PICKING_LIST -> pickinglistAdapter
                Constant.RECEIPT_LOCAL -> receiptLocalLineAdapter
                else -> receiptImportLineAdapter
            }
        }
    }

    private fun setupListener() {
        tb_search.setNavigationOnClickListener {
            view?.findNavController()?.popBackStack()
        }
        et_part_no.doAfterTextChanged {
            when (arg.source) {
                Constant.PICKING_LIST -> pickinglistAdapter.filter.filter(it.toString())
                Constant.RECEIPT_LOCAL -> receiptLocalLineAdapter.filter.filter(it.toString())
                Constant.RECEIPT_IMPORT -> receiptImportLineAdapter.filter.filter(it.toString())
            }
        }
    }
}