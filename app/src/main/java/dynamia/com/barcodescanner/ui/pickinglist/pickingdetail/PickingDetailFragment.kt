package dynamia.com.barcodescanner.ui.pickinglist.pickingdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.pickinglist.adapter.PickingDetailAdapter
import dynamia.com.core.data.model.PickingListHeaderValue
import dynamia.com.core.data.model.PickingListLineValue
import dynamia.com.core.util.Constant
import dynamia.com.core.util.showLongToast
import dynamia.com.core.util.toNormalDate
import kotlinx.android.synthetic.main.picking_detail_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PickingDetailFragment : Fragment() {
    private val viewModel: PickingDetailViewModel by viewModel()
    private val args: PickingDetailFragmentArgs by navArgs()
    val adapter = PickingDetailAdapter(mutableListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.picking_detail_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getPickingDetail(args.pickingListNo)
        setupView()
        setupListener()
        setObseverable()
    }

    private fun setupView() {
        tv_picking_detail_so.text = getString(R.string.picklistno_title, args.pickingListNo)
        et_customer_po_no.setText(viewModel.getEmployeeName())
        rv_picking_detail.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rv_picking_detail.adapter = adapter
        viewModel.pickingListRepository.getAllPickingListLine(args.pickingListNo)
            .observe(viewLifecycleOwner, {
                adapter.update(it.toMutableList())
            })
    }

    private fun setObseverable() {
        viewModel.pickingDetailViewState.observe(viewLifecycleOwner, {
            when (it) {
                is PickingDetailViewModel.PickingDetailViewState.SuccessGetLocalData -> {
                    setupMainView(it.value)
                }
                is PickingDetailViewModel.PickingDetailViewState.ErrorGetLocalData -> {
                    context?.showLongToast(it.message)
                }
            }
        })
    }

    private fun setupMainView(value: PickingListHeaderValue) {
        with(value) {
            et_customer_name.setText(sellToCustomerName)
            et_order_date.setText(requestedDeliveryDate.toNormalDate())
            et_so_no.setText(sONo)
            et_project_code.setText(projectCode)
        }
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
            showPostDialog()
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
        adapter.setOnClickListener(object :
            PickingDetailAdapter.OnPickingListDetailAdapterClicklistener {
            override fun onclicklistener(pickingListLineValue: PickingListLineValue) {
                val action =
                    PickingDetailFragmentDirections.actionPickingDetailFragmentToHistoryInputFragment(
                        args.pickingListNo,
                        Constant.PICKING_LIST,
                        partNo = pickingListLineValue.partNoOriginal
                    )
                view?.findNavController()?.navigate(action)
            }
        })
    }

    private fun showPostDialog() {
        val dialog = PickingPostDialog()
        dialog.show(requireActivity().supportFragmentManager, dialog.tag)
    }

    override fun onDestroy() {
        super.onDestroy()
        rv_picking_detail?.adapter = null
    }
}
