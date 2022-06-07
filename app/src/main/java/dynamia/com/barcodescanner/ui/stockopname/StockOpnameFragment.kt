package dynamia.com.barcodescanner.ui.stockopname

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.databinding.StockOpnameFragmentBinding
import dynamia.com.barcodescanner.ui.history.HistoryType
import dynamia.com.barcodescanner.ui.stockopname.adapter.StockOpnameAdapter
import dynamia.com.barcodescanner.ui.transferstore.TransferType
import dynamia.com.core.base.BaseFragmentBinding
import dynamia.com.core.data.entinty.StockOpnameData

@AndroidEntryPoint
class StockOpnameFragment :
    BaseFragmentBinding<StockOpnameFragmentBinding>(StockOpnameFragmentBinding::inflate),
    StockOpnameAdapter.OnStockClicklistener {

    private val viewModel: StockOpnameViewModel by viewModels()

    private val stockOpnameAdapter = StockOpnameAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupObseverable()
        setclicklistener()
    }

    private fun setclicklistener() {
        with(viewBinding) {
            fabManualInputStockOpname.setOnClickListener {
                val action =
                    StockOpnameFragmentDirections.actionStockOpnameFragmentToStockOpnameInputFragment2(
                        barcode = ""
                    )
                view?.findNavController()?.navigate(action)
            }
            fabInputStockOpname.setOnClickListener {
                val bottomSheetFragment =
                    ScanInputStockOpnameDialog.newInstance("", TransferType.STOCKOPNAME)
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            }
        }
    }

    private fun setupObseverable() {
        viewModel.repository.getALlStockOpname().observe(viewLifecycleOwner) {
            stockOpnameAdapter.submitList(it)
        }
    }

    fun setupView() {
        with(viewBinding) {
            tbStockOpname.title = viewModel.getCompanyName()
            tbStockOpname.setNavigationOnClickListener {
                view?.findNavController()?.popBackStack()
            }
            fabUploadStock.setOnClickListener {
                val dialog = StockOpnamePostDialog()
                dialog.show(requireActivity().supportFragmentManager, dialog.tag)
            }
            tbStockOpname.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.stock_opname_history -> {
                        val action =
                            StockOpnameFragmentDirections.actionStockOpnameFragmentToHistoryInputFragment(
                                showAll = true,
                                historyType = HistoryType.STOCKOPNAME,
                                documentNo = null, lineNo = 0
                            )
                        view?.findNavController()?.navigate(action)
                        true
                    }

                    R.id.stock_opname_delete_box -> {
                        val action =
                            StockOpnameFragmentDirections.actionStockOpnameFragmentToStockOpnameDeleteFragment()
                        view?.findNavController()?.navigate(action)
                        true
                    }
                    else -> false
                }
            }
            with(rvStockopname) {
                layoutManager =
                    LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = stockOpnameAdapter
            }
            viewModel.repository.getStockOpnameAlreadyScanTotal().observe(viewLifecycleOwner, {
                tvStockOpnameTotalScan.text =
                    getString(R.string.stock_opname_total_scanned, it.toString())
            })
        }
    }

    override fun onStockClicklistener(data: StockOpnameData) {
        val action =
            StockOpnameFragmentDirections.actionStockOpnameFragmentToStockOpnameInputFragment2(
                id = data.id ?: 0,
                barcode = data.itemIdentifier
            )
        view?.findNavController()?.navigate(action)
    }

}