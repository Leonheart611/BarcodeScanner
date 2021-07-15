package dynamia.com.barcodescanner.ui.binreclass.detail

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.binreclass.adapter.BinReclassInputAdapter
import dynamia.com.core.data.entinty.BinreclassHeader
import dynamia.com.core.data.entinty.BinreclassInputData
import dynamia.com.core.util.showLongToast
import kotlinx.android.synthetic.main.bin_reclass_header_layout.*
import kotlinx.android.synthetic.main.bin_relass_header_dialog.*
import kotlinx.android.synthetic.main.binreclass_detail_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class BinreclassDetailFragment : Fragment(), BinReclassInputAdapter.OnBinclassInputClicklistener {

    private val viewModel: BinreclassDetailViewModel by viewModel()
    private val args: BinreclassDetailFragmentArgs by navArgs()
    private var binFrom = ""
    private var binTo = ""
    private val inputAdapter = BinReclassInputAdapter(mutableListOf(), this)
    private var rebinHeader: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.binreclass_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binFrom = args.binFrom
        binTo = args.binTo
        viewModel.getLocalDataHeader(binFrom, binTo)
        setupView()
        setObseverable()
    }

    private fun setObseverable() {
        viewModel.viewState.observe(viewLifecycleOwner, {
            when (it) {
                is BinreclassDetailViewModel.BinReclassViewState.OnErrorGetLocalData -> {
                    context?.showLongToast(it.error)
                }
                is BinreclassDetailViewModel.BinReclassViewState.SuccessGetLocalData -> {
                    setupDetailView(it.data)
                }
                is BinreclassDetailViewModel.BinReclassViewState.SuccessUpdateHeaderData -> {
                    context?.showLongToast("Success Update Data")
                    binFrom = it.fromBin
                    binTo = it.toBin
                    viewModel.getLocalDataHeader(binFrom = it.fromBin, binTo = it.toBin)
                }
            }
        })
    }

    fun setupView() {
        with(tb_binreclas_detail) {
            title = viewModel.getCompanyName()
            setNavigationOnClickListener {
                view?.findNavController()?.popBackStack()
            }
        }
        fab_add_rebin_input.setOnClickListener {
            val dialog = BinreclassInputDialog.newInstance(
                fromBin = binFrom, toBin = binTo)
            dialog.show(requireActivity().supportFragmentManager, dialog.tag)
        }
        with(rv_rebin_class_input) {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = inputAdapter
        }
        btn_edit.setOnClickListener {
            showUpdateDialog()
        }
    }

    private fun setupDetailView(data: BinreclassHeader) {
        with(data) {
            tv_rebin_documentno.text = documentNo
            tv_rebin_from_code.text =
                getString(R.string.bin_reclass_detail_from_code, transferFromBinCode)
            tv_rebin_to_code.text =
                getString(R.string.bin_reclass_detail_to_code, transferToBinCode)
            tv_rebin_detail_date.text = getString(R.string.bin_reclass_detail_date, date)
        }
        tv_rebin_detail_user.text =
            getString(R.string.bin_reclass_detail_user, viewModel.getUserName())
        btn_edit.isVisible = !data.sync_status
        viewModel.repository.getBinreclassInputData(data.id!!)
            .observe(viewLifecycleOwner, {
                inputAdapter.addData(it.toMutableList())
            })
    }

    override fun onclicklistener(value: BinreclassInputData) {
        value.id?.let {
            val dialog = BinreclassInputDialog.newInstance(
                fromBin = binFrom,
                toBin = binTo,
                id = it)
            dialog.show(requireActivity().supportFragmentManager, dialog.tag)
        }
    }

    private fun showUpdateDialog() {
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
                    et_bin_from_code.setText(args.binFrom)
                    et_bin_to_code.setText(args.binTo)
                    btn_create_bin.text = "Update"
                    btn_create_bin.setOnClickListener {
                        if (checkDialogInput(this)) {
                            viewModel.updateRebinClassHeader(
                                fromBin = et_bin_from_code.text.toString(),
                                toBin = et_bin_to_code.text.toString())
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