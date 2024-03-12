package dynamia.com.barcodescanner.ui.binreclass.detail

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.databinding.BinReclassInputDialogBinding
import dynamia.com.barcodescanner.databinding.BinreclassDetailFragmentBinding
import dynamia.com.barcodescanner.ui.binreclass.adapter.BinReclassInputAdapter
import dynamia.com.barcodescanner.ui.binreclass.detail.BinreclassInputDialog.ADD_TYPE.*
import dynamia.com.core.base.BaseFragmentBinding
import dynamia.com.core.data.entinty.BinreclassHeader
import dynamia.com.core.data.entinty.BinreclassInputData
import dynamia.com.core.util.ifCounterIsNull
import dynamia.com.core.util.showLongToast

@AndroidEntryPoint
class BinreclassDetailFragment :
    BaseFragmentBinding<BinreclassDetailFragmentBinding>(BinreclassDetailFragmentBinding::inflate),
    BinReclassInputAdapter.OnBinclassInputClicklistener {

    private val viewModel: BinreclassDetailViewModel by viewModels()
    private val args: BinreclassDetailFragmentArgs by navArgs()
    private var binFrom = ""
    private var binTo = ""
    private val inputAdapter = BinReclassInputAdapter(mutableListOf(), this)
    private var rebinHeader: Dialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binFrom = args.binFrom
        binTo = args.binTo
        viewModel.getLocalDataHeader(binFrom, binTo)
        setupView()
        setObseverable()
    }

    private fun setObseverable() {
        viewModel.viewState.observe(viewLifecycleOwner) {
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
        }
    }

    fun setupView() {
        with(viewBinding) {
            with(tbBinreclasDetail) {
                title = viewModel.getCompanyName()
                setNavigationOnClickListener {
                    view?.findNavController()?.popBackStack()
                }
            }
            fabAddRebinInput.setOnClickListener {
                val dialog = BinreclassInputDialog.newInstance(
                    fromBin = binFrom,
                    toBin = binTo,
                    addType = MANUAL
                )
                dialog.show(requireActivity().supportFragmentManager, dialog.tag)
            }
            fabInputRebin.setOnClickListener {
                val dialog = BinreclassInputDialog.newInstance(
                    fromBin = binFrom,
                    toBin = binTo,
                    addType = SCAN
                )
                dialog.show(requireActivity().supportFragmentManager, dialog.tag)
            }
            with(rvRebinClassInput) {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = inputAdapter
            }
            includeHeader.btnEdit.setOnClickListener {
                showUpdateDialog()
            }
        }

    }

    private fun setupDetailView(data: BinreclassHeader) {
        with(viewBinding.includeHeader) {
            with(data) {
                tvRebinDocumentno.text = documentNo
                tvRebinFromCode.text =
                    getString(R.string.bin_reclass_detail_from_code, transferFromBinCode)
                tvRebinToCode.text =
                    getString(R.string.bin_reclass_detail_to_code, transferToBinCode)
                tvRebinDetailDate.text = getString(R.string.bin_reclass_detail_date, date)
                viewModel.repository.getBinReclassTotalQtyScan(documentNo)
                    .observe(viewLifecycleOwner) {
                        tvRebinTotalQty.text =
                            getString(R.string.bin_reclass_total_scan, it.ifCounterIsNull())
                    }
            }
            tvRebinDetailUser.text =
                getString(R.string.bin_reclass_detail_user, viewModel.getUserName())
            btnEdit.isVisible = !data.sync_status

        }
        viewModel.repository.getBinreclassInputData(data.id!!)
            .observe(viewLifecycleOwner) {
                inputAdapter.addData(it.toMutableList())
            }
    }

    override fun onclicklistener(value: BinreclassInputData) {
        value.id?.let {
            val dialog = BinreclassInputDialog.newInstance(
                fromBin = binFrom,
                toBin = binTo,
                id = it,
                addType = MANUAL
            )
            dialog.show(requireActivity().supportFragmentManager, dialog.tag)
        }
    }

    private fun showUpdateDialog() {
        context?.let { context ->
            rebinHeader = Dialog(context)
            rebinHeader?.let { dialog ->
                with(dialog) {
                    val bind = BinReclassInputDialogBinding.inflate(layoutInflater)
                    setContentView(bind.root)
                    window
                        ?.setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    with(bind) {
                        etBinFromCode.setText(args.binFrom)
                        etBinToCode.setText(args.binTo)
                        btnCreateBin.text = "Update"
                        btnCreateBin.setOnClickListener {
                            if (checkDialogInput(this)) {
                                viewModel.updateRebinClassHeader(
                                    fromBin = etBinFromCode.text.toString(),
                                    toBin = etBinToCode.text.toString()
                                )
                            }
                        }
                        btnCancleBin.setOnClickListener {
                            dismiss()
                        }
                    }
                    show()
                }
            }
        }
    }

    private fun checkDialogInput(dialog: BinReclassInputDialogBinding): Boolean {
        with(dialog) {
            tilBinFrom.error = null
            tilBinTo.error = null
            var result = true
            if (etBinFromCode.text.toString().isEmpty()) {
                result = false
                tilBinFrom.error = "Harap di isi"
            }
            if (etBinToCode.text.toString().isEmpty()) {
                result = false
                tilBinTo.error = "Harap di isi"
            }
            return result
        }
    }
}