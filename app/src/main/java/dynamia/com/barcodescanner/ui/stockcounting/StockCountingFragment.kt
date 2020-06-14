package dynamia.com.barcodescanner.ui.stockcounting

import android.app.Instrumentation
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.stockcounting.adapter.StockCountAdapter
import dynamia.com.core.base.BaseFragment
import dynamia.com.core.data.model.StockCount
import dynamia.com.core.util.EventObserver
import dynamia.com.core.util.getCurrentDate
import dynamia.com.core.util.getCurrentTime
import dynamia.com.core.util.showToast
import kotlinx.android.synthetic.main.stock_counting_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class StockCountingFragment : BaseFragment() {

    private val viewModel: StockCountingViewModel by viewModel()

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
    }

    private fun setupView() {
        viewModel.stockCountRepository.getAllStockCount()
            .observe(viewLifecycleOwner, Observer { data ->
                with(rv_stock_count) {
                    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                    adapter = StockCountAdapter(data)
                }
            })
        viewModel.postStockCountMessage.observe(viewLifecycleOwner, EventObserver {
            context?.showToast(it)
        })
        viewModel.loading.observe(viewLifecycleOwner, EventObserver {
            showLoading(it)
        })
        tv_employee_name.text = viewModel.getEmployeeName()
    }

    private fun setupListeneer() {
        cv_count_post.setOnClickListener {
            viewModel.postStockCountData()
        }
        et_count_part_no.doAfterTextChanged {
            tryInsertData()

        }
        et_count_item_no.doAfterTextChanged {
            tryInsertData()
        }
        et_count_serial_no.doAfterTextChanged {
            tryInsertData()
        }
        cv_back.setOnClickListener {
            view?.findNavController()?.popBackStack()
        }
    }

    private fun tryInsertData() {
        if (checkMandatory()) {
            viewModel.stockCountRepository.insertStockCount(
                StockCount(
                    Part_No = et_count_part_no.text.toString(),
                    Serial_No = et_count_serial_no.text.toString(),
                    Item_No = et_count_item_no.text.toString(),
                    time = context?.getCurrentTime() ?: "",
                    date = "${context?.getCurrentDate()}T${context?.getCurrentTime()}",
                    Employee_COde = viewModel.getEmployeeName() ?: ""
                )
            )
            clearInputData()
            et_count_part_no.isFocusable = true
        }else{
            nextTextView()
        }
    }

    private fun checkMandatory(): Boolean {
        return et_count_part_no.text.toString().isNotEmpty() && et_count_serial_no.text.toString()
            .isNotEmpty() && et_count_item_no.text.toString().isNotEmpty()
    }

    private fun clearInputData(){
        et_count_serial_no.text?.clear()
        et_count_item_no.text?.clear()
        et_count_part_no.text?.clear()
    }

    fun nextTextView() {
        Thread(Runnable {
            try {
                val inst = Instrumentation()
                inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER)
            } catch (e: InterruptedException) {
                Log.e("Error Thread KeyCode",e.localizedMessage)
            }
        }).start()
    }
}
