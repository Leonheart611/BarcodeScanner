package dynamia.com.barcodescanner.ui.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dynamia.com.barcodescanner.BuildConfig
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.databinding.LoginFragmentBinding
import dynamia.com.barcodescanner.ui.MainActivity
import dynamia.com.barcodescanner.ui.home.HomePostAllDialog
import dynamia.com.barcodescanner.ui.login.LoginViewModel.LoginState.*
import dynamia.com.core.base.BaseFragmentBinding
import dynamia.com.core.data.entinty.UserData
import dynamia.com.core.util.EventObserver
import dynamia.com.core.util.showLongToast

@AndroidEntryPoint
class LoginFragment :
    BaseFragmentBinding<LoginFragmentBinding>(LoginFragmentBinding::inflate) {

    private val viewModel: LoginViewModel by activityViewModels()
    var activity: MainActivity? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.checkSharedPreferences()
        activity = requireActivity() as MainActivity
        initview()
        setupListener()
        setObservable()
    }

    private fun initview() {
        if (BuildConfig.BUILD_TYPE == "debug") {
            with(viewBinding) {
                etServerHost.setText(getString(R.string.server_host_name))
                tiedUsername.setText(getString(R.string.user_name))
                tiedPassword.setText(getString(R.string.password))
                etDomainname.setText(getString(R.string.domain))
                etCompanyName.setText(getString(R.string.company_name))
            }
        }
    }

    private fun setupListener() {
        with(viewBinding) {
            btnLogin.setOnClickListener {
                if (checkNotEmpty()) {
                    if (viewModel.checkServerUrl(etServerHost.text.toString())) {
                        viewModel.saveSharedPreferences(
                            baseUrl = etServerHost.text.toString(),
                            username = tiedUsername.text.toString(),
                            password = tiedPassword.text.toString(),
                            domain = etDomainname.text.toString(),
                            companyName = etCompanyName.text.toString()
                        )
                    } else {
                        context?.showLongToast("Host Name Must start with (HTTP://) and end with (/)")
                    }
                } else {
                    context?.showLongToast("Please fill all form")
                }
            }
        }
    }

    private fun checkNotEmpty(): Boolean {
        var result = true
        with(viewBinding) {
            if (tiedPassword.text.toString().isEmpty()) {
                result = false
            }
            if (tiedUsername.text.toString().isEmpty()) {
                result = false
            }
            if (etServerHost.text.toString().isEmpty()) {
                result = false
            }
            if (etDomainname.text.toString().isEmpty()) {
                result = false
            }
            return result
        }
    }

    private fun setView(userdata: UserData) {
        with(userdata) {
            with(viewBinding) {
                etServerHost.setText(hostName)
                tiedUsername.setText(username)
                tiedPassword.setText(password)
                etDomainname.setText(domainName)
                etCompanyName.setText(companyName)
            }
        }
    }

    private fun setObservable() {
        viewModel.modelState.observe(viewLifecycleOwner, {
            when (it) {
                is Success -> {
                    val dialog = CheckLoginBottomSheet()
                    dialog.isCancelable = false
                    dialog.show(requireActivity().supportFragmentManager, dialog.tag)
                }
                is Error -> {
                    context?.showLongToast(it.message)
                }
                is UserhasLogin -> {
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }
                is UserHaveData -> setView(it.userData)
                SuccessCheckLogin -> {
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    context?.showLongToast("Success Login")
                }
            }
        })
    }
}
