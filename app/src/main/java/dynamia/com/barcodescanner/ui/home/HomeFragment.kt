package dynamia.com.barcodescanner.ui.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.MainActivity
import dynamia.com.barcodescanner.ui.home.HomeViewModel.HomeViewState.DBhasEmpty
import dynamia.com.core.util.Constant.RECEIPT_IMPORT
import dynamia.com.core.util.Constant.RECEIPT_LOCAL
import dynamia.com.core.util.showLongToast
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.home_item_detail.*
import kotlinx.android.synthetic.main.refresh_warning_dialog.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModel()
    private var activity: MainActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.checkDBNotNull()
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity = requireActivity() as MainActivity
        setObservable()
        initView()
        setListener()
    }

    private fun setObservable() {
        viewModel.pickingListRepository.getCountPickingListHeader(viewModel.getEmployeeName())
            .observe(viewLifecycleOwner, {
                tv_picking_list_count.text = it.toString()
            })
        viewModel.receiptImportRepository.getCountReceiptImportHeader(viewModel.getEmployeeName())
            .observe(viewLifecycleOwner, {
                tv_count_receipt_import.text = it.toString()
            })
        viewModel.receiptLocalRepository.getCountReceiptLocalHeader(viewModel.getEmployeeName())
            .observe(viewLifecycleOwner, {
                tv_count_receipt_local.text = it.toString()
            })

        viewModel.homeViewState.observe(viewLifecycleOwner, {
            when (it) {
                is DBhasEmpty -> {
                    if (it.boolean) {
                        openStatusApi()
                    }
                }
                is HomeViewModel.HomeViewState.Error -> {
                    context?.showLongToast(it.message)
                }
                is HomeViewModel.HomeViewState.ShowLoading -> {

                }
                is HomeViewModel.HomeViewState.HasSuccessLogout -> {
                    activity?.showLoading(false)
                    context?.showLongToast("Log Out")
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
                }
            }
        })
    }

    private fun openStatusApi() {
        val dialog = HomeGetDataDialog()
        dialog.show(requireActivity().supportFragmentManager, HomeGetDataDialog.TAG)
    }

    private fun openPostStatusApi() {
        val dialog = HomePostAllDialog()
        dialog.show(requireActivity().supportFragmentManager, dialog.tag)
    }

    private fun initView() {
        tv_employee_name.text = getString(R.string.employee_title, viewModel.getEmployeeName())
    }

    private fun setListener() {
        cv_log_out.setOnClickListener {
            viewModel.logOutSharedPreferences()
        }
        cv_refresh.setOnClickListener {
            showDialog()
        }
        cv_upload.setOnClickListener {
            openPostStatusApi()
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
                    viewModel.clearAllDB()
                    openStatusApi()
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
