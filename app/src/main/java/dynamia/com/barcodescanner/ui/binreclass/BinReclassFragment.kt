package dynamia.com.barcodescanner.ui.binreclass

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dynamia.com.barcodescanner.databinding.BinReclassFragmentBinding
import dynamia.com.barcodescanner.databinding.BinReclassInputDialogBinding
import dynamia.com.barcodescanner.ui.binreclass.adapter.BinReclassAdapter
import dynamia.com.core.base.BaseFragmentBinding
import dynamia.com.core.data.entinty.BinreclassHeader
import dynamia.com.core.util.EventObserver
import dynamia.com.core.util.showLongToast

@AndroidEntryPoint
class BinReclassFragment :
        BaseFragmentBinding<BinReclassFragmentBinding>(BinReclassFragmentBinding::inflate),
        BinReclassAdapter.BinreclassOnclicklistener {

    private val viewModel: BinReclassViewModel by viewModels()
    private val binReclassAdapter = BinReclassAdapter(mutableListOf(), this)
    private var rebinHeader: Dialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setObseverable()
    }

    private fun setObseverable() {
        with(viewBinding) {
            spnBinreclassFilter.onItemSelectedListener = spinerFilterSelectListener
        }
        viewModel.viewState.observe(viewLifecycleOwner, EventObserver {
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
        with(viewBinding) {
            fabAddBinReclass.setOnClickListener {
                showAddDialog()
            }
            with(rvBinreclassList) {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = binReclassAdapter
            }
            with(tbBinreclasList) {
                title = viewModel.getCompanyName()
                setNavigationOnClickListener {
                    view?.findNavController()?.popBackStack()
                }
            }
            fabUploadBinReclass.setOnClickListener {
                val dialog = BinReclassPostDialog()
                dialog.show(requireActivity().supportFragmentManager, dialog.tag)
            }
        }
    }

    override fun onclicklistener(data: BinreclassHeader) {
        findNavController().navigate(BinReclassFragmentDirections.actionBinReclassFragmentToBinreclassDetailFragment(
                binTo = data.transferToBinCode,
                binFrom = data.transferFromBinCode
        ))
    }

    private fun showAddDialog() {
        rebinHeader = Dialog(requireContext())
        rebinHeader?.let { dialog ->
            with(dialog) {
                val bind = BinReclassInputDialogBinding.inflate(layoutInflater)
                setContentView(bind.root)
                window?.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
                with(bind) {
                    btnCreateBin.setOnClickListener {
                        if (checkDialogInput(bind)) {
                            viewModel.insertBinReclass(
                                    binFrom = etBinFromCode.text.toString(),
                                    binTo = etBinToCode.text.toString())
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