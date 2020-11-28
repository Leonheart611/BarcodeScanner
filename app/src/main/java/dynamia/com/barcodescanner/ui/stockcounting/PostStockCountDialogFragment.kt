package dynamia.com.barcodescanner.ui.stockcounting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dynamia.com.barcodescanner.R
import dynamia.com.core.util.crossFade
import kotlinx.android.synthetic.main.fragment_post_stock_count_dialog.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class PostStockCountDialogFragment : BottomSheetDialogFragment() {

    private val viewModel: StockCountingViewModel by viewModel()
    private var animateDuration: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        animateDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
        return inflater.inflate(R.layout.fragment_post_stock_count_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.postStockCountDataNew()
        setObservable()
        setOnClicklistener()
    }

    private fun setObservable() {
        viewModel.stockCountPostViewState.observe(viewLifecycleOwner, {
            when (it) {
                is StockCountingViewModel.StockCountPostViewState.GetUnpostedData -> {
                    tv_stock_total_post.text = it.data.toString()
                }
                is StockCountingViewModel.StockCountPostViewState.UpdateSuccessPosted -> {
                    tv_stock_posted_count.text = it.data.toString()
                }
                is StockCountingViewModel.StockCountPostViewState.ErrorPostData -> {
                    iv_status_post_stock_count.crossFade(
                        animateDuration.toLong(),
                        pb_post_stock_count
                    )
                    iv_status_post_stock_count.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_error_circle,
                            null
                        )
                    )
                    tv_error_stock_count_post.text = it.message
                    btn_dismis_stock_count.isEnabled = true
                }
                StockCountingViewModel.StockCountPostViewState.SuccessPostedAllData -> {
                    iv_status_post_stock_count.crossFade(
                        animateDuration.toLong(),
                        pb_post_stock_count
                    )
                    btn_dismis_stock_count.isEnabled = true
                }
            }
        })
    }

    fun setOnClicklistener() {
        btn_dismis_stock_count.isEnabled = false
        btn_dismis_stock_count.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }
}