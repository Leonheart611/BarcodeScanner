package dynamia.com.barcodescanner.ui.transferstore.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.entinty.PurchaseOrderHeader
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.transfer_list_item.view.*


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
        return PurchaseHeaderHolder(parent.inflate(R.layout.transfer_list_item))
    }

    override fun getItemCount(): Int {
        return transferDatas.size
    }

    override fun onBindViewHolder(holder: PurchaseHeaderHolder, position: Int) {
        transferDatas[position].let {
            holder.bind(it)
        }
    }

    inner class PurchaseHeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: PurchaseOrderHeader) {
            with(itemView) {
                tv_transfer_no.text = value.no
                tv_transfer_date.text = value.documentDate
                tv_transfer_status.text = "Status: ${value.status}"
                setOnClickListener {
                    listener.clickListener(value)
                }
            }
        }
    }
}