package dynamia.com.barcodescanner.ui.peminjaman.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.model.PeminjamanHeader
import dynamia.com.core.util.inflate
import dynamia.com.core.util.toNormalDate
import kotlinx.android.synthetic.main.list_item_recieving.view.*

class PeminjamanListAdapter(private val clicklistener: OnPeminjamanClicklistener) :
	ListAdapter<PeminjamanHeader, PeminjamanListAdapter.PeminjamanViewHolder>(
		PeminjamanHeaderDiffUtil()
	) {
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeminjamanViewHolder {
		return PeminjamanViewHolder(parent.inflate(R.layout.list_item_recieving))
	}
	
	override fun onBindViewHolder(holder: PeminjamanViewHolder, position: Int) {
		getItem(position).let { holder.bind(it) }
	}
	
	inner class PeminjamanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		fun bind(data: PeminjamanHeader) {
			with(itemView) {
				tv_so_no.text = data.no
				tv_order_date.text = data.postingDate.toNormalDate()
				tv_picklist_no.text = data.transferFromName
				tv_sell_to_customer_name.text = data.transferToName
				setOnClickListener {
					clicklistener.onclicklister(data)
				}
			}
		}
	}
	
	interface OnPeminjamanClicklistener {
		fun onclicklister(data: PeminjamanHeader)
	}
	
	class PeminjamanHeaderDiffUtil : DiffUtil.ItemCallback<PeminjamanHeader>() {
		override fun areItemsTheSame(
			oldItem: PeminjamanHeader,
			newItem: PeminjamanHeader
		): Boolean {
			return oldItem.id == newItem.id
		}
		
		override fun areContentsTheSame(
			oldItem: PeminjamanHeader,
			newItem: PeminjamanHeader
		): Boolean {
			return oldItem == newItem
		}
	}
}