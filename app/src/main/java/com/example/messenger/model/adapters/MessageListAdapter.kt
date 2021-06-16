package com.example.messenger.model.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.R
import com.example.messenger.databinding.MessageItemBinding
import com.example.messenger.model.dataModel.Message
import java.text.SimpleDateFormat
import java.time.LocalDateTime

class MessageListAdapter:
    RecyclerView.Adapter<MessageListAdapter.MessageViewHolder>(){

    class MessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private val diffCallback = object: DiffUtil.ItemCallback<Message>(){
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.message_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]

        holder.itemView.apply {

            val binding = MessageItemBinding.bind(holder.itemView)


            binding.tvMessageText.text = message.text
            binding.tvSendTime.text = message.sendTime
            binding.tvSenderUserName.text = if(message.senderUserName.isNullOrBlank()) "You" else message.senderUserName

        }
    }


    override fun getItemCount(): Int {
        return messages.size
    }

    fun clear(){
        messages = arrayListOf()
        differ.submitList(messages)
    }

    val differ = AsyncListDiffer(this, diffCallback)

    var messages: MutableList<Message>
        get() = differ.currentList
        set(value) = differ.submitList(value)

}