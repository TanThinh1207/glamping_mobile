package com.avocado.glamping.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.avocado.glamping.BuildConfig
import com.avocado.glamping.R
import com.avocado.glamping.data.model.resp.CampTypeItem
import com.avocado.glamping.di.PriceFormat
import com.google.android.material.imageview.ShapeableImageView

class BookingCampTypeAdapter(
    private val campTypes : List<CampTypeItem>
) : RecyclerView.Adapter<BookingCampTypeAdapter.BookingCampTypeViewHolder>(){

    class BookingCampTypeViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val image : ShapeableImageView = view.findViewById(R.id.img_camp_type)
        val name : TextView = view.findViewById(R.id.tv_camp_type)
        val quantity : TextView = view.findViewById(R.id.tv_quantity)
        val price : TextView = view.findViewById(R.id.tv_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingCampTypeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_camp_type, parent, false)
        return BookingCampTypeViewHolder(view)
    }

    override fun getItemCount(): Int = campTypes.count()

    override fun onBindViewHolder(holder: BookingCampTypeViewHolder, position: Int) {
        val campType = campTypes[position]

        holder.name.text = campType.bookingDetail.campTypeResponse.type
        holder.quantity.text = "x${campType.quantity}"

        Log.e("Booking Detail", campType.bookingDetail.toString())
        holder.price.text = PriceFormat.formatPrice(campType.total, BuildConfig.LANGUAGE, BuildConfig.COUNTRY)
        Log.e("campTypeItem", campType.toString())
        holder.image.load(campType.bookingDetail.campTypeResponse.image){
            placeholder(R.drawable.image_placeholder)
            error(R.drawable.image_placeholder)
        }
    }
}