package dynamia.com.barcodescanner.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.util.readJsonAsset
import dynamia.com.barcodescanner.util.showToast
import kotlinx.android.synthetic.main.login_fragment.*
import java.io.InputStream


class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        setupListener()

    }

    private fun setupListener(){
        btn_login.setOnClickListener {
            context?.showToast("Login Success")
            findNavController().navigate(R.id.action_loginFragment_to_POSOList)
        }
    }

    fun setDataFromJsonLocal(){
        val pickingListHeader = context?.readJsonAsset("PickingListHeader.json")
        val pickingListLine = context?.readJsonAsset("PickingListLine.json")
        val pickingListScanEntries = context?.readJsonAsset("PickingListScanEntries.json")


    }

}
