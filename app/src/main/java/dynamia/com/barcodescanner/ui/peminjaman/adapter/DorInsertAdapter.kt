package dynamia.com.barcodescanner.ui.peminjaman.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.model.DorPickingScanEntries
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.history_input_layout.view.*

class DorInsertAdapter :
	ListAdapter<DorPickingScanEntries, DorInsertAdapter.DorInsertHolder>(DorScanEntriesDiff()) {
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DorInsertHolder {
		return DorInsertHolder(parent.inflate(R.layout.history_input_layout))
	}
	
	override fun onBindViewHolder(holder: DorInsertHolder, position: Int) {
		getItem(position).let { holder.bind(it) }
	}
	
	class DorInsertHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		fun bind(data: DorPickingScanEntries) {
			with(itemView) {
				tv_sn_history.text = data.serialNo
			}
		}
	}
	
	class DorScanEntriesDiff : DiffUtil.ItemCallback<DorPickingScanEntries>() {
		override fun areItemsTheSame(
			oldItem: DorPickingScanEntries,
			newItem: DorPickingScanEntries
		): Boolean {
			return oldItem.id == newItem.id
		}
		
		override fun areContentsTheSame(
			oldItem: DorPickingScanEntries,
			newItem: DorPickingScanEntries
		): Boolean {
			return oldItem == newItem
		}
	}
}