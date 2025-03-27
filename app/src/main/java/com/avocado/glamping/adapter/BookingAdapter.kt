package com.avocado.glamping.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.avocado.glamping.BuildConfig
import com.avocado.glamping.R
import com.avocado.glamping.data.model.resp.BookingResponse
import com.avocado.glamping.di.PriceFormat
import com.google.android.material.imageview.ShapeableImageView

class BookingAdapter(
    private var bookingList : List<BookingResponse>,
    private val onClick : (BookingResponse) -> Unit
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {


    class BookingViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val campSiteName : TextView = view.findViewById(R.id.tv_camp_site_name)
        val bookingDate : TextView = view.findViewById(R.id.tv_date)
        val status : TextView = view.findViewById(R.id.tv_status)
        val total : TextView = view.findViewById(R.id.tv_total)
        val deposit : TextView = view.findViewById(R.id.tv_deposit)
        val bookingCard : CardView = view.findViewById(R.id.bookingCard)
        val image : ShapeableImageView = view.findViewById(R.id.image)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bookingList.size
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        if (bookingList.isNotEmpty()){
            val booking = bookingList[position]

            val color = when (booking.status){
                "Pending" -> Color.Gray
                "Deposit" -> Color.Yellow
                "Accepted" -> Color.Blue
                "Check_In" -> Color.Blue
                "Completed" -> Color.Green
                else -> Color.Red
            }



            holder.campSiteName.text = booking.campSite.name
            holder.bookingDate.text = holder.itemView.context.getString(R.string.card_booking_date_format, booking.checkIn, booking.checkOut)
            holder.status.apply {
                text = booking.status
                setTextColor(
                    when (booking.status) {
                        "Pending" -> ContextCompat.getColor(context, R.color.gray) // Gray
                        "Deposit" -> ContextCompat.getColor(context, R.color.yellow) // Yellow
                        "Accepted", "Check_In" -> ContextCompat.getColor(context, R.color.blue) // Blue
                        "Completed" -> ContextCompat.getColor(context, R.color.green) // Green
                        else -> ContextCompat.getColor(context, R.color.red) // Red
                    }
                )
            }
            holder.total.text =  PriceFormat.formatPrice(booking.totalAmount, BuildConfig.LANGUAGE, BuildConfig.COUNTRY)
            holder.deposit.text =
                if (booking.paymentResponseList.isEmpty()) PriceFormat.formatPrice(0.0, BuildConfig.LANGUAGE, BuildConfig.COUNTRY)
            else PriceFormat.formatPrice(booking.paymentResponseList[0].totalAmount, BuildConfig.LANGUAGE, BuildConfig.COUNTRY)
            holder.image.load(booking.campSite.imageList.getOrNull(0)?.path ){
                placeholder(R.drawable.image_placeholder)
                error(R.drawable.image_placeholder)
            }

            holder.bookingCard.setOnClickListener{
                onClick(booking)
            }
        }

    }




}
