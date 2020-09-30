package dynamia.com.barcodescanner.ui.pickinglist.adapter


import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.model.PickingListLineValue
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.picking_detail_line_item.view.*
import java.util.*

class PickingDetailAdapter(private var pickingListLineValues: MutableList<PickingListLineValue>) :
    RecyclerView.Adapter<PickingDetailAdapter.PickingDetailHolder>(), Filterable {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickingDetailHolder {
        return PickingDetailHolder(parent.inflate(R.layout.picking_detail_line_item))
    }

    val allData by lazy { pickingListLineValues }
    override fun getItemCount(): Int {
        return pickingListLineValues.size
    }

    override fun onBindViewHolder(holder: PickingDetailHolder, position: Int) {
        pickingListLineValues[position].let {
            holder.bind(it)
        }
    }

    fun update(data: MutableList<PickingListLineValue>) {
        pickingListLineValues.clear()
        pickingListLineValues = data
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val query = p0.toString().toUpperCase(Locale.ROOT)
                val filterResult = FilterResults()
                filterResult.values = if (query.isEmpty())
                    allData
                else {
                    allData.filter {
                        it.description.toUpperCase(Locale.ROOT).contains(query) ||
                                it.lineNo.toString().contains(query)
                    }
                }
                return filterResult
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                pickingListLineValues = p1?.values as MutableList<PickingListLineValue>
                notifyDataSetChanged()
            }
        }
    }

    class PickingDetailHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(pickingListLineValue: PickingListLineValue) {
            with(itemView) {
                tv_line_no.text = pickingListLineValue.lineNo.toString()
                tv_description.text = pickingListLineValue.description
                tv_description_2.text = pickingListLineValue.partNoOriginal
                tv_outstanding.text =
                    "${pickingListLineValue.alreadyPickup}/${pickingListLineValue.qtyToShip}/${pickingListLineValue.outstandingQuantity}"
                tv_purchase_order.text = pickingListLineValue.purchOrderNo
            }
        }
    }


}