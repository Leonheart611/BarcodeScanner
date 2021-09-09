package dynamia.com.barcodescanner.ui.stockopname.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.databinding.StockOpnameListItemBinding
import dynamia.com.core.data.entinty.StockOpnameData
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
        return StockOpnameHolder(StockOpnameListItemBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
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

    inner class StockOpnameHolder(private val binding: StockOpnameListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StockOpnameData) {
            with(binding) {
                tvItemCode.text = data.itemIdentifier
                tvDescription.text = data.description
                tvQty.text =
                    "${data.alredyScanned}"
                tvStockOpnameBin.text = data.binCode
                root.setOnClickListener {
                    listener.onStockClicklistener(data)
                }
            }
        }
    }

    interface OnStockClicklistener {
        fun onStockClicklistener(data: StockOpnameData)
    }
}