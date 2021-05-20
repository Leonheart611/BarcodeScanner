package dynamia.com.barcodescanner.ui.transferstore.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.entinty.TransferShipmentHeader
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.transfer_list_item.view.*

class TransferListAdapter(
    private val transferDatas: MutableList<TransferShipmentHeader>,
    private val listener: OnTransferListClicklistener
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
        return PickingListHolder(parent.inflate(R.layout.transfer_list_item))
    }

    override fun getItemCount(): Int {
        return transferDatas.size
    }

    override fun onBindViewHolder(holder: PickingListHolder, position: Int) {
        transferDatas[position].let {
            holder.bind(it, listener)
        }
    }

    class PickingListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: TransferShipmentHeader, listener: OnTransferListClicklistener) {
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