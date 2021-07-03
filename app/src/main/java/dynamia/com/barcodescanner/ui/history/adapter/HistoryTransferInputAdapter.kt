package dynamia.com.barcodescanner.ui.history.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.entinty.TransferInputData
import dynamia.com.core.data.model.PickingListScanEntriesValue
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.item_transfer_input_history.view.*

class HistoryTransferInputAdapter(
    private val pickingListScanEntriesValues: MutableList<TransferInputData>,
    private val listener: OnHistorySelected
) : RecyclerView.Adapter<HistoryTransferInputAdapter.HistoryInputHolder>() {

    fun updateData(data: MutableList<TransferInputData>) {
        pickingListScanEntriesValues.clear()
        pickingListScanEntriesValues.addAll(data)
        notifyDataSetChanged()
    }

    interface OnHistorySelected {
        fun onHistorySelectDelete(value: TransferInputData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryInputHolder {
        return HistoryInputHolder(parent.inflate(R.layout.item_transfer_input_history))
    }

    override fun getItemCount(): Int {
        return pickingListScanEntriesValues.size
    }

    override fun onBindViewHolder(holder: HistoryInputHolder, position: Int) {
        pickingListScanEntriesValues[position].let {
            holder.bind(it, listener)
        }
    }

    class HistoryInputHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: TransferInputData, listener: OnHistorySelected) {
            with(itemView) {
                tv_transfer_docno.text = "Document No: ${value.documentNo}"
                tv_transferhistory_qty.text = "Qty: ${value.quantity}"
                tv_transfer_lineno.text = "Line No: ${value.lineNo}"
                tv_transfer_itemno.text = "Item No: ${value.itemNo}"
                value.id?.let { id ->
                    setOnClickListener {
                        listener.onHistorySelectDelete(value)
                    }
                }
                if (value.sycn_status) {
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