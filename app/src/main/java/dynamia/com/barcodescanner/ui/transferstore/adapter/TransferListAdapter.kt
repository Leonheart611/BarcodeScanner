package dynamia.com.barcodescanner.ui.transferstore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.databinding.TransferListItemBinding
import dynamia.com.core.data.entinty.TransferShipmentHeader

class TransferListAdapter(
    private val transferDatas: MutableList<TransferShipmentHeader>,
    private val listener: OnTransferListClicklistener,
) : RecyclerView.Adapter<TransferListAdapter.PickingListHolder>() {

    interface OnTransferListClicklistener {
        fun clickListener(data: TransferShipmentHeader)
    }

    fun updateData(data: MutableList<TransferShipmentHeader>) {
        transferDatas.clear()
        transferDatas.addAll(data)
        notifyDataSetChanged()
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

    override fun getItemCount(): Int {
        return transferDatas.size
    }

    override fun onBindViewHolder(holder: PickingListHolder, position: Int) {
        transferDatas[position].let {
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
}