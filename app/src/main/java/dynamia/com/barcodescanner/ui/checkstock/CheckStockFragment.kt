package dynamia.com.barcodescanner.ui.checkstock

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels

import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import dynamia.com.barcodescanner.databinding.CheckStockFragmentBinding
import dynamia.com.barcodescanner.databinding.ScanStockSearchDialogBinding
import dynamia.com.barcodescanner.ui.MainActivity
import dynamia.com.core.base.BaseFragmentBinding
import dynamia.com.core.data.entinty.StockCheckDataAssets
import dynamia.com.core.data.entinty.StockOpnameDataAssets
import dynamia.com.core.data.entinty.TransferShipmentHeaderAsset
import dynamia.com.core.util.readJsonAsset
import dynamia.com.core.util.showLongToast

@AndroidEntryPoint
class CheckStockFragment :
    BaseFragmentBinding<CheckStockFragmentBinding>(CheckStockFragmentBinding::inflate) {

    companion object {
        const val SEARCH_ITEM_IDENTIFIERS = "contains(Item_Identifiers,"
        const val SEARCH_ITEM_NO = "contains(Item_No,"
    }

    private var stockAdapter = StockCheckAdapter(mutableListOf())
    private var dialog: Dialog? = null
    private val viewModel: CheckStockViewModel by viewModels()
    private var activity: MainActivity? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity = requireActivity() as MainActivity
        with(viewBinding) {
            fabSearchStock.setOnClickListener {
                showSearchDialog()
            }
            with(tbStockCheck) {
                title = viewModel.getCompanyName()
                setNavigationOnClickListener {
                    view.findNavController().popBackStack()
                }
            }
            with(rvStockList) {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = stockAdapter
            }
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
                    val bind = ScanStockSearchDialogBinding.inflate(layoutInflater)
                    setContentView(bind.root)
                    window
                        ?.setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    with(bind) {
                        btnSearchItemIdentifier.setOnClickListener {
                            viewModel.getStockCheck("$SEARCH_ITEM_IDENTIFIERS '${etQuerySearch.text}')")
                            dismiss()
                        }
                        btnSearchItemNo.setOnClickListener {
                            viewModel.getStockCheck("$SEARCH_ITEM_NO '${etQuerySearch.text}')")
                            dismiss()
                        }
                    }
                    show()
                }
            }
        }
    }

    fun getAssetResult() {
        val data = Gson().fromJson(
            activity?.readJsonAsset("StockCheck.json"),
            StockCheckDataAssets::class.java
        )
        viewModel.getCheckStockFromAsset(data.value.toMutableList())
    }
}