package dynamia.com.barcodescanner.ui.transferstore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.databinding.PickingDetailLineItemBinding
import dynamia.com.core.data.entinty.InventoryPickLine

class InventoryLineListAdapter(val listener: OnclickInventoryLineAdapter) :
    ListAdapter<InventoryPickLine, InventoryLineListAdapter.InventoryLineHolder>(
        InventoryLineDiffUtil()
    ) {

    interface OnclickInventoryLineAdapter {
        fun onclicklistener(data: InventoryPickLine)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryLineHolder {
        return InventoryLineHolder(
            PickingDetailLineItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: InventoryLineHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class InventoryLineHolder(val binding: PickingDetailLineItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: InventoryPickLine) {
            with(binding) {
                tvItemCode.text = data.itemRefNo
                tvDescription.text = data.description
                tvQty.text =
                    "${data.alredyScanned} / ${data.quantity}"
                tvBinCode.text = data.binCode
                root.setOnClickListener {
                    listener.onclicklistener(data)
                }
            }
        }
    }

    class InventoryLineDiffUtil : DiffUtil.ItemCallback<InventoryPickLine>() {
        override fun areItemsTheSame(
            oldItem: InventoryPickLine,
            newItem: InventoryPickLine
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: InventoryPickLine,
            newItem: InventoryPickLine
        ): Boolean {
            return oldItem == newItem
        }
    }

}