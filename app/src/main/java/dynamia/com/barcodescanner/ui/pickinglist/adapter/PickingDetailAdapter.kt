package dynamia.com.barcodescanner.ui.pickinglist.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R


import dynamia.com.core.util.inflate
import dynamia.com.core.data.model.PickingListLineValue
import kotlinx.android.synthetic.main.picking_detail_line_item.view.*

class PickingDetailAdapter(private val pickingListLineValues:MutableList<PickingListLineValue>):RecyclerView.Adapter<PickingDetailAdapter.PickingDetailHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickingDetailHolder {
        return PickingDetailHolder(parent.inflate(R.layout.picking_detail_line_item))
    }

    override fun getItemCount(): Int {
       return pickingListLineValues.size
    }

    override fun onBindViewHolder(holder: PickingDetailHolder, position: Int) {
        pickingListLineValues[position].let {
            holder.bind(it)
        }
    }

    class PickingDetailHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        fun bind(pickingListLineValue:PickingListLineValue){
            with(itemView){
                tv_line_no.text = pickingListLineValue.lineNo.toString()
                tv_description.text = pickingListLineValue.description
                tv_description_2.text = pickingListLineValue.partNoOriginal
                tv_outstanding.text = " ${pickingListLineValue.qtyToShip}/${pickingListLineValue.outstandingQuantity}"
            }
        }
    }
}