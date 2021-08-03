package dynamia.com.barcodescanner.ui.binreclass.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.databinding.PickingDetailLineItemBinding
import dynamia.com.core.data.entinty.BinreclassInputData

class BinReclassInputAdapter(
    private var values: MutableList<BinreclassInputData>,
    private val listener: OnBinclassInputClicklistener,
) : RecyclerView.Adapter<BinReclassInputAdapter.BinreclassInputHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BinreclassInputHolder {
        return BinreclassInputHolder(PickingDetailLineItemBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
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

    inner class BinreclassInputHolder(val binding: PickingDetailLineItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: BinreclassInputData) {
            with(binding) {
                tvItemCode.text = data.itemIdentifier
                tvQty.text =
                    "QTY: ${data.quantity}"
                root.setOnClickListener {
                    listener.onclicklistener(data)
                }
            }

        }

    }


    interface OnBinclassInputClicklistener {
        fun onclicklistener(value: BinreclassInputData)
    }

}