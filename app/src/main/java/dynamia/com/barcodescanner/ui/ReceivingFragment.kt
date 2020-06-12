package dynamia.com.barcodescanner.ui

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.viewmodel.ReceivingViewModel

class ReceivingFragment : Fragment() {

    companion object {
        fun newInstance() = ReceivingFragment()
    }

    private lateinit var viewModel: ReceivingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.receiving_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ReceivingViewModel::class.java)
        // TODO: Use the ViewModel
    }

}