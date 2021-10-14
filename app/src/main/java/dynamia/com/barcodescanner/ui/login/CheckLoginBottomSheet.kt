package dynamia.com.barcodescanner.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dynamia.com.barcodescanner.databinding.DialogSelectInputTypeBinding
import dynamia.com.core.util.EventObserver

@AndroidEntryPoint
class CheckLoginBottomSheet : BottomSheetDialogFragment() {

    val viewModel: CheckLoginViewModel by viewModels()
    val loginViewModel: LoginViewModel by activityViewModels()

    private lateinit var _viewBinding: DialogSelectInputTypeBinding
    val viewBinding by lazy { _viewBinding }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _viewBinding = DialogSelectInputTypeBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.btnCheckLogin.isVisible = false
        viewModel.getTransferData()
        viewModel.loginViewState.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                is CheckLoginViewModel.LoginViewState.LoginFailed -> {
                    viewBinding.tvCheckLoginMessage.text = it.message
                    viewBinding.btnCheckLogin.setOnClickListener {
                        dismiss()
                    }
                }
                CheckLoginViewModel.LoginViewState.LoginSuccess -> {
                    viewBinding.btnCheckLogin.isVisible = true
                    viewBinding.btnCheckLogin.setOnClickListener {
                        loginViewModel.setSuccessCheckLogin()
                        dismiss()
                    }
                }
            }
        })
    }

}