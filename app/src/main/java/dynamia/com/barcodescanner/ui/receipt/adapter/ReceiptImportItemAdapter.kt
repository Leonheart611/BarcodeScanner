package dynamia.com.barcodescanner.ui.receipt.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.model.ReceiptImportHeaderValue
import dynamia.com.core.util.inflate

class ReceiptImportItemAdapter(val receiptImportHeaderValue: MutableList<ReceiptImportHeaderValue>):RecyclerView.Adapter<ReceiptImportItemAdapter.ReceiptItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptItemHolder {
        return ReceiptItemHolder(parent.inflate(R.layout.receipt_item_import_layout))
    }

    override fun getItemCount(): Int {
        return receiptImportHeaderValue.size
    }

    override fun onBindViewHolder(holder: ReceiptItemHolder, position: Int) {

    }

    class ReceiptItemHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        fun bind(){

        }
    }
}