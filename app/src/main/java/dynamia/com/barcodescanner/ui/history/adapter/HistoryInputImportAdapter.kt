package dynamia.com.barcodescanner.ui.history.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.model.ReceiptImportScanEntriesValue
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.item_history_input.view.*


class HistoryInputImportAdapter(
    private val pickingListScanEntriesValues: MutableList<ReceiptImportScanEntriesValue>,
    private val listener: OnImportClicklistener
) : RecyclerView.Adapter<HistoryInputImportAdapter.HistoryInputHolder>() {

    fun updateData(data: MutableList<ReceiptImportScanEntriesValue>) {
        pickingListScanEntriesValues.clear()
        pickingListScanEntriesValues.addAll(data)
        notifyDataSetChanged()
    }

    interface OnImportClicklistener {
        fun onLocalClicklistener(value: ReceiptImportScanEntriesValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryInputHolder {
        return HistoryInputHolder(parent.inflate(R.layout.item_history_input))
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
        fun bind(value: ReceiptImportScanEntriesValue, listener: OnImportClicklistener) {
            with(itemView) {
                tv_entryno_history.text = value.id.toString()
                tv_partno_history.text = value.partNo
                tv_qty_history.text = "1"
                tv_sn_history.text = value.serialNo
                value.id?.let { id ->
                    setOnClickListener {
                        listener.onLocalClicklistener(value)
                    }
                }
                if (value.sycn_status) {
                    tv_post_status.setText(R.string.posted_status_true)
                    tv_post_status.setTextColor(resources.getColor(R.color.posted_true))
                } else {
                    tv_post_status.setText(R.string.posted_status_false)
                    tv_post_status.setTextColor(resources.getColor(R.color.posted_false))
                }
                tv_scanby_history.text = value.employeeCode

            }
        }
    }
}