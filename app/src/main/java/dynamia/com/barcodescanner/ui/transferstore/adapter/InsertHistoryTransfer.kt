package dynamia.com.barcodescanner.ui.transferstore.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.entinty.TransferInputData
import dynamia.com.core.util.inflate


class InsertHistoryTransfer(var data: MutableList<TransferInputData>) :
    RecyclerView.Adapter<InsertHistoryTransfer.HolderHistoryItem>() {

    fun update(data: MutableList<TransferInputData>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderHistoryItem {
        return HolderHistoryItem(parent.inflate(R.layout.history_input_layout))
    }

    override fun onBindViewHolder(holder: HolderHistoryItem, position: Int) {
        data[position].let {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class HolderHistoryItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: TransferInputData) {
            with(itemView) {
                //tv_sn_history.text = data.serialNo
            }
        }
    }
}