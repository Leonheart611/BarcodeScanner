package dynamia.com.barcodescanner.ui.pickinglist.pickingdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.pickinglist.adapter.PickingDetailAdapter
import dynamia.com.core.base.BaseFragment
import dynamia.com.core.util.Constant
import dynamia.com.core.util.EventObserver
import dynamia.com.core.util.toNormalDate
import kotlinx.android.synthetic.main.picking_detail_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PickingDetailFragment : BaseFragment() {
    private val viewModel: PickingDetailViewModel by viewModel()
    private val args: PickingDetailFragmentArgs by navArgs()
    private val pickingListHeaderValue by lazy {
        viewModel.pickingListRepository.getPickingListHeader(
            args.pickingListNo
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.picking_detail_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupView()
        setupListener()
    }

    override fun onResume() {
        super.onResume()
        setupView()
    }

    private fun setupView() {
        tv_picking_detail_so.text = getString(R.string.picklistno_title, args.pickingListNo)
        with(pickingListHeaderValue) {
            et_customer_name.setText(sellToCustomerName)
            et_customer_po_no.setText(sellToCustomerNo)
            et_order_date.setText(requestedDeliveryDate.toNormalDate())
            et_so_no.setText(sONo)
            et_project_code.setText(projectCode)
        }
        viewModel.pickingListRepository.getAllPickingListLine(args.pickingListNo)
            .observe(viewLifecycleOwner,
                Observer {
                    val adapter = PickingDetailAdapter(
                        it.toMutableList()
                    )
                    rv_picking_detail.layoutManager =
                        LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                    rv_picking_detail.adapter = adapter
                })
        viewModel.loading.observe(viewLifecycleOwner, EventObserver {
            showLoading(it)
        })
    }

    private fun setupListener() {
        cv_pick.setOnClickListener {
            val action =
                PickingDetailFragmentDirections.actionPickingDetailFragmentToReceivingFragment(args.pickingListNo)
            view?.findNavController()?.navigate(action)
        }
        cv_back.setOnClickListener {
            view?.findNavController()?.popBackStack()
        }
        cv_post.setOnClickListener {
            viewModel.postPickingData()
        }
        toolbar_picking_detail.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_search -> {
                    val action =
                        PickingDetailFragmentDirections.actionPickingDetailFragmentToReceiptSearchFragment(
                            PoNo = args.pickingListNo,
                            source = Constant.PICKING_LIST
                        )
                    view?.findNavController()?.navigate(action)
                    true
                }
                R.id.menu_history -> {
                    val action =
                        PickingDetailFragmentDirections.actionPickingDetailFragmentToHistoryInputFragment(
                            args.pickingListNo, Constant.PICKING_LIST
                        )
                    view?.findNavController()?.navigate(action)
                    true
                }
                else -> false
            }
        }
    }

}
