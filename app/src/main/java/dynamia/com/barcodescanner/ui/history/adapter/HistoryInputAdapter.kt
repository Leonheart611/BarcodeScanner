package dynamia.com.barcodescanner.ui.history.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.model.PickingListScanEntriesValue
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.item_history_input.view.*

class HistoryInputAdapter(private val pickingListScanEntriesValues: MutableList<PickingListScanEntriesValue>, private val listener: OnHistorySelected):RecyclerView.Adapter<HistoryInputAdapter.HistoryInputHolder>() {

    interface OnHistorySelected{
        fun onHistorySelectDelete(value:PickingListScanEntriesValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryInputHolder {
        return HistoryInputHolder(parent.inflate(R.layout.item_history_input))
    }

    override fun getItemCount(): Int {
        return pickingListScanEntriesValues.size
    }

    override fun onBindViewHolder(holder: HistoryInputHolder, position: Int) {
        pickingListScanEntriesValues[position].let {
            holder.bind(it,listener)
        }
    }

    class HistoryInputHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        fun bind(value:PickingListScanEntriesValue,listener:OnHistorySelected){
            with(itemView){
                tv_entryno_history.text = value.entryNo
                tv_partno_history.text = value.partNo
                tv_qty_history.text = value.quantity
                tv_sn_history.text = value.serialNumber
                value.id?.let {id->
                    setOnClickListener {
                        listener.onHistorySelectDelete(value)
                    }
                }

            }
        }
    }
}