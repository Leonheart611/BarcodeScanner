package dynamia.com.barcodescanner.ui.receipt.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.model.ReceiptLocalHeaderValue
import dynamia.com.core.util.inflate
import dynamia.com.core.util.toNormalDate
import kotlinx.android.synthetic.main.item_receipt_list.view.*

class ReceiptLocalItemAdapter(val values:MutableList<ReceiptLocalHeaderValue>,val listener:OnReceiptLocalListener):RecyclerView.Adapter<ReceiptLocalItemAdapter.ReceiptLocalHolder>() {

    interface OnReceiptLocalListener{
        fun onReceiptLocalClicklistener(documentNo:String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptLocalHolder {
        return ReceiptLocalHolder(parent.inflate(R.layout.receipt_item_import_layout))
    }

    override fun getItemCount(): Int {
        return values.size
    }

    override fun onBindViewHolder(holder: ReceiptLocalHolder, position: Int) {
        values[position].let {
            holder.bind(it,listener)
        }
    }

    class ReceiptLocalHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: ReceiptLocalHeaderValue,listener:OnReceiptLocalListener) {
            with(itemView){
                tv_receipt_no.text = value.no
                tv_receipt_vendor_no.text = value.buyFromVendorNo
                tv_receipt_vendor_name.text = value.buyFromVendorName
                tv_date_expected.text = value.expectedReceiptDate.toNormalDate()
                setOnClickListener {
                    listener.onReceiptLocalClicklistener(value.no)
                }
            }
        }
    }

}