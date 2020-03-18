package dynamia.com.barcodescanner.ui.home.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.R
import dynamia.com.core.data.model.HomeData
import dynamia.com.core.util.inflate
import kotlinx.android.synthetic.main.home_item_list.view.*

class HomeAdapterView(var homeDataList:MutableList<HomeData>, val listener:OnHomeClicklistener):RecyclerView.Adapter<HomeAdapterView.HomeViewHolder>() {
    interface OnHomeClicklistener{
        fun onHomeClicklistener(value:String)
    }

    fun updateData(data: MutableList<HomeData>){
        homeDataList.clear()
        homeDataList = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(parent.inflate(R.layout.home_item_list))
    }

    override fun getItemCount(): Int {
        return homeDataList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        homeDataList[position].let {
            holder.bind(it,listener)
        }
    }

    class HomeViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        fun bind(homeData: HomeData, listener: OnHomeClicklistener){
            with(itemView){
                tv_title_activities.text = homeData.title
                tv_count_activities.text = homeData.countData.toString()
                setOnClickListener {
                    listener.onHomeClicklistener(homeData.title)
                }
            }

        }
    }
}