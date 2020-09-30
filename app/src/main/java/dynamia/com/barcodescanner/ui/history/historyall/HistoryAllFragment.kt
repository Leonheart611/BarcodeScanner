package dynamia.com.barcodescanner.ui.history.historyall

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import dynamia.com.barcodescanner.R

class HistoryAllFragment : Fragment() {

    private lateinit var viewModel: HistoryAllViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.history_all_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HistoryAllViewModel::class.java)
        // TODO: Use the ViewModel
    }

}