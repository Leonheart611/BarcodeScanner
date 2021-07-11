package dynamia.com.barcodescanner.ui.stockopname.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.entinty.StockOpnameData
import dynamia.com.core.data.model.StockCount
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.picking_detail_line_item.view.*

class StockOpnameAdapter(
    private val values: MutableList<StockOpnameData>,
    val listener: OnStockClicklistener,
) :
    RecyclerView.Adapter<StockOpnameAdapter.StockOpnameHolder>() {

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

    inner class StockOpnameHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: StockOpnameData) {
            with(itemView) {
                tv_item_code.text = data.barcode
                tv_description.text = data.itemName
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