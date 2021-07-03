package dynamia.com.barcodescanner.ui.history

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
import dynamia.com.barcodescanner.ui.history.adapter.HistoryTransferInputAdapter
import dynamia.com.barcodescanner.ui.transferstore.transferinput.TransferHistoryBottomSheet
import dynamia.com.core.data.entinty.TransferInputData
import dynamia.com.core.util.Constant
import kotlinx.android.synthetic.main.history_input_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryInputFragment : Fragment(), HistoryTransferInputAdapter.OnHistorySelected {

    private val viewModel: HistoryInputViewModel by viewModel()
    private val args: HistoryInputFragmentArgs by navArgs()
    private var scanEntriesAdapter = HistoryTransferInputAdapter(mutableListOf(), this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.history_input_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecylerView()
        setupView()
        setupListener()
    }

    private fun setupRecylerView() {
        with(rv_input_history) {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = when (args.source) {
                else -> scanEntriesAdapter
            }
        }
    }

    private fun setupView() {
        when (args.source) {
            Constant.TRANSFER_SHIPMENT -> {
                tv_transfer_input.text = getString(R.string.transfer_shipment_history_title)
                args.documentNo?.let { documentNo ->
                    viewModel.transferShipmentRepository.getTransferInputHistoryLiveData(documentNo)
                        .observe(viewLifecycleOwner, {
                            scanEntriesAdapter.updateData(it.toMutableList())
                        })
                }
            }
        }
    }

    private fun setupListener() {
        tb_history.title = viewModel.getCompanyName()
        tb_history.setNavigationOnClickListener {
            view?.findNavController()?.popBackStack()
        }
    }

    override fun onHistorySelectDelete(value: TransferInputData) {
        value.id?.let {
            val dialog = TransferHistoryBottomSheet.newInstance(it)
            dialog.show(requireActivity().supportFragmentManager, dialog.tag)
        }
    }

}
