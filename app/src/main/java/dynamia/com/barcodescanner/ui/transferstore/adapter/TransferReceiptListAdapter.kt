package dynamia.com.barcodescanner.ui.transferstore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.databinding.TransferListItemBinding
import dynamia.com.core.data.entinty.TransferReceiptHeader


class TransferReceiptListAdapter(
    private val transferDatas: MutableList<TransferReceiptHeader>,
    private val listener: OnTransferReceiptListCLicklistener,
) : RecyclerView.Adapter<TransferReceiptListAdapter.TransferReceiptHolder>() {

    interface OnTransferReceiptListCLicklistener {
        fun clickListener(data: TransferReceiptHeader)
    }

    fun updateData(data: MutableList<TransferReceiptHeader>) {
        transferDatas.clear()
        transferDatas.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransferReceiptHolder {
        return TransferReceiptHolder(TransferListItemBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun getItemCount(): Int {
        return transferDatas.size
    }

    override fun onBindViewHolder(holder: TransferReceiptHolder, position: Int) {
        transferDatas[position].let {
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
}