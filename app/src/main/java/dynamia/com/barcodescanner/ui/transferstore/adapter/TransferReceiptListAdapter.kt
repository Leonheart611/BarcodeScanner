package dynamia.com.barcodescanner.ui.transferstore.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.entinty.TransferReceiptHeader
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.transfer_list_item.view.*


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
        return TransferReceiptHolder(parent.inflate(R.layout.transfer_list_item))
    }

    override fun getItemCount(): Int {
        return transferDatas.size
    }

    override fun onBindViewHolder(holder: TransferReceiptHolder, position: Int) {
        transferDatas[position].let {
            holder.bind(it, listener)
        }
    }

    class TransferReceiptHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: TransferReceiptHeader, listener: OnTransferReceiptListCLicklistener) {
            with(itemView) {
                tv_transfer_no.text = value.no
                tv_transfer_date.text = value.postingDate
                tv_transfer_status.text = "Status: ${value.status}"
                setOnClickListener {
                    listener.clickListener(value)
                }
            }
        }
    }
}