package dynamia.com.barcodescanner.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.util.showToast
import dynamia.com.barcodescanner.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.login_fragment.*


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
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        setupListener()

    }

    fun setupListener(){
        btn_login.setOnClickListener {
            context?.showToast("Login Success")
            findNavController().navigate(R.id.action_loginFragment_to_POSOList)
        }
    }

}
