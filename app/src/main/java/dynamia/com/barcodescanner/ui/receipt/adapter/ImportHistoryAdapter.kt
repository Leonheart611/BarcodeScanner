package dynamia.com.barcodescanner.ui.receipt.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.model.ReceiptImportScanEntriesValue
import dynamia.com.core.util.inflate

class ImportHistoryAdapter(val data: MutableList<ReceiptImportScanEntriesValue>) :
    RecyclerView.Adapter<ImportHistoryAdapter.ImportHistoryHolder>() {

    fun update(data: MutableList<ReceiptImportScanEntriesValue>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImportHistoryHolder {
        return ImportHistoryHolder(parent.inflate(R.layout.item_history_input))
    }

    override fun onBindViewHolder(holder: ImportHistoryHolder, position: Int) {
        data[position].let {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ImportHistoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: ReceiptImportScanEntriesValue) {

        }
    }

}