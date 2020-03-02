package dynamia.com.barcodescanner.ui.pickinglist.pickingdetail

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import dynamia.com.barcodescanner.R

class PickingDetailFragment : Fragment() {

    companion object {
        fun newInstance() = PickingDetailFragment()
    }

    private lateinit var viewModel: PickingDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.picking_detail_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PickingDetailViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
