package dynamia.com.barcodescanner.ui.peminjaman.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.model.DorPickingDetail
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.picking_detail_line_item.view.*
import java.util.*

class DorDetailListAdapter(private val onclicklistener: OnDorDetailClicklistener) :
	ListAdapter<DorPickingDetail, DorDetailListAdapter.DorDetailHolder>(DorDetailDiffUtil()),
	Filterable {
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DorDetailHolder {
		return DorDetailHolder(parent.inflate(R.layout.picking_detail_line_item))
	}
	
	override fun onBindViewHolder(holder: DorDetailHolder, position: Int) {
		getItem(position).let { holder.bind(it) }
	}
	
	override fun getFilter(): Filter {
		return object : Filter() {
			override fun performFiltering(p0: CharSequence?): FilterResults {
				val query = p0.toString().uppercase(Locale.ROOT)
				val filterResult = FilterResults()
				filterResult.values = if (query.isEmpty())
					currentList
				else {
					currentList.filter {
						it.description.uppercase(Locale.ROOT).contains(query) ||
								it.lineNo.toString().contains(query)
								|| it.partNo.uppercase(Locale.ROOT).contains(query)
					}
				}
				return filterResult
			}
			
			override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
				submitList(p1?.values as MutableList<DorPickingDetail>)
			}
		}
	}
	
	
	inner class DorDetailHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		fun bind(data: DorPickingDetail) {
			with(itemView) {
				tv_line_no.text = data.lineNo.toString()
				tv_description.text = data.description
				tv_description_2.text = data.partNo
				tv_outstanding.text =
					"${data.alreadyScanned}/${data.outstandingQuantity}"
				setOnClickListener {
					onclicklistener.onclicklistener(data)
				}
			}
		}
	}
	
	interface OnDorDetailClicklistener {
		fun onclicklistener(data: DorPickingDetail)
	}
	
	class DorDetailDiffUtil : DiffUtil.ItemCallback<DorPickingDetail>() {
		override fun areItemsTheSame(
			oldItem: DorPickingDetail,
			newItem: DorPickingDetail
		): Boolean {
			return oldItem.id == newItem.id
		}
		
		override fun areContentsTheSame(
			oldItem: DorPickingDetail,
			newItem: DorPickingDetail
		): Boolean {
			return oldItem == newItem
		}
		
	}
}