package dynamia.com.barcodescanner.ui.transferstore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.databinding.TransferListItemBinding
import dynamia.com.core.data.entinty.TransferShipmentHeader

class TransferListAdapter(
    private val listener: OnTransferListClicklistener,
) : ListAdapter<TransferShipmentHeader, TransferListAdapter.PickingListHolder>(
    TransferShipmentHeaderDiffUtil()
) {

    interface OnTransferListClicklistener {
        fun clickListener(data: TransferShipmentHeader)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickingListHolder {
        return PickingListHolder(
            TransferListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: PickingListHolder, position: Int) {
        getItem(position).let {
            holder.bind(it, listener)
        }
    }

    class PickingListHolder(val binding: TransferListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(value: TransferShipmentHeader, listener: OnTransferListClicklistener) {
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

    class TransferShipmentHeaderDiffUtil : DiffUtil.ItemCallback<TransferShipmentHeader>() {
        override fun areItemsTheSame(
            oldItem: TransferShipmentHeader,
            newItem: TransferShipmentHeader
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: TransferShipmentHeader,
            newItem: TransferShipmentHeader
        ): Boolean {
            return oldItem == newItem
        }
    }
}