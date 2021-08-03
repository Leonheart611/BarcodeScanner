package dynamia.com.barcodescanner.ui.checkstock

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.databinding.ItemStockCheckingResultBinding
import dynamia.com.core.data.entinty.StockCheckingData

class StockCheckAdapter(val values: MutableList<StockCheckingData>) :
    RecyclerView.Adapter<StockCheckAdapter.StockCheckHolder>() {

    fun addData(data: MutableList<StockCheckingData>) {
        values.clear()
        values.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockCheckHolder {
        return StockCheckHolder(ItemStockCheckingResultBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: StockCheckHolder, position: Int) {
        values[position].let { holder.bind(it) }
    }

    override fun getItemCount(): Int = values.size

    class StockCheckHolder(private val binding: ItemStockCheckingResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StockCheckingData) {
            with(binding) {
                tvItemIdentifier.text = data.itemIdentifiers
                tvItemLocation.text = data.locationCode
                tvItemNo.text = data.itemNo
                tvItemDesc.text = data.description
                tvItemQty.text = "QTY: ${data.inventory}"
            }
        }
    }
}