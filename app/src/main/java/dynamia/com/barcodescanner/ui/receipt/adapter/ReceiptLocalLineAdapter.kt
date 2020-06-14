package dynamia.com.barcodescanner.ui.receipt.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.model.ReceiptLocalLineValue
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.picking_detail_line_item.view.*

class ReceiptLocalLineAdapter(private val pickingListLineValues: MutableList<ReceiptLocalLineValue>) :
    RecyclerView.Adapter<ReceiptLocalLineAdapter.ReceiptLocalLineHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptLocalLineHolder {
        return ReceiptLocalLineHolder(parent.inflate(R.layout.picking_detail_line_item))
    }

    override fun getItemCount(): Int {
        return pickingListLineValues.size
    }

    override fun onBindViewHolder(holder: ReceiptLocalLineHolder, position: Int) {
        pickingListLineValues[position].let {
            holder.bind(it)
        }
    }

    class ReceiptLocalLineHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(pickingListLineValue: ReceiptLocalLineValue) {
            with(itemView) {
                tv_line_no.text = pickingListLineValue.lineNo.toString()
                tv_description.text = pickingListLineValue.description
                tv_description_2.text = pickingListLineValue.documentNo
                tv_outstanding.text =
                    " ${pickingListLineValue.quantity}/${pickingListLineValue.outstandingQuantity}"
            }
        }
    }
}