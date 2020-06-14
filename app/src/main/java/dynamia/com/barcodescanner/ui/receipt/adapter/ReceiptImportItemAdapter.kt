package dynamia.com.barcodescanner.ui.receipt.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.model.ReceiptImportHeaderValue
import dynamia.com.core.util.inflate
import dynamia.com.core.util.toNormalDate
import kotlinx.android.synthetic.main.item_receipt_list.view.*

class ReceiptImportItemAdapter(
    val receiptImportHeaderValue: MutableList<ReceiptImportHeaderValue>,
    val listener: ReceiptImportListener
) :
    RecyclerView.Adapter<ReceiptImportItemAdapter.ReceiptItemHolder>() {

    interface ReceiptImportListener {
        fun onReceiptImportClickListener(documentNo: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptItemHolder {
        return ReceiptItemHolder(parent.inflate(R.layout.item_receipt_list))
    }

    override fun getItemCount(): Int {
        return receiptImportHeaderValue.size
    }

    override fun onBindViewHolder(holder: ReceiptItemHolder, position: Int) {
        receiptImportHeaderValue[position].let {
            holder.bind(it, listener)
        }
    }

    class ReceiptItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: ReceiptImportHeaderValue, listener: ReceiptImportListener) {
            with(itemView) {
                tv_receipt_no.text = value.no
                tv_receipt_vendor_no.text = value.purchaseOrderNo
                tv_receipt_vendor_name.text = value.buyFromVendorName
                tv_date_expected.text = value.postingDate.toNormalDate()
                setOnClickListener {
                    listener.onReceiptImportClickListener(value.no)
                }
            }
        }
    }
}