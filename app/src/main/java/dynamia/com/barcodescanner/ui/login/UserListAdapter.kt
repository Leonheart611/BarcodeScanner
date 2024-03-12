package dynamia.com.barcodescanner.ui.login

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dynamia.com.barcodescanner.databinding.ItemUserListBinding
import dynamia.com.core.data.entinty.UserData

class UserListAdapter(val onclick: UserListListener) :
    ListAdapter<UserData, UserListAdapter.UserListViewholder>(UserDataDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewholder {
        return UserListViewholder(
            ItemUserListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: UserListViewholder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class UserListViewholder(private val binding: ItemUserListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: UserData) {
            with(binding) {
                tvAdressName.text = "${data.username} - ${data.companyName}"
                tvHostname.text = data.hostName
                ivDelete.setOnClickListener { onclick.onclicklistener(data) }
                root.setOnClickListener { onclick.onSetValue(data) }
            }
        }
    }

    class UserDataDiffUtil : DiffUtil.ItemCallback<UserData>() {
        override fun areItemsTheSame(oldItem: UserData, newItem: UserData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: UserData,
            newItem: UserData
        ): Boolean {
            return oldItem == newItem
        }
    }

    interface UserListListener {
        fun onclicklistener(data: UserData)
        fun onSetValue(data: UserData)
    }
}