package dynamia.com.barcodescanner.ui.transferstore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.databinding.TransferListItemBinding
import dynamia.com.core.data.entinty.PurchaseOrderHeader


class PurchaseHeaderListAdapter(
    private val transferDatas: MutableList<PurchaseOrderHeader>,
    private val listener: OnPurchaseHeaderClicklistener,
) : RecyclerView.Adapter<PurchaseHeaderListAdapter.PurchaseHeaderHolder>() {

    interface OnPurchaseHeaderClicklistener {
        fun clickListener(data: PurchaseOrderHeader)
    }

    fun updateData(data: MutableList<PurchaseOrderHeader>) {
        transferDatas.clear()
        transferDatas.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseHeaderHolder {
        return PurchaseHeaderHolder(TransferListItemBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun getItemCount(): Int {
        return transferDatas.size
    }

    override fun onBindViewHolder(holder: PurchaseHeaderHolder, position: Int) {
        transferDatas[position].let {
            holder.bind(it)
        }
    }

    inner class PurchaseHeaderHolder(val binding: TransferListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(value: PurchaseOrderHeader) {
            with(binding) {
                tvTransferNo.text = value.no
                tvTransferDate.text = value.documentDate
                tvTransferStatus.text = "Status: ${value.status}"
                root.setOnClickListener {
                    listener.clickListener(value)
                }
            }
        }
    }
}