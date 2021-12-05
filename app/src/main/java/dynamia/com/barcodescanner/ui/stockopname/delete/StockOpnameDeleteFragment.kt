package dynamia.com.barcodescanner.ui.stockopname.delete

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.databinding.RefreshWarningDialogBinding
import dynamia.com.barcodescanner.databinding.StockOpnameDeleteFragmentBinding
import dynamia.com.barcodescanner.ui.stockopname.adapter.StockOpnameHistoryDelete
import dynamia.com.core.base.BaseFragmentBinding
import dynamia.com.core.util.showLongToast

@AndroidEntryPoint
class StockOpnameDeleteFragment :
    BaseFragmentBinding<StockOpnameDeleteFragmentBinding>(StockOpnameDeleteFragmentBinding::inflate) {
    private val viewModel: StockOpnameDeleteViewModel by viewModels()
    private val stockOpnameAdapter = StockOpnameHistoryDelete()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    fun setupView() {
        with(viewBinding) {
            fabDeleteAllBox.hide()
            etBoxSearch.doOnTextChanged { text, _, _, count ->
                if (count != 0) {
                    text?.let { viewModel.updateQuery(it.toString()) }
                }
            }
            rvStockopnameInput.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            rvStockopnameInput.adapter = stockOpnameAdapter

            viewModel.stockOpnameInputValue.observe(viewLifecycleOwner, { data ->
                stockOpnameAdapter.submitList(data)
                if (data.isNotEmpty()) {
                    tvBoxCount.text = getString(
                        R.string.stock_opname_qty_box_delete,
                        data.sumOf { it.quantity }.toString()
                    )
                    fabDeleteAllBox.show()
                } else {
                    tvBoxCount.text = getString(
                        R.string.stock_opname_qty_box_delete,
                        data.sumOf { it.quantity }.toString()
                    )
                    fabDeleteAllBox.hide()
                }

            })
            viewModel.deleteViewState.observe(viewLifecycleOwner, {
                when (it) {
                    is StockOpnameDeleteViewModel.DeleteViewState.Error -> {
                        context?.showLongToast(it.message)
                    }
                    StockOpnameDeleteViewModel.DeleteViewState.Success -> {
                        context?.showLongToast("Success Delete All Data")
                    }
                }
            })

            fabDeleteAllBox.setOnClickListener {
                showDialog(warningMessage = getString(R.string.stock_opname_box_delete_warning)) {
                    viewModel.deleteAllBox(etBoxSearch.text.toString())
                }
            }
        }
    }

    private fun showDialog(
        warningMessage: String? = null,
        call: () -> Unit,
    ) {
        context?.let { context ->
            val dialog = Dialog(context)
            with(dialog) {
                val bind = RefreshWarningDialogBinding.inflate(layoutInflater)
                setContentView(bind.root)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                window
                    ?.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                with(bind) {
                    warningMessage?.let { message ->
                        tvWarningLogoutRefresh.text = message
                    }
                    btnRefreshYes.setOnClickListener {
                        call()
                        dismiss()
                    }
                    btnRefreshNo.setOnClickListener {
                        dismiss()
                    }
                }
                show()
            }
        }
    }


}