package dynamia.com.core.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VBinding : ViewBinding, ViewModel : androidx.lifecycle.ViewModel> :
    Fragment() {

    open var useSharedViewModel: Boolean = true

    protected lateinit var viewModel: ViewModel
    protected abstract fun getViewModelClass(): Class<ViewModel>

    protected lateinit var binding: VBinding
    protected abstract fun getViewBinding(): VBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        init()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        observeData()
    }

    open fun setUpViews() {}

    open fun observeView() {}

    open fun observeData() {}

    private fun init() {
        try {
            binding = getViewBinding()
            viewModel = if (useSharedViewModel) {
                ViewModelProvider(requireActivity()).get(
                    getViewModelClass()
                )
            } else {
                ViewModelProvider(this).get(getViewModelClass())
            }
        } catch (e: Exception) {
            e.stackTrace
            Log.e("Error Initialize VM", e.localizedMessage)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}