package dynamia.com.barcodescanner.ui.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.databinding.ItemTransferInputHistoryBinding
import dynamia.com.core.data.entinty.InventoryInputData

class HistoryInventoryInputAdapter(private val clicklistener: HistoryInventoryClicklistener) :
    ListAdapter<InventoryInputData, HistoryInventoryInputAdapter.HistoryInventoryHolder>(
        InventoryInputDiffUtil()
    ) {

    interface HistoryInventoryClicklistener {
        fun onclicklistener(value: InventoryInputData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryInventoryHolder {
        return HistoryInventoryHolder(
            ItemTransferInputHistoryBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: HistoryInventoryHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class HistoryInventoryHolder(private val binding: ItemTransferInputHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(value: InventoryInputData) {
            with(binding) {
                tvTransferDocno.text = "Document No: ${value.documentNo}"
                tvTransferhistoryQty.text = "Qty: ${value.quantity}"
                tvTransferLineno.text = "Line No: ${value.lineNo}"
                tvTransferItemno.text = "Item No: ${value.itemNo}"
                root.setOnClickListener {
                    clicklistener.onclicklistener(value)
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

    class InventoryInputDiffUtil : DiffUtil.ItemCallback<InventoryInputData>() {
        override fun areItemsTheSame(
            oldItem: InventoryInputData,
            newItem: InventoryInputData
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: InventoryInputData,
            newItem: InventoryInputData
        ): Boolean {
            return oldItem == newItem
        }
    }
}