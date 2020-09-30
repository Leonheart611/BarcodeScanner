package dynamia.com.barcodescanner.ui.pickinglist.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.model.PickingListLineValue
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.picking_detail_line_item.view.*

class PickingMultipleLineAdapter(
    private val pickingListLineValues: MutableList<PickingListLineValue>,
    private val listener: OnMultipleLineSelected
) : RecyclerView.Adapter<PickingMultipleLineAdapter.PickingLineHolder>() {
    interface OnMultipleLineSelected {
        fun onMultiplelineSelected(data: PickingListLineValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickingLineHolder {
        return PickingLineHolder(parent.inflate(R.layout.picking_detail_line_item))
    }

    override fun getItemCount(): Int {
        return pickingListLineValues.size
    }

    override fun onBindViewHolder(holder: PickingLineHolder, position: Int) {
        pickingListLineValues[position].let {
            holder.bind(it, listener)
        }
    }

    class PickingLineHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(pickingListLineValue: PickingListLineValue, listener: OnMultipleLineSelected) {
            with(itemView) {
                tv_line_no.text = pickingListLineValue.lineNo.toString()
                tv_description.text = pickingListLineValue.description
                tv_description_2.text = pickingListLineValue.partNoOriginal
                tv_outstanding.text =
                    "${pickingListLineValue.alreadyPickup}/${pickingListLineValue.qtyToShip}/${pickingListLineValue.outstandingQuantity}"
                setOnClickListener {
                    listener.onMultiplelineSelected(pickingListLineValue)
                }
            }
        }
    }
}