package com.avocado.glamping.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.avocado.glamping.R
import com.avocado.glamping.data.model.req.BookingRequest
import okhttp3.internal.userAgent

class BookingAdapter(
    private val bookingList : MutableList<BookingRequest>,
    private val onClick : (String) -> Unit
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {


    class BookingViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val username : TextView =  view.findViewById(R.id.customer_name)
        val startDate : TextView = view.findViewById(R.id.start_date)
        val endDate : TextView = view.findViewById(R.id.end_date)
        val status : TextView = view.findViewById(R.id.status)
        val total : TextView = view.findViewById(R.id.total)
        val bookingCard : CardView = view.findViewById(R.id.bookingCard)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_card, parent, false)
        return BookingViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bookingList.size
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookingList[position]

        holder.username.text = booking.username
        holder.startDate.text = booking.startDate
        holder.endDate.text = booking.endDate
        holder.status.text = booking.status
        holder.total.text = booking.total.toString()

        holder.bookingCard.setOnClickListener{
            onClick(booking.username)
        }
    }



}
