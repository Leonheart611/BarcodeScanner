package dynamia.com.barcodescanner.ui.transferstore.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.databinding.PickingDetailLineItemBinding
import dynamia.com.core.data.entinty.PurchaseOrderLine

class PurchaseDetailLineAdapter :
    ListAdapter<PurchaseOrderLine, PurchaseDetailLineAdapter.PurchaseLineHolder>(
        PurchaseLineDiffCallback()
    ) {

    var listener: OnPurchaseLineClicklistener? = null

    fun setOnClickListener(clicklistener: OnPurchaseLineClicklistener) {
        listener = clicklistener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseLineHolder {
        return PurchaseLineHolder(
            PickingDetailLineItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PurchaseLineHolder, position: Int) {
        getItem(position).let {
            holder.bindShipment(it)
        }
    }

    inner class PurchaseLineHolder(val binding: PickingDetailLineItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindShipment(transferShipmentLine: PurchaseOrderLine) {
            with(binding) {
                tvItemCode.text = transferShipmentLine.itemRefNo
                tvDescription.text = transferShipmentLine.description
                tvQty.text =
                    "${transferShipmentLine.alredyScanned} / ${transferShipmentLine.quantity}"
                root.setOnClickListener {
                    listener?.onclicklistener(transferShipmentLine)
                }
            }
        }
    }


    interface OnPurchaseLineClicklistener {
        fun onclicklistener(value: PurchaseOrderLine)
    }

    class PurchaseLineDiffCallback : DiffUtil.ItemCallback<PurchaseOrderLine>() {
        override fun areItemsTheSame(
            oldItem: PurchaseOrderLine,
            newItem: PurchaseOrderLine
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: PurchaseOrderLine,
            newItem: PurchaseOrderLine
        ): Boolean {
            return oldItem == newItem
        }
    }
}