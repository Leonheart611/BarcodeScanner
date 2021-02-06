package dynamia.com.barcodescanner.ui.stockcounting

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.stockcounting.adapter.StockCountAdapter
import dynamia.com.core.data.model.StockCount
import dynamia.com.core.util.getCurrentDate
import dynamia.com.core.util.getCurrentTime
import dynamia.com.core.util.showLongToast
import kotlinx.android.synthetic.main.delete_confirmation_dialog.*
import kotlinx.android.synthetic.main.dialog_part_no_not_found.*
import kotlinx.android.synthetic.main.dialog_part_no_not_found.tv_error_message
import kotlinx.android.synthetic.main.stock_counting_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class StockCountingFragment : Fragment(), StockCountAdapter.OnStockClicklistener {

    private val viewModel: StockCountingViewModel by viewModel()
    private var snNoDialog: Dialog? = null
    private val adapterStockCount = StockCountAdapter(mutableListOf(), this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.stock_counting_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupView()
        setupListeneer()
        setObseverable()
    }

    private fun setupView() {
        with(rv_stock_count) {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = adapterStockCount
        }
        viewModel.stockCountRepository.getAllStockCount()
            .observe(viewLifecycleOwner, { data ->
                adapterStockCount.updateData(data)
            })
        tv_employee_name.text = viewModel.getEmployeeName()
        switch_sn_pn_stock.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                switch_sn_pn_stock.text = getString(R.string.sn_pn_scan)
            else
                switch_sn_pn_stock.text = getString(R.string.sn_normal_scan)
        }
        switch_k_validation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switch_k_validation.text = getString(R.string.validation_k_sn)
            } else {
                switch_k_validation.text = getString(R.string.validation_k_sn_no)
            }
        }
    }

    private fun setupListeneer() {
        cv_count_post.setOnClickListener {
            showPostDialog()
        }
        tb_stock_count.setNavigationOnClickListener { view?.findNavController()?.popBackStack() }
        et_count_part_no.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            private var timer = Timer()
            private val DELAY: Long = 1000
            override fun afterTextChanged(p0: Editable?) {
                if (switch_stock_count.isChecked) {// Auto Enter Check
                    timer.cancel()
                    timer = Timer()
                    timer.schedule(
                        object : TimerTask() {
                            override fun run() {
                                activity?.runOnUiThread {
                                    if (switch_sn_pn_stock.isChecked && et_po_stock_count.text.toString()
                                            .isNotEmpty()
                                    ) {
                                        et_count_serial_no.requestFocus()
                                    } else {
                                        et_po_stock_count.requestFocus()
                                    }
                                }
                            }
                        }, DELAY
                    )
                }
            }
        })
        et_po_stock_count.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            private var timer = Timer()
            private val DELAY: Long = 1000
            override fun afterTextChanged(p0: Editable?) {
                if (switch_stock_count.isChecked) {
                    timer.cancel()
                    timer = Timer()
                    timer.schedule(
                        object : TimerTask() {
                            override fun run() {
                                activity?.runOnUiThread { et_count_serial_no.requestFocus() }
                            }
                        }, DELAY
                    )
                }
            }
        })
        et_count_serial_no.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }


            private var timer = Timer()
            private val DELAY: Long = 1000
            override fun afterTextChanged(p0: Editable?) {
                if (switch_stock_count.isChecked) {
                    if (et_count_serial_no.text?.isNotEmpty() != false)
                        tryInsertData()
                }
            }
        })

        cv_back.setOnClickListener {
            view?.findNavController()?.popBackStack()
        }

        et_count_serial_no.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                tryInsertData()
            }
            false
        }
    }

    private fun showPostDialog() {
        val postStockCountDialogFragment = PostStockCountDialogFragment()
        postStockCountDialogFragment.show(
            requireActivity().supportFragmentManager,
            postStockCountDialogFragment.tag
        )
    }

    private fun tryInsertData() {
        if (checkMandatory()) {
            viewModel.checkSnNo(et_count_serial_no.text.toString())
        } else {
            if (switch_k_validation.isChecked) {
                context?.showLongToast(getString(R.string.validation_stock_count_k_message))
            } else {
                context?.showLongToast(getString(R.string.validation_stock_count_message))
            }
        }
    }

    private fun setObseverable() {
        viewModel.stockCountViewState.observe(viewLifecycleOwner, {
            when (it) {
                is StockCountingViewModel.StockCountingViewState.CheckedSnNo -> {
                    if (it.isEmpty) {
                        viewModel.stockCountRepository.insertStockCount(
                            StockCount(
                                Part_No = et_count_part_no.text.toString(),
                                Serial_No = et_count_serial_no.text.toString(),
                                Item_No = et_po_stock_count.text.toString(),
                                time = context?.getCurrentTime() ?: "",
                                date = "${context?.getCurrentDate()}T${context?.getCurrentTime()}",
                                Employee_COde = viewModel.getEmployeeName()
                            )
                        )
                        if (switch_sn_pn_stock.isChecked) {
                            et_count_part_no.requestFocus()
                            et_count_part_no.text?.clear()
                            et_count_serial_no.text?.clear()
                        } else {
                            clearSnInput()
                        }

                    } else {
                        showErroSnDialog(getString(R.string.sn_no_already_inputed))
                    }
                }
                is StockCountingViewModel.StockCountingViewState.Error -> {
                    context?.showLongToast(it.message)
                }
            }
        })
    }


    private fun checkMandatory(): Boolean {
        return when (switch_k_validation.isChecked) {
            true -> {
                et_count_part_no.text.toString().isNotEmpty() && et_count_serial_no.text.toString()
                    .isNotEmpty() && et_po_stock_count.text.toString()
                    .isNotEmpty() && et_count_serial_no.text.toString().startsWith("S", true)
                        && et_po_stock_count.text.toString().startsWith("K", true)
                        && et_count_part_no.text.toString().startsWith("1P", true)
            }
            false -> {
                et_count_part_no.text.toString().isNotEmpty() && et_count_serial_no.text.toString()
                    .isNotEmpty() && et_po_stock_count.text.toString().isNotEmpty()
            }
        }
    }

    private fun clearInputData() {
        et_count_serial_no.text?.clear()
        et_po_stock_count.text?.clear()
        et_count_part_no.text?.clear()
        et_count_part_no.requestFocus()
    }

    private fun clearSnInput() {
        et_count_serial_no.text?.clear()
        et_count_serial_no.requestFocus()
    }

    private fun showErroSnDialog(message: String) {
        context?.let {
            snNoDialog = Dialog(it)
            snNoDialog?.let { dialog ->
                with(dialog) {
                    setContentView(R.layout.dialog_part_no_not_found)
                    window
                        ?.setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    tv_error_message.text = message
                    btn_ok.setOnClickListener {
                        dismiss()
                        clearSn()
                    }
                    show()
                }
            }
        }
    }

    private fun clearSn() {
        et_count_serial_no.text?.clear()
        et_count_serial_no.requestFocus()
    }

    override fun onStockClicklistener(data: StockCount) {
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
                    viewModel.deleteSnNo(data)
                    dismiss()
                }
                btn_cancel.setOnClickListener {
                    dismiss()
                }
                show()
            }
        }

    }
}
