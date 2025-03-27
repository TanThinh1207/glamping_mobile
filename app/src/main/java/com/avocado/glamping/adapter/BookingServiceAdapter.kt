package com.avocado.glamping.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avocado.glamping.BuildConfig
import com.avocado.glamping.R
import com.avocado.glamping.data.model.resp.BookingSelectionResponse
import com.avocado.glamping.di.PriceFormat

class BookingServiceAdapter(
    private val services : List<BookingSelectionResponse>,
) : RecyclerView.Adapter<BookingServiceAdapter.BookingServiceViewHolder>(){

    class BookingServiceViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val quantity : TextView = view.findViewById(R.id.tv_service_quantity)
        val total : TextView = view.findViewById(R.id.tv_service_total)
        val name : TextView = view.findViewById(R.id.tv_service_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingServiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_service, parent, false)
        return BookingServiceViewHolder(view)
    }

    override fun getItemCount(): Int = services.count()

    override fun onBindViewHolder(holder: BookingServiceViewHolder, position: Int) {
        val service = services[position]

        holder.quantity.text = "x ${service.quantity}"
        holder.name.text = service.name
        holder.total.text = PriceFormat.formatPrice(service.totalAmount, BuildConfig.LANGUAGE, BuildConfig.COUNTRY)
    }
}