package dynamia.com.barcodescanner.ui.receipt.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.model.ReceiptImportLineValue
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.picking_detail_line_item.view.*
import java.util.*

class ReceiptImportLineAdapter(private var reciptImportLines: MutableList<ReceiptImportLineValue>) :
    RecyclerView.Adapter<ReceiptImportLineAdapter.ReceiptImportLineHolder>(), Filterable {

    val allData by lazy { reciptImportLines }

    fun update(data: MutableList<ReceiptImportLineValue>) {
        reciptImportLines.clear()
        reciptImportLines = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptImportLineHolder {
        return ReceiptImportLineHolder(parent.inflate(R.layout.picking_detail_line_item))
    }

    override fun getItemCount(): Int {
        return reciptImportLines.size
    }

    override fun onBindViewHolder(holder: ReceiptImportLineHolder, position: Int) {
        reciptImportLines[position].let {
            holder.bind(it)
        }
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
                reciptImportLines = p1?.values as MutableList<ReceiptImportLineValue>
                notifyDataSetChanged()
            }
        }
    }

    class ReceiptImportLineHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(pickingListLineValue: ReceiptImportLineValue) {
            with(itemView) {
                tv_description_2.text = pickingListLineValue.partNo
                tv_line_no.text = pickingListLineValue.lineNo.toString()
                tv_description.text = pickingListLineValue.description
                tv_outstanding.text =
                    "${pickingListLineValue.alreadyScanned}/${pickingListLineValue.quantity}/${pickingListLineValue.outstandingQuantity}"
            }
        }
    }
}