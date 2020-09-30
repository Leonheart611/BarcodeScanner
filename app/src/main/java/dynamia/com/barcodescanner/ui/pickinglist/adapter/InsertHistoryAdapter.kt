package dynamia.com.barcodescanner.ui.pickinglist.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.model.PickingListScanEntriesValue
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.history_input_layout.view.*


class InsertHistoryAdapter(var data: MutableList<PickingListScanEntriesValue>) :
    RecyclerView.Adapter<InsertHistoryAdapter.HolderHistoryItem>() {

    fun update(data: MutableList<PickingListScanEntriesValue>) {
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
        fun bind(data: PickingListScanEntriesValue) {
            with(itemView) {
                tv_sn_history.text = data.serialNo
            }
        }
    }
}