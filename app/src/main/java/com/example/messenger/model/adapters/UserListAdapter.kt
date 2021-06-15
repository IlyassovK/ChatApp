package com.example.messenger.model.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.R
import com.example.messenger.databinding.FragmentUserListBinding
import com.example.messenger.databinding.UserItemBinding
import com.example.messenger.model.dataModel.User

class UserListAdapter:
    RecyclerView.Adapter<UserListAdapter.UserViewHolder>(){

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private val diffCallback = object: DiffUtil.ItemCallback<User>(){
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.user_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]

        holder.itemView.apply {

            val binding = UserItemBinding.bind(holder.itemView)

            binding.tvUserName.text = user.fullName
            binding.tvUserEmail.text = user.email

             setOnClickListener {
                onItemClickListener?.let { click ->
                    click(user)
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    val differ = AsyncListDiffer(this, diffCallback)

    var users: List<User>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private var onItemClickListener: ((User) -> Unit)? = null

    fun setItemClickListener(listener: (User) -> Unit){
        onItemClickListener = listener
    }

}