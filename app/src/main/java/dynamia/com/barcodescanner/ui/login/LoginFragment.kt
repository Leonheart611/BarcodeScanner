package dynamia.com.barcodescanner.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dynamia.com.barcodescanner.BuildConfig
import dynamia.com.barcodescanner.R
import dynamia.com.core.util.showLongToast
import kotlinx.android.synthetic.main.login_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (viewModel.checkLoginVariables())
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initview()
        setupListener()
    }

    private fun initview() {
        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            et_server_host.setText(getString(R.string.server_host_name))
            tied_username.setText(getString(R.string.user_name))
            tied_password.setText(getString(R.string.password))
            et_employee.setText(getString(R.string.employee_name))
        }
    }

    private fun setupListener() {
        btn_login.setOnClickListener {
            if (checkNotEmpty()) {
                if (et_server_host.text.toString().endsWith("/")) {
                    viewModel.saveLoginVariable(
                        hostname = et_server_host.text.toString(),
                        username = tied_username.text.toString(),
                        password = tied_password.text.toString(),
                        employee = et_employee.text.toString()
                    )
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                } else {
                    context?.showLongToast("Host Name Must end with (/)")
                }
            } else {
                context?.showLongToast("Please fill all form")
            }
        }
    }

    private fun checkNotEmpty(): Boolean {
        var result = true
        if (tied_password.text.toString().isEmpty()) {
            result = false
        }
        if (tied_username.text.toString().isEmpty()) {
            result = false
        }
        if (et_server_host.text.toString().isEmpty()) {
            result = false
        }
        if (et_employee.text.toString().isEmpty()) {
            result = false
        }
        return result
    }
}
