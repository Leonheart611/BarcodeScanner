package dynamia.com.barcodescanner.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dynamia.com.barcodescanner.R
import dynamia.com.core.util.crossFade
import dynamia.com.core.util.gone
import kotlinx.android.synthetic.main.fragment_post_bin_reclass_dialog.*
import kotlinx.android.synthetic.main.fragment_post_stock_count_dialog.*
import kotlinx.android.synthetic.main.home_post_all_dialog.*
import kotlinx.android.synthetic.main.picking_post_bottom_dialog.*
import kotlinx.android.synthetic.main.receipt_post_bottom_sheet_dialog.*
import kotlinx.android.synthetic.main.receipt_post_import_bottom_sheet_dialog.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomePostAllDialog : BottomSheetDialogFragment() {

    private val viewModel: HomeViewModel by viewModel()
    private var animateDuration: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        animateDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
        return inflater.inflate(R.layout.home_post_all_dialog, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setObseverable()
        callAllApi()
        btn_dismiss_all_post.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    private fun callAllApi() {
        with(viewModel) {
            postReceiptData()
            postShipmentData()
            postPurchaseData()
            postStockOpnameData()
            postBinReclassData()
        }
    }

    private fun setupView() {
        btn_dismis_picking_post.gone()
        btn_dismis_receipt_post.gone()
        btn_dismis_receipt_import.gone()
        btn_dismis_stock_count.gone()
        btn_dismis_bin_reclass_post.gone()
    }

    private fun setObseverable() {
        viewModel.homePostViewState.observe(viewLifecycleOwner, {
            when (it) {
                is HomeViewModel.HomePostViewState.GetUnpostedTransferShipment -> {
                    tv_transfer_total_unposted.text = it.data.toString()
                }
                is HomeViewModel.HomePostViewState.GetSuccessTransferShipment -> {
                    tv_transfer_shipment_posted_count.text = it.data.toString()
                }
                is HomeViewModel.HomePostViewState.ErrorPostTransferShipment -> {
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
                }
                HomeViewModel.HomePostViewState.AllDataPostedTransferShipment -> {
                    iv_status_post_transfer_shipment.crossFade(
                        animateDuration.toLong(),
                        pb_transfer_shipment_post_dialog
                    )
                }


                is HomeViewModel.HomePostViewState.GetUnpostedTransferReceipt -> {
                    tv_receipt_total_post.text = it.data.toString()
                }
                is HomeViewModel.HomePostViewState.GetSuccessfulTransferReceipt -> {
                    tv_receipt_posted_count.text = it.data.toString()
                }
                is HomeViewModel.HomePostViewState.ErrorPostTransferReceipt -> {
                    iv_status_post_receipt.crossFade(
                        animateDuration.toLong(),
                        pb_receipt_post_dialog
                    )
                    iv_status_post_receipt.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_error_circle,
                            null
                        )
                    )
                    tv_error_receipt_post.text = it.message
                }
                HomeViewModel.HomePostViewState.SuccessPostallTransferReceipt -> {
                    iv_status_post_receipt.crossFade(
                        animateDuration.toLong(),
                        pb_receipt_post_dialog
                    )
                }


                is HomeViewModel.HomePostViewState.GetUnpostedPurchase -> {
                    tv_purhcase_unposted_count.text = it.data.toString()
                }
                is HomeViewModel.HomePostViewState.GetSuccessfulPurchase -> {
                    tv_purchase_posted.text = it.data.toString()
                }
                is HomeViewModel.HomePostViewState.ErrorPostPurchase -> {
                    iv_post_status_purchase.crossFade(
                        animateDuration.toLong(),
                        pb_post_purchase
                    )
                    iv_post_status_purchase.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_error_circle,
                            null
                        )
                    )
                    tv_error_post_purchase.text = it.message
                }
                HomeViewModel.HomePostViewState.SuccessPostallPurchase -> {
                    iv_post_status_purchase.crossFade(
                        animateDuration.toLong(),
                        pb_post_purchase
                    )
                }


                is HomeViewModel.HomePostViewState.GetUnpostedStock -> {
                    tv_stock_total_post.text = it.data.toString()
                }
                is HomeViewModel.HomePostViewState.GetSuccessfulStock -> {
                    tv_stock_posted_count.text = it.data.toString()
                }
                is HomeViewModel.HomePostViewState.ErrorPostStock -> {
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
                }
                HomeViewModel.HomePostViewState.SuccessPostallStock -> {
                    iv_status_post_stock_count.crossFade(
                        animateDuration.toLong(),
                        pb_post_stock_count
                    )
                }

                is HomeViewModel.HomePostViewState.ErrorPostBinReclass -> {
                    iv_bin_reclass_status_post.crossFade(
                        animateDuration.toLong(),
                        pb_bin_reclass_post
                    )
                    iv_bin_reclass_status_post.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_error_circle,
                            null
                        )
                    )
                    tv_error_message_bin_reclass.text = it.message
                }
                is HomeViewModel.HomePostViewState.GetSuccessfulBinReclass -> {
                    tv_bin_reclass_post_count.text = it.data.toString()
                }

                is HomeViewModel.HomePostViewState.GetUnpostedBinReclass -> {
                    tv_bin_reclass_unposted_count.text = it.data.toString()
                }
                HomeViewModel.HomePostViewState.SuccessPostallBinReclass -> {
                    iv_bin_reclass_status_post.crossFade(
                        animateDuration.toLong(),
                        pb_bin_reclass_post
                    )
                }
            }
        })
    }

}