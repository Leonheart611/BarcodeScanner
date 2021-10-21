package dynamia.com.barcodescanner.ui.stockopname.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.databinding.StockOpnameListItemBinding
import dynamia.com.core.data.entinty.StockOpnameData
import java.util.*

class StockOpnameAdapter(val listener: OnStockClicklistener) :
    ListAdapter<StockOpnameData, StockOpnameAdapter.StockOpnameHolder>(StockOpnameDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockOpnameHolder {
        return StockOpnameHolder(
            StockOpnameListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StockOpnameHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class StockOpnameHolder(private val binding: StockOpnameListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StockOpnameData) {
            with(binding) {
                tvItemCode.text = data.itemRefNo
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

    class StockOpnameDiffUtil : DiffUtil.ItemCallback<StockOpnameData>() {
        override fun areItemsTheSame(oldItem: StockOpnameData, newItem: StockOpnameData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: StockOpnameData,
            newItem: StockOpnameData
        ): Boolean {
            return oldItem == newItem
        }
    }
}