package dynamia.com.barcodescanner.ui.pickinglist.pickingdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.pickinglist.adapter.PickingDetailAdapter
import kotlinx.android.synthetic.main.picking_detail_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PickingDetailFragment : Fragment() {
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

    private fun setupView() {
        toolbar_picking_detail.title = args.pickingListNo
        with(pickingListHeaderValue) {
            et_customer_name.setText(sellToCustomerName)
            et_customer_po_no.setText(customerPurchaseOrderNo)
            et_order_date.setText(orderDate)
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
    }

    private fun setupListener() {
        fab_input_picking.setOnClickListener {
            val action =
                PickingDetailFragmentDirections.actionPickingDetailFragmentToReceivingFragment(args.pickingListNo)
            view?.findNavController()?.navigate(action)
        }
    }

}
