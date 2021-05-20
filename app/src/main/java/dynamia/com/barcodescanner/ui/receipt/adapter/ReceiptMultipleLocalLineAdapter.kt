package dynamia.com.barcodescanner.ui.receipt.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.model.ReceiptLocalLineValue
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.picking_detail_line_item.view.*

class ReceiptMultipleLocalLineAdapter(
    private val receiptImportLines: MutableList<ReceiptLocalLineValue>,
    private val listener: OnMultipleLocalLineListener
) : RecyclerView.Adapter<ReceiptMultipleLocalLineAdapter.ReceiptHolder>() {
    interface OnMultipleLocalLineListener {
        fun onMultipleLocalLineSelected(data: ReceiptLocalLineValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptHolder {
        return ReceiptHolder(parent.inflate(R.layout.picking_detail_line_item))
    }

    override fun getItemCount(): Int {
        return receiptImportLines.size
    }

    override fun onBindViewHolder(holder: ReceiptHolder, position: Int) {
        receiptImportLines[position].let {
            holder.bind(it, listener)
        }
    }

    class ReceiptHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: ReceiptLocalLineValue, listener: OnMultipleLocalLineListener) {
            with(itemView) {
                tv_item_code.text = data.lineNo.toString()
                tv_description.text = data.description
                tv_qty.text =
                    "${data.alredyScanned}/${data.quantity}/${data.outstandingQuantity}"
                setOnClickListener {
                    listener.onMultipleLocalLineSelected(data)
                }
            }
        }
    }
}