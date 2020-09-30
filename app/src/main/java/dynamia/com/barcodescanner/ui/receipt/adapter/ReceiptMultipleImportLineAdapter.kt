package dynamia.com.barcodescanner.ui.receipt.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.model.ReceiptImportLineValue
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.picking_detail_line_item.view.*

class ReceiptMultipleImportLineAdapter(
    private val receiptImportLines: MutableList<ReceiptImportLineValue>,
    private val listener: OnMultipleImportLineListener
) : RecyclerView.Adapter<ReceiptMultipleImportLineAdapter.ReceiptHolder>() {
    interface OnMultipleImportLineListener {
        fun onMultipleImportLineSelected(data: ReceiptImportLineValue)
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
        fun bind(data: ReceiptImportLineValue, listener: OnMultipleImportLineListener) {
            with(itemView) {
                tv_line_no.text = data.lineNo.toString()
                tv_description.text = data.description
                tv_description_2.text = data.itemNo
                tv_outstanding.text =
                    "${data.alreadyScanned}/${data.quantity}/${data.outstandingQuantity}"
                setOnClickListener {
                    listener.onMultipleImportLineSelected(data)
                }
            }
        }
    }
}