package dynamia.com.barcodescanner.ui.checkstock

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.entinty.StockCheckingData
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.item_stock_checking_result.view.*

class StockCheckAdapter(val values: MutableList<StockCheckingData>) :
    RecyclerView.Adapter<StockCheckAdapter.StockCheckHolder>() {

    fun addData(data: MutableList<StockCheckingData>) {
        values.clear()
        values.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockCheckHolder {
        return StockCheckHolder(parent.inflate(R.layout.item_stock_checking_result))
    }

    override fun onBindViewHolder(holder: StockCheckHolder, position: Int) {
        values[position].let { holder.bind(it) }
    }

    override fun getItemCount(): Int = values.size

    class StockCheckHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: StockCheckingData) {
            with(itemView) {
                tv_item_identifier.text = data.itemIdentifiers
                tv_item_location.text = data.locationCode
                tv_item_no.text = data.itemNo
                tv_item_desc.text = data.description
                tv_item_qty.text = "QTY: ${data.inventory}"
            }
        }
    }
}