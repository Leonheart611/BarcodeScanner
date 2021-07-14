package dynamia.com.barcodescanner.ui.stockopname.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.entinty.PurchaseOrderLine
import dynamia.com.core.data.entinty.StockOpnameData
import dynamia.com.core.data.model.StockCount
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.picking_detail_line_item.view.*
import java.util.*

class StockOpnameAdapter(
    private var values: MutableList<StockOpnameData>,
    val listener: OnStockClicklistener,
) :
    RecyclerView.Adapter<StockOpnameAdapter.StockOpnameHolder>(), Filterable {
    val allData by lazy { values }

    fun updateData(values: MutableList<StockOpnameData>) {
        this.values.clear()
        this.values.addAll(values)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockOpnameHolder {
        return StockOpnameHolder(parent.inflate(R.layout.picking_detail_line_item))
    }

    override fun onBindViewHolder(holder: StockOpnameHolder, position: Int) {
        values[position].let { holder.bind(it) }
    }

    override fun getItemCount(): Int = values.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val query = p0.toString().uppercase(Locale.ROOT)
                val filterResult = FilterResults()
                filterResult.values = if (query.isEmpty())
                    allData
                else {
                    allData.filter {
                        it.journalTemplateName.uppercase(Locale.ROOT).contains(query)
                    }
                }
                return filterResult
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                values = p1?.values as MutableList<StockOpnameData>
                notifyDataSetChanged()
            }
        }
    }

    inner class StockOpnameHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: StockOpnameData) {
            with(itemView) {
                tv_item_code.text = data.itemIdentifier
                tv_description.text = data.description
                tv_qty.text =
                    "${data.alredyScanned}"
                setOnClickListener {
                    listener.onStockClicklistener(data)
                }
            }
        }
    }

    interface OnStockClicklistener {
        fun onStockClicklistener(data: StockOpnameData)
    }
}