package dynamia.com.barcodescanner.ui.transferstore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.databinding.PurchaseOrderListBinding
import dynamia.com.barcodescanner.databinding.TransferListItemBinding
import dynamia.com.core.data.entinty.PurchaseOrderHeader


class PurchaseHeaderListAdapter(
    private val listener: OnPurchaseHeaderClicklistener,
) : ListAdapter<PurchaseOrderHeader, PurchaseHeaderListAdapter.PurchaseHeaderHolder>(
    PurchaseHeaderDiffUtil()
) {

    interface OnPurchaseHeaderClicklistener {
        fun clickListener(data: PurchaseOrderHeader)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseHeaderHolder {
        return PurchaseHeaderHolder(
            PurchaseOrderListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PurchaseHeaderHolder, position: Int) {
        getItem(position).let {
            holder.bind(it)
        }
    }

    inner class PurchaseHeaderHolder(val binding: PurchaseOrderListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(value: PurchaseOrderHeader) {
            with(binding) {
                tvPurchaseNo.text = value.no
                tvPurchaseDate.text = value.documentDate
                tvPurchaseFrom.text = "Status: ${value.status}"
                tvVendorInvoice.text = value.vendorInvoiceNo
                root.setOnClickListener {
                    listener.clickListener(value)
                }
            }
        }
    }

    class PurchaseHeaderDiffUtil : DiffUtil.ItemCallback<PurchaseOrderHeader>() {
        override fun areItemsTheSame(
            oldItem: PurchaseOrderHeader,
            newItem: PurchaseOrderHeader
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: PurchaseOrderHeader,
            newItem: PurchaseOrderHeader
        ): Boolean {
            return oldItem == newItem
        }

    }
}