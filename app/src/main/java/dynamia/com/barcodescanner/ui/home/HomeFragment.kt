package dynamia.com.barcodescanner.ui.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dynamia.com.barcodescanner.BuildConfig
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.databinding.HomeFragmentBinding
import dynamia.com.barcodescanner.databinding.RefreshWarningDialogBinding
import dynamia.com.barcodescanner.ui.MainActivity
import dynamia.com.barcodescanner.ui.home.HomeViewModel.HomeViewState.DBhasEmpty
import dynamia.com.barcodescanner.ui.transferstore.TransferType
import dynamia.com.core.base.BaseFragmentBinding
import dynamia.com.core.util.Constant
import dynamia.com.core.util.EventObserver
import dynamia.com.core.util.showLongToast


@AndroidEntryPoint
class HomeFragment : BaseFragmentBinding<HomeFragmentBinding>(HomeFragmentBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()
    private var activity: MainActivity? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity = requireActivity() as MainActivity
        setObservable()
        initView()
        setListener()
    }

    private fun setObservable() {
        with(viewModel) {
            homeViewState.observe(viewLifecycleOwner, EventObserver {
                when (it) {
                    is DBhasEmpty -> {
                        if (it.value == 0) openStatusApi()
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
                        showDialog(
                            if (it.unpostedCount.isEmpty()) null else it.unpostedCount,
                            getString(R.string.logout_warning)
                        ) {
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

            transferReceiptRepository.getCountTransferReceiptHeader()
                .observe(viewLifecycleOwner, {
                    viewBinding.homeInclude.tvCountReceipt.text = it.toString()
                })
            transferShipmentRepository.getTransferHeaderCount().observe(viewLifecycleOwner, {
                viewBinding.homeInclude.tvTransferCount.text = it.toString()
            })
            purchaseOrderRepository.getCountPurchaseOrderHeader().observe(viewLifecycleOwner, {
                viewBinding.homeInclude.tvCountPurchaseOrder.text = it.toString()
            })
            stockOpnameDataRepository.getCountStockOpname().observe(viewLifecycleOwner, {
                viewBinding.homeInclude.tvCountStockOpname.text = it.toString()
            })
            inventoryRepository.getCountInventoryHeader().observe(viewLifecycleOwner, {
                viewBinding.homeInclude.tvInventoryCount.text = it.toString()
            })
        }
    }

    private fun openStatusApi() {
        val dialog = HomeGetDataDialog()
        dialog.isCancelable = false
        dialog.show(requireActivity().supportFragmentManager, HomeGetDataDialog.TAG)
    }

    private fun openPostStatusApi() {
        val dialog = HomePostAllDialog()
        dialog.show(requireActivity().supportFragmentManager, dialog.tag)
    }

    private fun initView() {
        viewBinding.toolbarHome.title =
            getString(R.string.employee_title, viewModel.getCompanyName())
        when (BuildConfig.FLAVOR) {
            Constant.APP_STORE -> {
                with(viewBinding.homeInclude) {
                    cvInventory.isVisible = false
                    cvPurchaseOrder.isVisible = false
                    cvBinReclass.isVisible = false
                }
            }
        }
    }

    private fun setListener() {
        with(viewBinding) {
            cvLogOut.setOnClickListener {
                viewModel.checkUnpostedData(HomeViewModel.FunctionDialog.LOGOUT)
            }
            cvRefresh.setOnClickListener {
                viewModel.checkUnpostedData(HomeViewModel.FunctionDialog.REFRESH)
            }
            cvUpload.setOnClickListener {
                openPostStatusApi()
            }
            with(homeInclude) {
                cvTransferStore.setOnClickListener {
                    val action = HomeFragmentDirections.actionHomeFragmentToPickingListFragment(
                        TransferType.SHIPMENT
                    )
                    findNavController().navigate(action)
                }
                cvTransferReceipt.setOnClickListener {
                    val action = HomeFragmentDirections.actionHomeFragmentToPickingListFragment(
                        TransferType.RECEIPT
                    )
                    findNavController().navigate(action)
                }
                cvPurchaseOrder.setOnClickListener {
                    val action = HomeFragmentDirections.actionHomeFragmentToPickingListFragment(
                        TransferType.PURCHASE
                    )
                    findNavController().navigate(action)
                }
                cvStockOpname.setOnClickListener {
                    val action = HomeFragmentDirections.actionHomeFragmentToStockOpnameFragment()
                    findNavController().navigate(action)
                }
                cvCheckStock.setOnClickListener {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCheckStockFragment())
                }
                cvBinReclass.setOnClickListener {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToBinReclassFragment())
                }
                cvInventory.setOnClickListener {
                    findNavController().navigate(
                        HomeFragmentDirections.actionHomeFragmentToPickingListFragment(
                            TransferType.INVENTORY
                        )
                    )
                }
            }
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
                    unpostedCount?.let {
                        tvUnpostData.text =
                            getString(R.string.refresh_and_logout_warning_unposted_data, it)
                        tvUnpostData.isVisible = true
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
