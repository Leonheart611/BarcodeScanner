package dynamia.com.barcodescanner.ui.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dynamia.com.barcodescanner.R
import dynamia.com.core.base.BaseFragment
import dynamia.com.core.util.Constant.RECEIPT_IMPORT
import dynamia.com.core.util.Constant.RECEIPT_LOCAL
import dynamia.com.core.util.EventObserver
import dynamia.com.core.util.showLongToast
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.home_item_detail.*
import kotlinx.android.synthetic.main.refresh_warning_dialog.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment() {

    private val viewModel: HomeViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (viewModel.checkDBNotNull())
            viewModel.getAllDataFromAPI()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setObservable()
        initView()
        setListener()
    }

    private fun setObservable() {
        viewModel.getAllDaataMessage.observe(viewLifecycleOwner, EventObserver {
            it.let {
                context?.showLongToast(it)
            }
        })
        viewModel.loading.observe(viewLifecycleOwner, EventObserver {
            showLoading(it)
        })
        viewModel.postImportMessage.observe(viewLifecycleOwner, EventObserver {
            context?.showLongToast(it)
        })
        viewModel.postLocalMessage.observe(viewLifecycleOwner, EventObserver {
            context?.showLongToast(it)
        })
        viewModel.postStockCountMessage.observe(viewLifecycleOwner, EventObserver {
            context?.showLongToast(it)
        })
        viewModel.pickingPostMessage.observe(viewLifecycleOwner, EventObserver {
            context?.showLongToast(it)
        })
        viewModel.pickingListRepository.getCountPickingListHeader(viewModel.getEmployeeName())
            .observe(viewLifecycleOwner, Observer {
                tv_picking_list_count.text = it.toString()
            })
        viewModel.receiptImportRepository.getCountReceiptImportHeader(viewModel.getEmployeeName())
            .observe(viewLifecycleOwner, Observer {
                tv_count_receipt_import.text = it.toString()
            })
        viewModel.receiptLocalRepository.getCountReceiptLocalHeader(viewModel.getEmployeeName())
            .observe(viewLifecycleOwner, Observer {
                tv_count_receipt_local.text = it.toString()
            })
    }

    private fun initView() {
        tv_employee_name.text = getString(R.string.employee_title, viewModel.getEmployeeName())
    }

    private fun setListener() {
        cv_log_out.setOnClickListener {
            viewModel.clearAllDB()
            context?.showLongToast("Log Out")
            viewModel.clearSharedpreference()
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
        }
        cv_refresh.setOnClickListener {
            showDialog()
        }
        cv_upload.setOnClickListener {
            viewModel.postPickingData()
            viewModel.postReceiptImportData()
            viewModel.postStockCountData()
            viewModel.postReceiptLocalData()
        }
        cv_picking_list.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_pickingListFragment)
        }
        cv_receipt_import.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToReceiptFragment(
                RECEIPT_IMPORT
            )
            findNavController().navigate(action)
        }
        cv_receipt_local.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToReceiptFragment(
                RECEIPT_LOCAL
            )
            findNavController().navigate(action)
        }
        cv_stock_count.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToStockCountingFragment()
            findNavController().navigate(action)
        }
    }

    private fun showDialog() {
        context?.let { context ->
            val dialog = Dialog(context)
            with(dialog) {
                setContentView(R.layout.refresh_warning_dialog)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                window
                    ?.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                btn_refresh_yes.setOnClickListener {
                    viewModel.getAllDataFromAPI()
                    dismiss()
                }
                btn_refresh_no.setOnClickListener {
                    dismiss()
                }
                show()
            }
        }
    }

}
