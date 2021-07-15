package dynamia.com.barcodescanner.ui.binreclass

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.binreclass.adapter.BinReclassAdapter
import dynamia.com.barcodescanner.ui.home.HomeFragmentDirections
import dynamia.com.barcodescanner.ui.transferstore.transferdetail.PickingPostDialog
import dynamia.com.core.data.entinty.BinreclassHeader
import dynamia.com.core.util.showLongToast
import kotlinx.android.synthetic.main.bin_reclass_fragment.*
import kotlinx.android.synthetic.main.bin_relass_header_dialog.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class BinReclassFragment : Fragment(), BinReclassAdapter.BinreclassOnclicklistener {

    private val viewModel: BinReclassViewModel by viewModel()
    private val binReclassAdapter = BinReclassAdapter(mutableListOf(), this)
    private var rebinHeader: Dialog? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.bin_reclass_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setObseverable()
    }

    private fun setObseverable() {
        spn_binreclass_filter.onItemSelectedListener = spinerFilterSelectListener
        viewModel.viewState.observe(viewLifecycleOwner, {
            when (it) {
                is BinReclassViewModel.BinReclassInputState.OnFailedSave -> {
                    context?.showLongToast(it.message)
                }
                is BinReclassViewModel.BinReclassInputState.OnSuccessSave -> {
                    context?.showLongToast("Success Create Bin Class")
                    findNavController().navigate(BinReclassFragmentDirections.actionBinReclassFragmentToBinreclassDetailFragment(
                        binTo = it.binTo,
                        binFrom = it.binFrom
                    ))
                    rebinHeader?.dismiss()
                }
            }
        })
    }

    private val spinerFilterSelectListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            when (position) {
                0 -> viewModel.binreclassRepository.getAllBinReclassHeader()
                    .observe(viewLifecycleOwner, {
                        binReclassAdapter.updateData(it.toMutableList())
                    })
                1 -> viewModel.binreclassRepository.getALlSycn(false)
                    .observe(viewLifecycleOwner, {
                        binReclassAdapter.updateData(it.toMutableList())
                    })
                2 -> viewModel.binreclassRepository.getALlSycn(true)
                    .observe(viewLifecycleOwner, {
                        binReclassAdapter.updateData(it.toMutableList())
                    })
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

    }

    private fun setupView() {
        with(tb_binreclas_list) {
            title = viewModel.getCompanyName()
            setNavigationOnClickListener {
                view?.findNavController()?.popBackStack()
            }
        }
        with(rv_binreclass_list) {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = binReclassAdapter
        }
        fab_add_bin_reclass.setOnClickListener {
            showAddDialog()
        }
        fab_upload_bin_reclass.setOnClickListener {
            val dialog = BinReclassPostDialog()
            dialog.show(requireActivity().supportFragmentManager, dialog.tag)
        }
    }

    override fun onclicklistener(data: BinreclassHeader) {
        findNavController().navigate(BinReclassFragmentDirections.actionBinReclassFragmentToBinreclassDetailFragment(
            binTo = data.transferToBinCode,
            binFrom = data.transferFromBinCode
        ))
    }

    private fun showAddDialog() {
        context?.let { context ->
            rebinHeader = Dialog(context)
            rebinHeader?.let { dialog ->
                with(dialog) {
                    setContentView(R.layout.bin_relass_header_dialog)
                    window
                        ?.setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    btn_create_bin.setOnClickListener {
                        if (checkDialogInput(this)) {
                            viewModel.insertBinReclass(
                                binFrom = et_bin_from_code.text.toString(),
                                binTo = et_bin_to_code.text.toString())
                        }
                    }
                    btn_cancle_bin.setOnClickListener {
                        dismiss()
                    }
                    show()
                }
            }
        }
    }

    fun checkDialogInput(dialog: Dialog): Boolean {
        with(dialog) {
            til_bin_from.error = null
            til_bin_to.error = null
            var result = true
            if (et_bin_from_code.text.toString().isEmpty()) {
                result = false
                til_bin_from.error = "Harap di isi"
            }
            if (et_bin_to_code.text.toString().isEmpty()) {
                result = false
                til_bin_to.error = "Harap di isi"
            }
            return result
        }
    }

}