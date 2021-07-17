package dynamia.com.barcodescanner.ui.binreclass.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.entinty.BinreclassInputData
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.picking_detail_line_item.view.*

class BinReclassInputAdapter(
    private var values: MutableList<BinreclassInputData>,
    private val listener: OnBinclassInputClicklistener,
) : RecyclerView.Adapter<BinReclassInputAdapter.BinreclassInputHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BinreclassInputHolder {
        return BinreclassInputHolder(parent.inflate(R.layout.picking_detail_line_item))
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

    inner class BinreclassInputHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: BinreclassInputData) {
            with(itemView) {
                tv_item_code.text = data.itemIdentifier
                tv_qty.text =
                    "QTY: ${data.quantity}"
                setOnClickListener {
                    listener.onclicklistener(data)
                }
            }

        }

    }


    interface OnBinclassInputClicklistener {
        fun onclicklistener(value: BinreclassInputData)
    }

}