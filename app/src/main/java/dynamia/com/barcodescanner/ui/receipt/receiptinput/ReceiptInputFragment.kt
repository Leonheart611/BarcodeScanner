package dynamia.com.barcodescanner.ui.receipt.receiptinput

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import dynamia.com.barcodescanner.R
import dynamia.com.core.view.NormalInputLayout
import kotlinx.android.synthetic.main.receipt_form_item.*
import kotlinx.android.synthetic.main.receipt_input_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReceiptInputFragment : Fragment() {
    private val viewModel: ReceiptInputViewModel by viewModel()
    private val args: ReceiptInputFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.receipt_input_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupView()
        setupListener()
    }

    private fun setupView() {
        tv_receipt_detail_po.text = args.poNo
    }

    private fun setupListener() {

    }

    fun generalTextWatcher(et: NormalInputLayout) {
        et.addTextWatcher(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (checkMandatoryData()){

                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })
    }

    fun checkMandatoryData(): Boolean {
        var result = true
        if (et_packingid.isEmpty()) {
            result = false
        }
        if (et_po_no.isEmpty()) {
            result = false
        }
        if (et_part_no.isEmpty()){
            result = false
        }
        if (et_sn_no.isEmpty()){
            result = false
        }
        if (et_shipset.isEmpty()){
            result = false
        }
        if (et_trackingid.isEmpty()){
            result = false
        }
        if (et_mac_address.isEmpty()){
            result = false
        }
        return result
    }

}
