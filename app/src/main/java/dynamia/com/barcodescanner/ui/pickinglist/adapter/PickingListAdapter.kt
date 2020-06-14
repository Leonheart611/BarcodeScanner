package dynamia.com.barcodescanner.ui.pickinglist.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.model.PickingListHeaderValue
import dynamia.com.core.util.inflate
import dynamia.com.core.util.toNormalDate
import kotlinx.android.synthetic.main.list_item_recieving.view.*

class PickingListAdapter(
    private val pickingListHeaderValues: MutableList<PickingListHeaderValue>,
    private val listener: OnPickinglistListener
) : RecyclerView.Adapter<PickingListAdapter.PickingListHolder>() {

    interface OnPickinglistListener {
        fun onPickingListClickListener(pickingListNo: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickingListHolder {
        return PickingListHolder(parent.inflate(R.layout.list_item_recieving))
    }

    override fun getItemCount(): Int {
        return pickingListHeaderValues.size
    }

    override fun onBindViewHolder(holder: PickingListHolder, position: Int) {
        pickingListHeaderValues[position].let {
            holder.bind(it, listener)
        }
    }

    class PickingListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(pickingListHeaderValue: PickingListHeaderValue, listener: OnPickinglistListener) {
            with(itemView) {
                tv_so_no.text = pickingListHeaderValue.sONo
                tv_order_date.text = pickingListHeaderValue.requestedDeliveryDate.toNormalDate()
                tv_picklist_no.text = pickingListHeaderValue.pickingListNo
                tv_sell_to_customer_name.text = pickingListHeaderValue.sellToCustomerName
                setOnClickListener {
                    listener.onPickingListClickListener(pickingListHeaderValue.pickingListNo)
                }
            }
        }
    }
}