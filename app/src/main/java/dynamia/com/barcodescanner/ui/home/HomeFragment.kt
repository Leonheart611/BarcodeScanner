package dynamia.com.barcodescanner.ui.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.home.adapter.HomeAdapterView
import dynamia.com.core.data.model.*
import dynamia.com.core.util.Constant.PICKING_LIST
import dynamia.com.core.util.Constant.RECEIPT_IMPORT
import dynamia.com.core.util.Constant.RECEIPT_LOCAL
import dynamia.com.core.util.readJsonAsset
import dynamia.com.core.util.showToast
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.refresh_warning_dialog.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(), HomeAdapterView.OnHomeClicklistener {

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
        viewModel.message.observe(viewLifecycleOwner, Observer {
            it.let {
                context?.showToast(it)
            }
        })
    }

    private fun initView() {
        tv_employee_name.text = getString(R.string.employee_title, viewModel.getEmployeeName())
        val adapter = HomeAdapterView(
            getHomeDataList(),
            this
        )
        rv_home_list.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rv_home_list.adapter = adapter
    }

    private fun setListener() {
        cv_log_out.setOnClickListener {
            viewModel.clearAllDB()
            context?.showToast("Log Out")
            view?.findNavController()?.popBackStack()
        }
        toolbar_home.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.home_logout -> {
                    view?.findNavController()?.popBackStack()
                    true
                }
                else -> false
            }
        }
        cv_refresh.setOnClickListener {
            showDialog()
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
        return homeDatas
    }

    override fun onHomeClicklistener(value: String) {
        when (value) {
            PICKING_LIST -> {
                findNavController().navigate(R.id.action_homeFragment_to_pickingListFragment)
            }
            RECEIPT_LOCAL->{
                val action = HomeFragmentDirections.actionHomeFragmentToReceiptFragment(
                    RECEIPT_LOCAL)
                findNavController().navigate(action)
            }
            RECEIPT_IMPORT->{
                val action = HomeFragmentDirections.actionHomeFragmentToReceiptFragment(
                    RECEIPT_IMPORT)
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
                    initView()
                    dismiss()
                }
                btn_refresh_no.setOnClickListener {
                    dismiss()
                }
                show()
            }
        }
    }

    private fun resetDataFromJsonLocal() {
        viewModel.clearAllDB()
        val pickingListHeader = context?.readJsonAsset("PickingListHeader.json")
        val pickingListLine = context?.readJsonAsset("PickingListLine.json")
        val pickingListScanEntries = context?.readJsonAsset("PickingListScanEntries.json")

        val pickingListHeaders = Gson().fromJson(pickingListHeader, PickingListHeader::class.java)

        pickingListHeaders.value?.forEach { pickingListValue ->
            pickingListValue?.let {
                viewModel.pickingListRepository.insertPickingListHeader(it)
            }
        }
        val pickingListLines = Gson().fromJson(pickingListLine, PickingListLine::class.java)
        pickingListLines.value.forEach {
            viewModel.pickingListRepository.insertPickingListLine(it)
        }
        val pickingListScanEntriesList = Gson().fromJson(
            pickingListScanEntries,
            PickingListScanEntries::class.java
        )
        pickingListScanEntriesList.value.forEach {
            viewModel.pickingListRepository.insertPickingListScanEntries(it)
        }

        val receiptImportHeader = context?.readJsonAsset("ReceiptImportHeader.json")
        val receiptImportLine = context?.readJsonAsset("ReceiptImportLine.json")
        val receiptImportScanEntries = context?.readJsonAsset("ReceiptImportScanEntries.json")

        Gson().fromJson(receiptImportHeader, ReceiptImportHeader::class.java).value.forEach {
            viewModel.receiptImportRepository.insertReceiptImportHeader(it)
        }
        Gson().fromJson(receiptImportLine, ReceiptImportLine::class.java).value.forEach {
            viewModel.receiptImportRepository.insertReceiptImportLine(it)
        }
        Gson().fromJson(
            receiptImportScanEntries,
            ReceiptImportScanEntries::class.java
        ).value.forEach {
            viewModel.receiptImportRepository.insertReceiptImportScanEntries(it)
        }

        val receiptLocalHeader = context?.readJsonAsset("ReceiptLocalHeader.json")
        val receiptLocalLine = context?.readJsonAsset("ReceiptLocalLine.json")
        val receiptLocalScanEntries = context?.readJsonAsset("ReceiptLocalScanEntries.json")

        Gson().fromJson(receiptLocalHeader, ReceiptLocalHeader::class.java).value.forEach {
            viewModel.receiptLocalRepository.insertReceiptLocalHeader(it)
        }
        Gson().fromJson(receiptLocalLine, ReceiptLocalLine::class.java).value.forEach {
            viewModel.receiptLocalRepository.insertReceiptLocalLine(it)
        }
        Gson().fromJson(
            receiptLocalScanEntries,
            ReceiptLocalScanEntries::class.java
        ).value.forEach {
            viewModel.receiptLocalRepository.insertReceiptLocalScanEntries(it)
        }

    }


}
