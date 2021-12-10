package dynamia.com.barcodescanner.ui.binreclass.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.databinding.ItemTransferInputHistoryBinding
import dynamia.com.core.data.entinty.BinreclassInputData

class BinReclassInputAdapter(
    private var values: MutableList<BinreclassInputData>,
    private val listener: OnBinclassInputClicklistener,
) : RecyclerView.Adapter<BinReclassInputAdapter.BinreclassInputHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BinreclassInputHolder {
        return BinreclassInputHolder(
            ItemTransferInputHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return values.size
    }

    override fun onBindViewHolder(holder: BinreclassInputHolder, position: Int) {
        values[position].let { holder.bind(it) }
    }

    fun addData(data: MutableList<BinreclassInputData>) {
        values.clear()
        values = data
        notifyDataSetChanged()
    }

    inner class BinreclassInputHolder(val binding: ItemTransferInputHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(value: BinreclassInputData) {
            with(binding) {
                tvTransferDocno.text = "${value.itemIdentifier}"
                tvTransferhistoryQty.text = "Qty: ${value.quantity}"
                tvBoxNo.text = "Box No: ${value.box}"
                root.setOnClickListener {
                    listener.onclicklistener(value)
                }
                if (value.sync_status) {
                    tvTransferhistoryStatus.setText(R.string.posted_status_true)
                    tvTransferhistoryStatus.setTextColor(
                        ContextCompat.getColor(
                            root.context,
                            R.color.posted_true
                        )
                    )
                } else {
                    tvTransferhistoryStatus.setText(R.string.posted_status_false)
                    tvTransferhistoryStatus.setTextColor(
                        ContextCompat.getColor(
                            root.context,
                            R.color.posted_false
                        )
                    )
                }
            }

        }

    }


    interface OnBinclassInputClicklistener {
        fun onclicklistener(value: BinreclassInputData)
    }

}