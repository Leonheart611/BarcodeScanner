package dynamia.com.barcodescanner.ui.peminjaman.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.model.PeminjamanDetail
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.picking_detail_line_item.view.*
import java.util.*

class PeminjamanDetailListAdapter(private val onClicklistener: PeminjamOnClicklistener) :
	ListAdapter<PeminjamanDetail, PeminjamanDetailListAdapter.PeminjamDetailHolder>(
		PeminjamanDetailDiffUtil()
	), Filterable {
	
	interface PeminjamOnClicklistener {
		fun onclicklistener(data: PeminjamanDetail)
	}
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeminjamDetailHolder {
		return PeminjamDetailHolder(parent.inflate(R.layout.picking_detail_line_item))
	}
	
	override fun onBindViewHolder(holder: PeminjamDetailHolder, position: Int) {
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
				submitList(p1?.values as MutableList<PeminjamanDetail>)
			}
		}
	}
	
	inner class PeminjamDetailHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		fun bind(data: PeminjamanDetail) {
			with(itemView) {
				tv_line_no.text = data.lineNo.toString()
				tv_description.text = data.description
				tv_description_2.text = data.partNo
				tv_outstanding.text =
					"${data.alreadyScanned}/${data.outstandingQuantity}"
				setOnClickListener {
					onClicklistener.onclicklistener(data)
				}
			}
		}
	}
	
	class PeminjamanDetailDiffUtil : DiffUtil.ItemCallback<PeminjamanDetail>() {
		override fun areItemsTheSame(
			oldItem: PeminjamanDetail,
			newItem: PeminjamanDetail
		): Boolean {
			return oldItem.id == newItem.id
		}
		
		override fun areContentsTheSame(
			oldItem: PeminjamanDetail,
			newItem: PeminjamanDetail
		): Boolean {
			return oldItem == newItem
		}
	}
	
	
}