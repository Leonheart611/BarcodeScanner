package dynamia.com.barcodescanner.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.viewmodel.PosolistViewModel
import kotlinx.android.synthetic.main.dialog_select_input_type.*
import kotlinx.android.synthetic.main.posolist_fragment.*

class POSOList : Fragment() {

    private lateinit var viewModel: PosolistViewModel

    var isRotated = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.posolist_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PosolistViewModel::class.java)

        fab_add_item.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        val dialog = Dialog(requireContext())
        with(dialog) {
            setContentView(R.layout.dialog_select_input_type)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            btn_receiving.setOnClickListener {
                findNavController().navigate(R.id.action_POSOList_to_receivingFragment)
                dismiss()
            }
            btn_shipment.setOnClickListener {
                findNavController().navigate(R.id.action_POSOList_to_receivingFragment)
                dismiss()
            }
            show()
        }
    }

}
