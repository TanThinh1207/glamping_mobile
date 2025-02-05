package com.avocado.glamping.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.avocado.glamping.R
import com.avocado.glamping.data.model.req.BookingRequest

class BookingAdapter(
    private val bookingList : MutableList<BookingRequest>,
    private val listener : OnBookingActionListener
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {


    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userName)
        val campName: TextView = itemView.findViewById(R.id.campName)
        val bookingDate: TextView = itemView.findViewById(R.id.bookingDate)
        val btnAccept: AppCompatButton = itemView.findViewById(R.id.btnAccept)
        val btnDeny: AppCompatButton = itemView.findViewById(R.id.btnDeny)
    }


    interface OnBookingActionListener {
        fun onAccept(booking: BookingRequest)
        fun onDeny(booking: BookingRequest)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return BookingViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bookingList.size
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookingList[position]
        holder.userName.text = booking.userName
        holder.campName.text = booking.campSite
        holder.bookingDate.text = booking.date

        holder.btnAccept.setOnClickListener{
            listener.onAccept(booking)
            removeItem(position)
        }

        holder.btnDeny.setOnClickListener{
            listener.onDeny(booking)
            removeItem(position)
        }
    }

    private fun removeItem(position: Int) {
        bookingList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, bookingList.size)
    }


}
