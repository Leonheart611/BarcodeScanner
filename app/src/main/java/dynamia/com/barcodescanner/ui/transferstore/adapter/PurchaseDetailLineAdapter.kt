package dynamia.com.barcodescanner.ui.transferstore.adapter


import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.entinty.PurchaseOrderLine
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.picking_detail_line_item.view.*
import java.util.*

class PurchaseDetailLineAdapter(
    private var values: MutableList<PurchaseOrderLine>,
) : RecyclerView.Adapter<PurchaseDetailLineAdapter.PurchaseLineHolder>(), Filterable {

    var listener: OnPurchaseLineClicklistener? = null

    fun setOnClickListener(clicklistener: OnPurchaseLineClicklistener) {
        listener = clicklistener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseLineHolder {
        return PurchaseLineHolder(parent.inflate(R.layout.picking_detail_line_item))
    }

    val allData by lazy { values }
    override fun getItemCount(): Int {
        return values.size
    }

    override fun onBindViewHolder(holder: PurchaseLineHolder, position: Int) {
        values[position].let {
            holder.bindShipment(it)
        }
    }

    fun update(data: MutableList<PurchaseOrderLine>) {
        values.clear()
        values = data
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val query = p0.toString().uppercase(Locale.ROOT)
                val filterResult = FilterResults()
                filterResult.values = if (query.isEmpty())
                    allData
                else {
                    allData.filter {
                        it.description.uppercase(Locale.ROOT).contains(query) ||
                                it.lineNo.toString().contains(query)
                    }
                }
                return filterResult
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                values = p1?.values as MutableList<PurchaseOrderLine>
                notifyDataSetChanged()
            }
        }
    }

    inner class PurchaseLineHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindShipment(transferShipmentLine: PurchaseOrderLine) {
            with(itemView) {
                tv_item_code.text = transferShipmentLine.itemIdentifier
                tv_description.text = transferShipmentLine.description
                tv_qty.text =
                    "${transferShipmentLine.alredyScanned} / ${transferShipmentLine.quantity}"
                setOnClickListener {
                    listener?.onclicklistener(transferShipmentLine)
                }
            }
        }
    }


    interface OnPurchaseLineClicklistener {
        fun onclicklistener(value: PurchaseOrderLine)
    }

}