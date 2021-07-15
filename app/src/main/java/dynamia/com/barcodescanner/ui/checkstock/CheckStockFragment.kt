package dynamia.com.barcodescanner.ui.checkstock

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.MainActivity
import dynamia.com.core.util.showLongToast
import kotlinx.android.synthetic.main.check_stock_fragment.*
import kotlinx.android.synthetic.main.scan_stock_search_dialog.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CheckStockFragment : Fragment() {

    companion object {
        const val SEARCH_ITEM_IDENTIFIERS = "contains(Item_Identifiers,"
        const val SEARCH_ITEM_NO = "contains(Item_No,"
    }

    private var stockAdapter = StockCheckAdapter(mutableListOf())
    private var dialog: Dialog? = null
    private val viewModel: CheckStockViewModel by viewModel()
    private var activity: MainActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.check_stock_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity = requireActivity() as MainActivity
        fab_search_stock.setOnClickListener {
            showSearchDialog()
        }
        with(tb_stock_check) {
            title = viewModel.getCompanyName()
            setNavigationOnClickListener {
                view.findNavController().popBackStack()
            }
        }
        with(rv_stock_list) {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = stockAdapter
        }
        viewModel.checkStockVs.observe(viewLifecycleOwner, {
            when (it) {
                is CheckStockViewModel.CheckStockViewState.Error -> {
                    context?.showLongToast(it.message)
                }
                is CheckStockViewModel.CheckStockViewState.Success -> {
                    stockAdapter.addData(it.data)
                }
                is CheckStockViewModel.CheckStockViewState.Loading -> {
                    activity?.showLoading(it.loading)
                }
            }
        })
        showSearchDialog()
    }


    private fun showSearchDialog() {
        context?.let { context ->
            dialog = Dialog(context)
            dialog?.let { dialog ->
                with(dialog) {
                    setContentView(R.layout.scan_stock_search_dialog)
                    window
                        ?.setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    btn_search_item_identifier.setOnClickListener {
                        viewModel.getStockCheck("$SEARCH_ITEM_IDENTIFIERS '${et_query_search.text}')")
                        dismiss()
                    }
                    btn_search_item_no.setOnClickListener {
                        viewModel.getStockCheck("$SEARCH_ITEM_NO '${et_query_search.text}')")
                        dismiss()
                    }
                    show()
                }
            }

        }
    }

}