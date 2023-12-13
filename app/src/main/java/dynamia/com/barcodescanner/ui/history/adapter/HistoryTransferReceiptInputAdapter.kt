package dynamia.com.barcodescanner.ui.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.databinding.ItemTransferInputHistoryBinding
import dynamia.com.core.data.entinty.TransferReceiptInput

class HistoryTransferReceiptInputAdapter(private val listener: OnHistorySelected) :
    ListAdapter<TransferReceiptInput, HistoryTransferReceiptInputAdapter.HistoryInputHolder>(
        TransferReceiptInputDataDiffUtil()
    ) {

    interface OnHistorySelected {
        fun receiptHistoryCLicklistener(value: TransferReceiptInput)
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
        getItem(position).let { holder.bind(it, listener) }
    }

    class HistoryInputHolder(val binding: ItemTransferInputHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(value: TransferReceiptInput, listener: OnHistorySelected) {
            with(binding) {
                tvTransferDocno.text = "Document No: ${value.documentNo}"
                tvTransferhistoryQty.text = "Qty: ${value.quantity}"
                tvTransferLineno.text = "Line No: ${value.lineNo}"
                tvTransferItemno.text = "Item No: ${value.itemNo.ifEmpty { value.itemIdentifier }}"
                tvBoxNo.text = "Box No: ${value.box}"
                value.id?.let { id ->
                    root.setOnClickListener {
                        listener.receiptHistoryCLicklistener(value)
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

    class TransferReceiptInputDataDiffUtil : DiffUtil.ItemCallback<TransferReceiptInput>() {
        override fun areItemsTheSame(
            oldItem: TransferReceiptInput,
            newItem: TransferReceiptInput
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: TransferReceiptInput,
            newItem: TransferReceiptInput
        ): Boolean {
            return oldItem == newItem
        }
    }

}