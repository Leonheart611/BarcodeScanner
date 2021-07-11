package dynamia.com.barcodescanner.ui.checkstock

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dynamia.com.barcodescanner.R

class CheckStockFragment : Fragment() {

    companion object {
        fun newInstance() = CheckStockFragment()
    }

    private lateinit var viewModel: CheckStockViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.check_stock_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CheckStockViewModel::class.java)
        // TODO: Use the ViewModel
    }

}