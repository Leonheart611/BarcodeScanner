package dynamia.com.barcodescanner.ui.transferstore.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.model.ReceiptLocalScanEntriesValue
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.history_input_layout.view.*

class LocalHistoryAdapter(val data: MutableList<ReceiptLocalScanEntriesValue>) :
    RecyclerView.Adapter<LocalHistoryAdapter.LocalHistoryHolder>() {

    fun update(data: MutableList<ReceiptLocalScanEntriesValue>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalHistoryHolder {
        return LocalHistoryHolder(parent.inflate(R.layout.history_input_layout))
    }

    override fun onBindViewHolder(holder: LocalHistoryHolder, position: Int) {
        data[position].let {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class LocalHistoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: ReceiptLocalScanEntriesValue) {
            with(itemView) { tv_sn_history.text = data.serialNo }
        }
    }
}