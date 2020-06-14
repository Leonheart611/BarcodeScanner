package dynamia.com.barcodescanner.ui.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.home.adapter.HomeAdapterView
import dynamia.com.core.base.BaseFragment
import dynamia.com.core.data.model.HomeData
import dynamia.com.core.util.Constant.PICKING_LIST
import dynamia.com.core.util.Constant.RECEIPT_IMPORT
import dynamia.com.core.util.Constant.RECEIPT_LOCAL
import dynamia.com.core.util.Constant.STOCK_COUNT
import dynamia.com.core.util.EventObserver
import dynamia.com.core.util.showToast
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.refresh_warning_dialog.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment(), HomeAdapterView.OnHomeClicklistener {

    private val viewModel: HomeViewModel by viewModel()
    private val adapter = HomeAdapterView(mutableListOf(), this)


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
                context?.showToast(it)
                adapter.updateData(getHomeDataList())
            }
        })
        viewModel.loading.observe(viewLifecycleOwner, EventObserver {
            showLoading(it)
        })
        viewModel.postImportMessage.observe(viewLifecycleOwner, EventObserver {
            context?.showToast(it)
        })
        viewModel.postLocalMessage.observe(viewLifecycleOwner, EventObserver {
            context?.showToast(it)
        })
        viewModel.postStockCountMessage.observe(viewLifecycleOwner, EventObserver {
            context?.showToast(it)
        })
        viewModel.pickingPostMessage.observe(viewLifecycleOwner, EventObserver {
            context?.showToast(it)
        })
    }

    private fun initView() {
        tv_employee_name.text = getString(R.string.employee_title, viewModel.getEmployeeName())
        rv_home_list.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rv_home_list.adapter = adapter
        adapter.updateData(getHomeDataList())
    }

    private fun setListener() {
        cv_log_out.setOnClickListener {
            viewModel.clearSharedpreference()
            viewModel.clearAllDB()
            context?.showToast("Log Out")
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
    }

    private fun getHomeDataList(): MutableList<HomeData> {
        val homeDatas: MutableList<HomeData> = mutableListOf()
        homeDatas.add(
            HomeData(
                countData = viewModel.pickingListRepository.getCountPickingListHeader(),
                title = PICKING_LIST
            )
        )
        homeDatas.add(
            HomeData(
                countData = viewModel.receiptImportRepository.getCountReceiptImportHeader(),
                title = RECEIPT_IMPORT
            )
        )
        homeDatas.add(
            HomeData(
                countData = viewModel.receiptLocalRepository.getCountReceiptLocalHeader(),
                title = RECEIPT_LOCAL
            )
        )
        homeDatas.add(
            HomeData(null, STOCK_COUNT)
        )
        return homeDatas
    }

    override fun onHomeClicklistener(value: String) {
        when (value) {
            PICKING_LIST -> {
                findNavController().navigate(R.id.action_homeFragment_to_pickingListFragment)
            }
            RECEIPT_LOCAL -> {
                val action = HomeFragmentDirections.actionHomeFragmentToReceiptFragment(
                    RECEIPT_LOCAL
                )
                findNavController().navigate(action)
            }
            RECEIPT_IMPORT -> {
                val action = HomeFragmentDirections.actionHomeFragmentToReceiptFragment(
                    RECEIPT_IMPORT
                )
                findNavController().navigate(action)
            }
            STOCK_COUNT -> {
                val action = HomeFragmentDirections.actionHomeFragmentToStockCountingFragment()
                findNavController().navigate(action)
            }
            else -> {
                context?.showToast("Under Maintenance please contact the Developer")
            }
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
