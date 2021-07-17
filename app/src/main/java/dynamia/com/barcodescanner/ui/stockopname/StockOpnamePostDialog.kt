package dynamia.com.barcodescanner.ui.stockopname

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dynamia.com.barcodescanner.R
import dynamia.com.core.util.crossFade
import kotlinx.android.synthetic.main.picking_post_bottom_dialog.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class StockOpnamePostDialog : BottomSheetDialogFragment() {

    val viewModel: StockOpnameViewModel by viewModel()

    private var animateDuration: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        animateDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
        return inflater.inflate(R.layout.picking_post_bottom_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setObseverable()
        setClicklistener()
        viewModel.postStockOpnameData()
    }

    private fun setupView() {
        tv_picking_post_title.text = "Stock Opname Data"
    }

    private fun setObseverable() {
        viewModel.stockOpnameViewState.observe(viewLifecycleOwner, {
            when (it) {
                StockOpnameViewModel.StockOpnameViewState.AllDataPosted -> {
                    iv_status_post_transfer_shipment.crossFade(
                        animateDuration.toLong(),
                        pb_transfer_shipment_post_dialog
                    )
                    btn_dismis_picking_post.isEnabled = true
                }
                is StockOpnameViewModel.StockOpnameViewState.ErrorPostData -> {
                    iv_status_post_transfer_shipment.crossFade(
                        animateDuration.toLong(),
                        pb_transfer_shipment_post_dialog
                    )
                    iv_status_post_transfer_shipment.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_error_circle,
                            null
                        )
                    )
                    tv_error_transfer_shipment_post.text = it.message
                    btn_dismis_picking_post.isEnabled = true
                }
                is StockOpnameViewModel.StockOpnameViewState.GetSuccessfullyPostedData -> {
                    tv_transfer_shipment_posted_count.text = it.data.toString()
                }
                is StockOpnameViewModel.StockOpnameViewState.GetUnpostedData -> {
                    tv_transfer_total_unposted.text = it.data.toString()
                }
            }
        })
    }

    private fun setClicklistener() {
        btn_dismis_picking_post.isEnabled = false
        btn_dismis_picking_post.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }
}