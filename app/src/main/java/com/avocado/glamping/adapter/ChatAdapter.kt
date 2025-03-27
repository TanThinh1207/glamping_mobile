package com.avocado.glamping.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.avocado.glamping.R
import com.avocado.glamping.data.model.req.ChatMessageRequest
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class ChatAdapter(private val userId : Int) :
    ListAdapter<ChatMessageRequest, ChatAdapter.ChatViewHolder>(DiffCallback()) {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.tv_message)
        val messageTime: TextView = itemView.findViewById(R.id.tv_date_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            if (viewType == VIEW_TYPE_SENT) R.layout.item_container_sent_message else R.layout.item_container_received_message,
            parent,
            false
        )
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatMessage = getItem(position)

        try {
            val formattedTime = when {
                chatMessage.timestamp.matches(Regex("\\d+")) -> { // If timestamp is a number
                    val date = Date(chatMessage.timestamp.toLong())
                    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date)
                }
                else -> { // If timestamp is in ISO 8601 format
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                    val localDateTime = LocalDateTime.parse(chatMessage.timestamp, formatter)
                    val date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
                    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date)
                }
            }
            holder.messageTime.text = formattedTime
        } catch (e: Exception) {
            Log.e("ChatAdapter", "Error parsing timestamp: ${chatMessage.timestamp}", e)
            holder.messageTime.text = "Invalid date"
        }
        holder.messageText.text = chatMessage.content
        Log.e("Item Chat", chatMessage.toString())
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).senderId == userId) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

    class DiffCallback : DiffUtil.ItemCallback<ChatMessageRequest>() {
        override fun areItemsTheSame(oldItem: ChatMessageRequest, newItem: ChatMessageRequest): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }

        override fun areContentsTheSame(oldItem: ChatMessageRequest, newItem: ChatMessageRequest): Boolean {
            return oldItem == newItem
        }
    }
}
