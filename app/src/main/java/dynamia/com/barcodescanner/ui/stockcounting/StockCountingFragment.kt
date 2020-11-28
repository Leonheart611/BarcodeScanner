package dynamia.com.barcodescanner.ui.stockcounting

import android.app.Dialog
import android.app.Instrumentation
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.stockcounting.adapter.StockCountAdapter
import dynamia.com.core.data.model.StockCount
import dynamia.com.core.util.getCurrentDate
import dynamia.com.core.util.getCurrentTime
import dynamia.com.core.util.showLongToast
import kotlinx.android.synthetic.main.dialog_part_no_not_found.*
import kotlinx.android.synthetic.main.stock_counting_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class StockCountingFragment : Fragment() {

    private val viewModel: StockCountingViewModel by viewModel()
    private var snNoDialog: Dialog? = null

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
        viewModel.stockCountRepository.getAllStockCount()
            .observe(viewLifecycleOwner, Observer { data ->
                with(rv_stock_count) {
                    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                    adapter = StockCountAdapter(data)
                }
            })
        tv_employee_name.text = viewModel.getEmployeeName()
    }

    private fun setupListeneer() {
        cv_count_post.setOnClickListener {
            showPostDialog()
        }
        et_count_part_no.doAfterTextChanged {
            if (et_count_part_no.text?.isNotEmpty() != false)
                tryInsertData()
        }
        et_count_item_no.doAfterTextChanged {
            if (et_count_item_no.text?.isNotEmpty() != false)
                tryInsertData()
        }
        et_count_serial_no.doAfterTextChanged {
            if (et_count_serial_no.text?.isNotEmpty() != false)
                tryInsertData()
        }
        cv_back.setOnClickListener {
            view?.findNavController()?.popBackStack()
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
            nextTextView()
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
                                Item_No = et_count_item_no.text.toString(),
                                time = context?.getCurrentTime() ?: "",
                                date = "${context?.getCurrentDate()}T${context?.getCurrentTime()}",
                                Employee_COde = viewModel.getEmployeeName()
                            )
                        )
                        clearInputData()
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
        return et_count_part_no.text.toString().isNotEmpty() && et_count_serial_no.text.toString()
            .isNotEmpty() && et_count_item_no.text.toString().isNotEmpty()
    }

    private fun clearInputData() {
        et_count_serial_no.text?.clear()
        et_count_item_no.text?.clear()
        et_count_part_no.text?.clear()
        et_count_part_no.requestFocus()
    }

    private fun nextTextView() {
        Thread(Runnable {
            try {
                val inst = Instrumentation()
                inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER)
            } catch (e: InterruptedException) {
                Log.e("Error Thread KeyCode", e.localizedMessage)
            }
        }).start()
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
}
