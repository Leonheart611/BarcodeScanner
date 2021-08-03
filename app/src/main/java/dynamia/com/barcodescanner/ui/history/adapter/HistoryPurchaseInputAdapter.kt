package dynamia.com.barcodescanner.ui.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.databinding.ItemTransferInputHistoryBinding
import dynamia.com.barcodescanner.di.App.Companion.context
import dynamia.com.core.data.entinty.PurchaseInputData

class HistoryPurchaseInputAdapter(
    private val pickingListScanEntriesValues: MutableList<PurchaseInputData>,
    private val listener: OnPurchaseHistoryClicklistener,
) : RecyclerView.Adapter<HistoryPurchaseInputAdapter.HistoryInputHolder>() {

    fun updateData(data: MutableList<PurchaseInputData>) {
        pickingListScanEntriesValues.clear()
        pickingListScanEntriesValues.addAll(data)
        notifyDataSetChanged()
    }

    interface OnPurchaseHistoryClicklistener {
        fun historyCLicklistener(value: PurchaseInputData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryInputHolder {
        return HistoryInputHolder(ItemTransferInputHistoryBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return pickingListScanEntriesValues.size
    }

    override fun onBindViewHolder(holder: HistoryInputHolder, position: Int) {
        pickingListScanEntriesValues[position].let {
            holder.bind(it, listener)
        }
    }

    class HistoryInputHolder(private val binding: ItemTransferInputHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(value: PurchaseInputData, listener: OnPurchaseHistoryClicklistener) {
            with(binding) {
                tvTransferDocno.text = "Document No: ${value.documentNo}"
                tvTransferhistoryQty.text = "Qty: ${value.quantity}"
                tvTransferLineno.text = "Line No: ${value.lineNo}"
                tvTransferItemno.text = "Item No: ${value.itemNo}"
                value.id?.let { id ->
                    root.setOnClickListener {
                        listener.historyCLicklistener(value)
                    }
                }
                if (value.sync_status) {
                    tvTransferhistoryStatus.setText(R.string.posted_status_true)
                    tvTransferhistoryStatus.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.posted_true
                        )
                    )
                } else {
                    tvTransferhistoryStatus.setText(R.string.posted_status_false)
                    tvTransferhistoryStatus.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.posted_false
                        )
                    )
                }
            }
        }
    }
}