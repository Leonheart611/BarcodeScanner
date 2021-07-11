package dynamia.com.barcodescanner.ui.stockopname

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.history.HistoryType
import dynamia.com.barcodescanner.ui.stockopname.adapter.StockOpnameAdapter
import dynamia.com.barcodescanner.ui.transferstore.TransferType
import dynamia.com.barcodescanner.ui.transferstore.transferdetail.PickingPostDialog
import dynamia.com.barcodescanner.ui.transferstore.transferdetail.ScanInputTransferDialog
import dynamia.com.barcodescanner.ui.transferstore.transferinput.TransferInputFragmentDirections
import dynamia.com.core.data.entinty.StockOpnameData
import kotlinx.android.synthetic.main.stock_opname_fragment.*
import kotlinx.android.synthetic.main.transfer_input_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class StockOpnameFragment : Fragment(), StockOpnameAdapter.OnStockClicklistener {

    private val viewModel: StockOpnameViewModel by viewModel()

    private val stockOpnameAdapter = StockOpnameAdapter(mutableListOf(), this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.stock_opname_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupObseverable()
        setclicklistener()
    }

    private fun setclicklistener() {
        fab_manual_input_stock_opname.setOnClickListener {
            val action =
                StockOpnameFragmentDirections.actionStockOpnameFragmentToTransferInputFragment(
                    transferNo = "",
                    barcodeNo = null,
                    TransferType.STOCKOPNAME)
            view?.findNavController()?.navigate(action)
        }
        fab_input_stock_opname.setOnClickListener {
            val bottomSheetFragment =
                ScanInputTransferDialog.newInstance("", TransferType.STOCKOPNAME)
            bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
        }
    }

    private fun setupObseverable() {
        viewModel.repository.getALlStockOpname().observe(viewLifecycleOwner, {
            stockOpnameAdapter.updateData(it.toMutableList())
        })
    }

    fun setupView() {
        tb_stock_opname.title = viewModel.getCompanyName()
        tb_stock_opname.setNavigationOnClickListener {
            view?.findNavController()?.popBackStack()
        }
        fab_upload_stock.setOnClickListener {
            val dialog = StockOpnamePostDialog()
            dialog.show(requireActivity().supportFragmentManager, dialog.tag)
        }
        tb_stock_opname.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.history_data -> {
                    val action =
                        StockOpnameFragmentDirections.actionStockOpnameFragmentToHistoryInputFragment(
                            showAll = true,
                            historyType = HistoryType.STOCKOPNAME,
                            documentNo = null, lineNo = 0)
                    view?.findNavController()?.navigate(action)
                    true
                }
                else -> false
            }
        }
        with(rv_stockopname) {
            layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = stockOpnameAdapter
        }
    }

    override fun onStockClicklistener(data: StockOpnameData) {
        val action =
            StockOpnameFragmentDirections.actionStockOpnameFragmentToTransferInputFragment(
                transferNo = "",
                barcodeNo = data.barcode,
                TransferType.STOCKOPNAME)
        view?.findNavController()?.navigate(action)
    }

}