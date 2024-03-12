package dynamia.com.barcodescanner.ui.history

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import dynamia.com.barcodescanner.databinding.HistoryLandingFragmentBinding
import dynamia.com.barcodescanner.ui.history.adapter.HistoryPagerAdapter
import dynamia.com.core.base.BaseFragmentBinding

@AndroidEntryPoint
class HistoryLandingFragment :
    BaseFragmentBinding<HistoryLandingFragmentBinding>(HistoryLandingFragmentBinding::inflate) {

    private var pagerAdapter: HistoryPagerAdapter? = null
    private val args: HistoryLandingFragmentArgs by navArgs()
    private val viewModel: HistoryInputViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPagger()
        setupListener()
    }

    fun setupViewPagger() = with(viewBinding) {
        pagerAdapter = HistoryPagerAdapter(lifecycle, parentFragmentManager)
        pagerAdapter?.addFragment(
            HistoryInputFragment.newInstance(
                historyType = args.historyType,
                documentNo = args.documentNo ?: "",
                inputValidation = true
            )
        )
        pagerAdapter?.addFragment(
            HistoryInputFragment.newInstance(
                historyType = args.historyType,
                documentNo = args.documentNo ?: "",
                inputValidation = false
            )
        )
        historyPagger.adapter = pagerAdapter
        TabLayoutMediator(historyTabLayout, historyPagger) { tab, position ->
            if (position == 0) {
                tab.text = "Input Validate"
            } else {
                tab.text = "Input Accidental"
            }
        }.attach()
    }

    private fun setupListener() {
        viewBinding.tbHistory.title = viewModel.getCompanyName()
        viewBinding.tbHistory.setNavigationOnClickListener {
            view?.findNavController()?.popBackStack()
        }
    }

}