package dynamia.com.barcodescanner.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.data.model.HomeData
import dynamia.com.barcodescanner.ui.home.adapter.HomeAdapterView
import dynamia.com.barcodescanner.util.Constant.PICKING_LIST
import dynamia.com.barcodescanner.util.Constant.RECEIPT_IMPORT
import dynamia.com.barcodescanner.util.Constant.RECEIPT_LOCAL
import dynamia.com.barcodescanner.util.showToast
import kotlinx.android.synthetic.main.home_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(), HomeAdapterView.OnHomeClicklistener {

    private val viewModel: HomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    private fun initView() {
        val adapter = HomeAdapterView(
            getHomeDataList(),
            this
        )
        rv_home_list.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rv_home_list.adapter = adapter
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
            else->{
                context?.showToast("Under Maintenance please contact the Developer")
            }
        }
    }


}
