package dynamia.com.barcodescanner.ui.binreclass.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.databinding.BinReclassItemHolderBinding
import dynamia.com.core.data.entinty.BinreclassHeader

class BinReclassAdapter(
    private val values: MutableList<BinreclassHeader>,
    val listener: BinreclassOnclicklistener,
) :
    RecyclerView.Adapter<BinReclassAdapter.BinReclassHolder>() {

    fun updateData(data: MutableList<BinreclassHeader>) {
        values.clear()
        values.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BinReclassHolder {
        return BinReclassHolder(BinReclassItemHolderBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: BinReclassHolder, position: Int) {
        values[position].let { holder.bind(it) }
    }

    override fun getItemCount(): Int = values.size

    inner class BinReclassHolder(private val binding: BinReclassItemHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: BinreclassHeader) {
            with(binding) {
                tvRebinDate.text = data.date
                tvRebinDocno.text = data.documentNo
                tvRebinCodeFrom.text = data.transferFromBinCode
                tvRebinCodeTo.text = data.transferToBinCode

                if (data.sync_status) {
                    tvBinReclassPosted.setText(R.string.posted_status_true)
                    tvBinReclassPosted.setTextColor(
                        ContextCompat.getColor(
                            root.context,
                            R.color.posted_true
                        )
                    )
                } else {
                    tvBinReclassPosted.setText(R.string.posted_status_false)
                    tvBinReclassPosted.setTextColor(
                        ContextCompat.getColor(
                            root.context,
                            R.color.posted_false
                        )
                    )
                }
                root.setOnClickListener {
                    listener.onclicklistener(data)
                }
            }
        }
    }

    interface BinreclassOnclicklistener {
        fun onclicklistener(data: BinreclassHeader)
    }
}