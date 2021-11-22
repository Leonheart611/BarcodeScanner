package dynamia.com.barcodescanner.ui.history.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.ui.peminjaman.adapter.PeminjamInsertAdapter
import dynamia.com.core.data.model.PeminjamScanEntries
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.item_history_input.view.*

class HistoryInputPeminjamAdapter(val onClicklistener: HistoryPeminjamClicklistener) :
	ListAdapter<PeminjamScanEntries, HistoryInputPeminjamAdapter.HistoryInputPeminjamHolder>(
		PeminjamInsertAdapter.PeminjamScanEntriesDiff()
	) {
	
	interface HistoryPeminjamClicklistener {
		fun onclicklistener(data: PeminjamScanEntries)
	}
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryInputPeminjamHolder {
		return HistoryInputPeminjamHolder(parent.inflate(R.layout.item_history_input))
	}
	
	override fun onBindViewHolder(holder: HistoryInputPeminjamHolder, position: Int) {
		getItem(position).let { holder.bind(it) }
	}
	
	inner class HistoryInputPeminjamHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		fun bind(value: PeminjamScanEntries) {
			with(itemView) {
				tv_entryno_history.text = value.id.toString()
				tv_partno_history.text = value.partNo
				tv_qty_history.text = "1"
				tv_sn_history.text = value.serialNo
				value.id?.let { id ->
					setOnClickListener {
						onClicklistener.onclicklistener(value)
					}
				}
				if (value.sycn_status) {
					tv_post_status.setText(R.string.posted_status_true)
					tv_post_status.setTextColor(resources.getColor(R.color.posted_true))
				} else {
					tv_post_status.setText(R.string.posted_status_false)
					tv_post_status.setTextColor(resources.getColor(R.color.posted_false))
				}
				tv_scanby_history.text = value.employeeCode
				
			}
		}
	}
}