package dynamia.com.barcodescanner.ui.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.databinding.ItemTransferInputHistoryBinding
import dynamia.com.core.data.entinty.StockOpnameInputData

class HistoryStockOpnameInputAdapter(private val listener: OnHistorySelected) :
    ListAdapter<StockOpnameInputData, HistoryStockOpnameInputAdapter.HistoryInputHolder>(
        StockOpnameDataDiffUtil()
    ) {

    interface OnHistorySelected {
        fun onStockOpnameCLicklistener(value: StockOpnameInputData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryInputHolder {
        return HistoryInputHolder(
            ItemTransferInputHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HistoryInputHolder, position: Int) {
        getItem(position).let {
            holder.bind(it, listener)
        }
    }

    class HistoryInputHolder(private val binding: ItemTransferInputHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(value: StockOpnameInputData, listener: OnHistorySelected) {
            with(binding) {
                tvTransferDocno.text = "Document No: ${value.documentNo}"
                tvTransferhistoryQty.text = "Qty: ${value.quantity}"
                tvTransferLineno.text = "Line No: ${value.lineNo}"
                tvTransferItemno.text = "Item No: ${value.itemNo}"
                tvBoxNo.text = "Box No: ${value.box}"
                value.id?.let { id ->
                    root.setOnClickListener {
                        listener.onStockOpnameCLicklistener(value)
                    }
                }
                if (value.sync_status) {
                    tvTransferhistoryStatus.setText(R.string.posted_status_true)
                    tvTransferhistoryStatus.setTextColor(
                        ContextCompat.getColor(
                            root.context,
                            R.color.posted_true
                        )
                    )
                } else {
                    tvTransferhistoryStatus.setText(R.string.posted_status_false)
                    tvTransferhistoryStatus.setTextColor(
                        ContextCompat.getColor(
                            root.context,
                            R.color.posted_false
                        )
                    )
                }
            }
        }
    }


    class StockOpnameDataDiffUtil : DiffUtil.ItemCallback<StockOpnameInputData>() {
        override fun areItemsTheSame(
            oldItem: StockOpnameInputData,
            newItem: StockOpnameInputData
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: StockOpnameInputData,
            newItem: StockOpnameInputData
        ): Boolean {
            return oldItem == newItem
        }
    }
}