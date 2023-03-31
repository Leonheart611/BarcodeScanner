package dynamia.com.barcodescanner.ui.transferstore.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.databinding.PickingDetailLineItemBinding
import dynamia.com.barcodescanner.ui.transferstore.TransferType
import dynamia.com.barcodescanner.ui.transferstore.TransferType.RECEIPT
import dynamia.com.barcodescanner.ui.transferstore.TransferType.SHIPMENT
import dynamia.com.core.data.entinty.TransferShipmentLine
import java.util.*

class TransferDetailLineAdapter() :
    ListAdapter<TransferShipmentLine, TransferDetailLineAdapter.PickingDetailHolder>(
        TransferLineDiffUtil()
    ) {

    private lateinit var transferType: TransferType

    var listener: OnTransferLineCLicklistener? = null

    fun setOnClickListener(clicklistener: OnTransferLineCLicklistener) {
        listener = clicklistener
    }

    fun setTransferType(type: TransferType) {
        transferType = type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickingDetailHolder {
        return PickingDetailHolder(
            PickingDetailLineItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    // val allData by lazy { values }

    override fun onBindViewHolder(holder: PickingDetailHolder, position: Int) {
        getItem(position).let {
            when (transferType) {
                SHIPMENT -> holder.bindShipment(it)
                RECEIPT -> holder.bindReceipt(it)
                else -> {}
            }
        }
    }

/*    fun update(data: MutableList<TransferShipmentLine>) {
        values.clear()
        values = data
        notifyDataSetChanged()
    }*/

    /*  override fun getFilter(): Filter {
          return object : Filter() {
              override fun performFiltering(p0: CharSequence?): FilterResults {
                  val query = p0.toString().uppercase(Locale.ROOT)
                  val filterResult = FilterResults()
                  filterResult.values = if (query.isEmpty())
                      allData
                  else {
                      allData.filter {
                          it.description.uppercase(Locale.ROOT).contains(query) ||
                                  it.lineNo.toString().contains(query)
                      }
                  }
                  return filterResult
              }

              override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                  values = p1?.values as MutableList<TransferShipmentLine>
                  notifyDataSetChanged()
              }
          }
      }*/

    inner class PickingDetailHolder(val binding: PickingDetailLineItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindShipment(transferShipmentLine: TransferShipmentLine) {
            with(binding) {
                tvItemCode.text = transferShipmentLine.itemRefNo
                tvDescription.text = transferShipmentLine.description
                tvQty.text =
                    "${transferShipmentLine.alredyScanned} / ${transferShipmentLine.quantity}"
                root.setOnClickListener {
                    listener?.onclicklistener(transferShipmentLine)
                }
            }
        }

        fun bindReceipt(transferShipmentLine: TransferShipmentLine) {
            with(binding) {
                tvItemCode.text = transferShipmentLine.itemRefNo
                tvDescription.text = transferShipmentLine.description
                tvQty.text =
                    "${transferShipmentLine.alreadyScanedReceipt} / ${transferShipmentLine.qtyInTransit}"
                root.setOnClickListener {
                    listener?.onclicklistener(transferShipmentLine)
                }
            }
        }
    }


    interface OnTransferLineCLicklistener {
        fun onclicklistener(pickingListLineValue: TransferShipmentLine)
    }

    class TransferLineDiffUtil : DiffUtil.ItemCallback<TransferShipmentLine>() {
        override fun areItemsTheSame(
            oldItem: TransferShipmentLine,
            newItem: TransferShipmentLine
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: TransferShipmentLine,
            newItem: TransferShipmentLine
        ): Boolean {
            return oldItem == newItem
        }
    }

}