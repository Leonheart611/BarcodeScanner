package dynamia.com.barcodescanner.ui.transferstore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.databinding.TransferListItemBinding
import dynamia.com.core.data.entinty.InventoryPickHeader

class InventoryHeaderListAdapter(val clicklistener: InventoryHeaderClicklistener) :
    ListAdapter<InventoryPickHeader, InventoryHeaderListAdapter.InventoryHeaderHolder>(
        InventoryHeaderDiffUtil()
    ) {
    interface InventoryHeaderClicklistener {
        fun onclicklistener(data: InventoryPickHeader)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryHeaderHolder {
        return InventoryHeaderHolder(
            TransferListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: InventoryHeaderHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class InventoryHeaderHolder(val binding: TransferListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(value: InventoryPickHeader) {
            with(binding) {
                tvTransferNo.text = value.no
                tvTransferFrom.text = value.sourceNo
                tvTransferTo.text = value.transferToCode
                tvTransferDate.text = value.postingDate
                root.setOnClickListener {
                    clicklistener.onclicklistener(value)
                }
            }
        }
    }

    class InventoryHeaderDiffUtil : DiffUtil.ItemCallback<InventoryPickHeader>() {

        override fun areItemsTheSame(
            oldItem: InventoryPickHeader,
            newItem: InventoryPickHeader
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: InventoryPickHeader,
            newItem: InventoryPickHeader
        ): Boolean {
            return oldItem == newItem
        }
    }
}