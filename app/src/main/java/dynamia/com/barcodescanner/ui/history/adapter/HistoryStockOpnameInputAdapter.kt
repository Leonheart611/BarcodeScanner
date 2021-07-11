package dynamia.com.barcodescanner.ui.history.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.entinty.StockOpnameInputData
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.item_transfer_input_history.view.*

class HistoryStockOpnameInputAdapter(
    private val entriesValues: MutableList<StockOpnameInputData>,
    private val listener: OnHistorySelected,
) : RecyclerView.Adapter<HistoryStockOpnameInputAdapter.HistoryInputHolder>() {

    fun updateData(data: MutableList<StockOpnameInputData>) {
        entriesValues.clear()
        entriesValues.addAll(data)
        notifyDataSetChanged()
    }

    interface OnHistorySelected {
        fun onStockOpnameCLicklistener(value: StockOpnameInputData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryInputHolder {
        return HistoryInputHolder(parent.inflate(R.layout.item_transfer_input_history))
    }

    override fun getItemCount(): Int {
        return entriesValues.size
    }

    override fun onBindViewHolder(holder: HistoryInputHolder, position: Int) {
        entriesValues[position].let {
            holder.bind(it, listener)
        }
    }

    class HistoryInputHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: StockOpnameInputData, listener: OnHistorySelected) {
            with(itemView) {
                tv_transfer_docno.text = "Document No: ${value.documentNo}"
                tv_transferhistory_qty.text = "Qty: ${value.quantity}"
                tv_transfer_lineno.text = "Line No: ${value.lineNo}"
                tv_transfer_itemno.text = "Item No: ${value.itemNo}"
                value.id?.let { id ->
                    setOnClickListener {
                        listener.onStockOpnameCLicklistener(value)
                    }
                }
                if (value.sync_status) {
                    tv_transferhistory_status.setText(R.string.posted_status_true)
                    tv_transferhistory_status.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.posted_true
                        )
                    )
                } else {
                    tv_transferhistory_status.setText(R.string.posted_status_false)
                    tv_transferhistory_status.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.posted_false
                        )
                    )
                }
            }
        }
    }
}