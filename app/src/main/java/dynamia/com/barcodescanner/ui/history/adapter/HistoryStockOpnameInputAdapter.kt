package dynamia.com.barcodescanner.ui.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.databinding.ItemTransferInputHistoryBinding
import dynamia.com.barcodescanner.di.App
import dynamia.com.core.data.entinty.StockOpnameInputData

class HistoryStockOpnameInputAdapter(
    private val entriesValues: MutableList<StockOpnameInputData>,
    private val listener: OnHistorySelected,
) : RecyclerView.Adapter<HistoryStockOpnameInputAdapter.HistoryInputHolder>() {

    fun updateData(data: MutableList<StockOpnameInputData>) {
        entriesValues.clear()
        entriesValues.addAll(data)
        notifyDataSetChanged()
    }

    interface OnHistorySelected {
        fun onStockOpnameCLicklistener(value: StockOpnameInputData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryInputHolder {
        return HistoryInputHolder(ItemTransferInputHistoryBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return entriesValues.size
    }

    override fun onBindViewHolder(holder: HistoryInputHolder, position: Int) {
        entriesValues[position].let {
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
                value.id?.let { id ->
                    root.setOnClickListener {
                        listener.onStockOpnameCLicklistener(value)
                    }
                }
                if (value.sync_status) {
                    tvTransferhistoryStatus.setText(R.string.posted_status_true)
                    tvTransferhistoryStatus.setTextColor(
                        ContextCompat.getColor(
                            App.context,
                            R.color.posted_true
                        )
                    )
                } else {
                    tvTransferhistoryStatus.setText(R.string.posted_status_false)
                    tvTransferhistoryStatus.setTextColor(
                        ContextCompat.getColor(
                            App.context,
                            R.color.posted_false
                        )
                    )
                }
            }
        }
    }
}