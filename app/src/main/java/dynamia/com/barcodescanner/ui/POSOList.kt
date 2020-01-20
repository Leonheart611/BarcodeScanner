package dynamia.com.barcodescanner.ui

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.viewmodel.PosolistViewModel
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

        }

    }

}
