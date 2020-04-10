package dynamia.com.barcodescanner.ui.receipt.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.model.ReceiptImportLineValue
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.picking_detail_line_item.view.*

class ReceiptImportLineAdapter(private val pickingListLineValues: MutableList<ReceiptImportLineValue>) :
    RecyclerView.Adapter<ReceiptImportLineAdapter.ReceiptImportLineHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptImportLineHolder {
        return ReceiptImportLineHolder(parent.inflate(R.layout.picking_detail_line_item))
    }

    override fun getItemCount(): Int {
        return pickingListLineValues.size
    }

    override fun onBindViewHolder(holder: ReceiptImportLineHolder, position: Int) {
        pickingListLineValues[position].let {
            holder.bind(it)
        }
    }

    class ReceiptImportLineHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(pickingListLineValue: ReceiptImportLineValue) {
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