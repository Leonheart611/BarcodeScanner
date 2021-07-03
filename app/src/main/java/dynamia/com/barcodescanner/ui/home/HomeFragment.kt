package dynamia.com.barcodescanner.ui.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.MainActivity
import dynamia.com.barcodescanner.ui.home.HomeViewModel.HomeViewState.DBhasEmpty
import dynamia.com.barcodescanner.ui.transferstore.TransferType
import dynamia.com.core.util.Constant.RECEIPT_LOCAL
import dynamia.com.core.util.EventObserver
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
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity = requireActivity() as MainActivity
        setObservable()
        initView()
        setListener()
    }

    private fun setObservable() {
        viewModel.homeViewState.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                is DBhasEmpty -> {

                }
                is HomeViewModel.HomeViewState.Error -> {
                    context?.showLongToast(it.message)
                }
                is HomeViewModel.HomeViewState.ShowLoading -> {
                    activity?.showLoading(it.boolean)
                }
                is HomeViewModel.HomeViewState.HasSuccessLogout -> {
                    activity?.showLoading(false)
                    context?.showLongToast("Log Out")
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
                }
                is HomeViewModel.HomeViewState.GetUnpostedDataLogout -> {
                    showDialog(if (it.unpostedCount.isEmpty()) null else it.unpostedCount,
                        getString(R.string.logout_warning)) {
                        viewModel.logOutSharedPreferences()
                    }
                }
                is HomeViewModel.HomeViewState.GetUnpostedDataRefresh -> {
                    showDialog(if (it.unpostedCount.isEmpty()) null else it.unpostedCount) {
                        openStatusApi()
                    }
                }
            }
        })
        viewModel.transferShipmentRepository.getCheckEmptyOrNot().observe(viewLifecycleOwner, {
            if (it == 0) {
                openStatusApi()
            } else {
                tv_transfer_count.text = it.toString()
            }
        })
        viewModel.transferReceiptRepository.getAllTransferReceiptHeader()
            .observe(viewLifecycleOwner, {
                tv_count_receipt.text = it.size.toString()
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
        toolbar_home.title = getString(R.string.employee_title, viewModel.getCompanyName())
    }

    private fun setListener() {
        cv_log_out.setOnClickListener {
            viewModel.checkUnpostedData(HomeViewModel.FunctionDialog.LOGOUT)
        }
        cv_refresh.setOnClickListener {
            viewModel.checkUnpostedData(HomeViewModel.FunctionDialog.REFRESH)
        }
        cv_upload.setOnClickListener {
            openPostStatusApi()
        }
        cv_transfer_store.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToPickingListFragment(
                TransferType.SHIPMENT
            )
            findNavController().navigate(action)
        }
        cv_transfer_receipt.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToPickingListFragment(
                TransferType.RECEIPT
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

    private fun showDialog(
        unpostedCount: String?,
        warningMessage: String? = null,
        call: () -> Unit,
    ) {
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
                warningMessage?.let { message ->
                    tv_warning_logout_refresh.text = message
                }
                unpostedCount?.let {
                    tv_unpost_data.text =
                        getString(R.string.refresh_and_logout_warning_unposted_data, it)
                    tv_unpost_data.isVisible = true
                }
                btn_refresh_yes.setOnClickListener {
                    call()
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
