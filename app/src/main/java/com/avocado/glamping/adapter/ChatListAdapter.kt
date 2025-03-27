package com.avocado.glamping.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.avocado.glamping.R
import com.avocado.glamping.data.model.resp.ChatUserResponse

class ChatListAdapter(
    private val chatUsers : List<ChatUserResponse>,
    private val onClick : (ChatUserResponse) -> Unit
) : RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>(){
    class ChatListViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val userName : TextView = view.findViewById(R.id.tv_user_name)
        val email : TextView = view.findViewById(R.id.tv_user_email)
        val chatCard : CardView = view.findViewById(R.id.card_chat_user)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ChatListViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.card_chat_user, p0, false)
        return ChatListViewHolder(view)
    }

    override fun getItemCount(): Int = chatUsers.count()

    override fun onBindViewHolder(holder: ChatListViewHolder, p1: Int) {
        val chatUser = chatUsers[p1]

        holder.userName.text = chatUser.firstname
        holder.email.text = chatUser.email
        holder.chatCard.setOnClickListener {
            onClick(chatUser)
        }
    }
}