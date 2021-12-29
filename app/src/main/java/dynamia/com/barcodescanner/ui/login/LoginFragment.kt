package dynamia.com.barcodescanner.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dynamia.com.barcodescanner.BuildConfig
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.MainActivity
import dynamia.com.barcodescanner.ui.login.LoginViewModel.LoginState.*
import dynamia.com.core.util.showLongToast
import kotlinx.android.synthetic.main.login_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginFragment : Fragment() {
	
	private val viewModel: LoginViewModel by viewModel()
	var activity: MainActivity? = null
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		viewModel.checkSharedPreferences()
	}
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(R.layout.login_fragment, container, false)
	}
	
	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		activity = requireActivity() as MainActivity
		initview()
		setupListener()
		setObservable()
	}
	
	private fun initview() {
		if (BuildConfig.BUILD_TYPE == "debug") {
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
					viewModel.saveSharedPreferences(
						hostname = et_server_host.text.toString(),
						username = tied_username.text.toString(),
						password = tied_password.text.toString(),
						employee = et_employee.text.toString()
					)
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
	
	private fun setObservable() {
		viewModel.userRepository.getUserData().observe(viewLifecycleOwner, {
			it?.let {
				et_server_host.setText(it.hostName)
				tied_username.setText(it.username)
				tied_password.setText(it.password)
				et_employee.setText(it.employeeCode)
			}
		})
		viewModel.modelState.observe(viewLifecycleOwner, {
			when (it) {
				is Success -> {
					findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
					context?.showLongToast("Success Save data")
				}
				is Error -> {
					context?.showLongToast(it.message)
				}
				is ShowLoading -> {
					activity?.showLoading(it.boolean)
				}
				is UserhasLogin -> {
					findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
				}
			}
		})
	}
}
