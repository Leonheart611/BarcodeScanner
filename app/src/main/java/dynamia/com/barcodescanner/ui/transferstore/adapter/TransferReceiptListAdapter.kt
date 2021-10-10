package dynamia.com.barcodescanner.ui.transferstore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.databinding.TransferListItemBinding
import dynamia.com.core.data.entinty.TransferReceiptHeader


class TransferReceiptListAdapter(
    private val listener: OnTransferReceiptListCLicklistener,
) : ListAdapter<TransferReceiptHeader, TransferReceiptListAdapter.TransferReceiptHolder>(
    TransferReceiptDiffUtil()
) {

    interface OnTransferReceiptListCLicklistener {
        fun clickListener(data: TransferReceiptHeader)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransferReceiptHolder {
        return TransferReceiptHolder(
            TransferListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: TransferReceiptHolder, position: Int) {
        getItem(position).let {
            holder.bind(it, listener)
        }
    }

    class TransferReceiptHolder(val binding: TransferListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(value: TransferReceiptHeader, listener: OnTransferReceiptListCLicklistener) {
            with(binding) {
                tvTransferNo.text = value.no
                tvTransferFrom.text = value.transferFromCode
                tvTransferTo.text = value.transferToCode
                tvTransferDate.text = value.postingDate
                root.setOnClickListener {
                    listener.clickListener(value)
                }
            }
        }
    }

    class TransferReceiptDiffUtil : DiffUtil.ItemCallback<TransferReceiptHeader>() {
        override fun areItemsTheSame(
            oldItem: TransferReceiptHeader,
            newItem: TransferReceiptHeader
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: TransferReceiptHeader,
            newItem: TransferReceiptHeader
        ): Boolean {
            return oldItem == newItem
        }
    }

}