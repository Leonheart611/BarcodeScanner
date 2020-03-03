package dynamia.com.barcodescanner.ui.pickinglist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.pickinglist.adapter.PickingListAdapter
import kotlinx.android.synthetic.main.pickinglist_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PickingListFragment : Fragment(), PickingListAdapter.OnPickinglistListener {

    private val viewModel: PickingListViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.pickinglist_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }
    private fun initView(){
        viewModel.pickingListRepository.getAllPickingListHeader().observe(viewLifecycleOwner,
            Observer {
                val adapter = PickingListAdapter(
                    it.toMutableList(),
                    this
                )
                rv_pickinglist.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                rv_pickinglist.adapter = adapter
            })


    }

    override fun onPickingListClickListener(pickingListNo: String) {

    }
}