package dynamia.com.barcodescanner.ui.stockcounting.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.model.StockCount
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.stock_count_item.view.*

class StockCountAdapter (var stockCounts :MutableList<StockCount>):RecyclerView.Adapter<StockCountAdapter.StockCountHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockCountHolder {
        return StockCountHolder(parent.inflate(R.layout.stock_count_item))
    }

    override fun getItemCount(): Int {
        return stockCounts.size
    }

    override fun onBindViewHolder(holder: StockCountHolder, position: Int) {
        stockCounts[position].let {
            holder.bind(it)
        }
    }

    class StockCountHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        fun bind(data:StockCount){
            with(itemView){
                tv_stock_part_no.text = data.Part_No
                tv_stock_item_no.text = data.Item_No
                tv_stock_serial_no.text = data.Serial_No
                if (data.sycn_status){
                    tv_stock_sycn_status.setText(R.string.posted_status_true)
                    tv_stock_sycn_status.setTextColor(resources.getColor(R.color.posted_true))
                }else{
                    tv_stock_sycn_status.setText(R.string.posted_status_false)
                    tv_stock_sycn_status.setTextColor(resources.getColor(R.color.posted_false))
                }

            }
        }
    }
}