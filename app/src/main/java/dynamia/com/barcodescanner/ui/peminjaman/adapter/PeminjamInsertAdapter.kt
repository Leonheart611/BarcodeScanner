package dynamia.com.barcodescanner.ui.peminjaman.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.model.PeminjamScanEntries
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.history_input_layout.view.*

class PeminjamInsertAdapter :
	ListAdapter<PeminjamScanEntries, PeminjamInsertAdapter.PeminjamInsertHolder>(
		PeminjamScanEntriesDiff()
	) {
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeminjamInsertHolder {
		return PeminjamInsertHolder(parent.inflate(R.layout.history_input_layout))
	}
	
	override fun onBindViewHolder(holder: PeminjamInsertHolder, position: Int) {
		getItem(position).let { holder.bind(it) }
	}
	
	class PeminjamInsertHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		fun bind(data: PeminjamScanEntries) {
			with(itemView) {
				tv_sn_history.text = data.serialNo
			}
		}
	}
	
	class PeminjamScanEntriesDiff : DiffUtil.ItemCallback<PeminjamScanEntries>() {
		override fun areItemsTheSame(
			oldItem: PeminjamScanEntries,
			newItem: PeminjamScanEntries
		): Boolean {
			return oldItem.id == newItem.id
		}
		
		override fun areContentsTheSame(
			oldItem: PeminjamScanEntries,
			newItem: PeminjamScanEntries
		): Boolean {
			return oldItem == newItem
		}
	}
}