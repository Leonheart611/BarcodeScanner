package dynamia.com.barcodescanner.ui.binreclass.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.entinty.BinreclassHeader
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.bin_reclass_item_holder.view.*

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
        return BinReclassHolder(parent.inflate(R.layout.bin_reclass_item_holder))
    }

    override fun onBindViewHolder(holder: BinReclassHolder, position: Int) {
        values[position].let { holder.bind(it) }
    }

    override fun getItemCount(): Int = values.size

    inner class BinReclassHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: BinreclassHeader) {
            with(itemView) {
                tv_rebin_date.text = data.date
                tv_rebin_docno.text = data.documentNo
                tv_rebin_code_from.text = data.transferFromBinCode
                tv_rebin_code_to.text = data.transferToBinCode

                if (data.sync_status) {
                    tv_bin_reclass_posted.setText(R.string.posted_status_true)
                    tv_bin_reclass_posted.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.posted_true
                        )
                    )
                } else {
                    tv_bin_reclass_posted.setText(R.string.posted_status_false)
                    tv_bin_reclass_posted.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.posted_false
                        )
                    )
                }
                setOnClickListener {
                    listener.onclicklistener(data)
                }
            }
        }
    }

    interface BinreclassOnclicklistener {
        fun onclicklistener(data: BinreclassHeader)
    }
}