package dynamia.com.barcodescanner.ui.peminjaman.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.model.DorPickingHeader
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.list_item_recieving.view.*

class DorListAdapter(private val clicklistener: OnDorListClicklistener) :
	ListAdapter<DorPickingHeader, DorListAdapter.DorHeaderHolder>(DorListHeaderDiff()) {
	
	interface OnDorListClicklistener {
		fun onclicklistener(data: DorPickingHeader)
	}
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DorHeaderHolder {
		return DorHeaderHolder(parent.inflate(R.layout.list_item_recieving))
	}
	
	override fun onBindViewHolder(holder: DorHeaderHolder, position: Int) {
		getItem(position).let { holder.bind(it) }
	}
	
	inner class DorHeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		fun bind(data: DorPickingHeader) {
			with(itemView) {
				tv_so_no.text = data.no
				tv_order_date.text = data.salesRentDocNo
				tv_picklist_no.text = data.transferFromName
				tv_sell_to_customer_name.text = data.transferToName
				setOnClickListener {
					clicklistener.onclicklistener(data)
				}
			}
		}
	}
	
	
	class DorListHeaderDiff : DiffUtil.ItemCallback<DorPickingHeader>() {
		override fun areItemsTheSame(
			oldItem: DorPickingHeader,
			newItem: DorPickingHeader
		): Boolean {
			return oldItem.id == newItem.id
		}
		
		override fun areContentsTheSame(
			oldItem: DorPickingHeader,
			newItem: DorPickingHeader
		): Boolean {
			return oldItem == newItem
		}
	}
}