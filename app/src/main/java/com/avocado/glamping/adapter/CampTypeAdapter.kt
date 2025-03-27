package com.avocado.glamping.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.avocado.glamping.BuildConfig
import com.avocado.glamping.R
import com.avocado.glamping.data.model.resp.CampTypeResponse
import com.avocado.glamping.di.PriceFormat
import com.google.android.material.imageview.ShapeableImageView

class CampTypeAdapter (
    private val campTypes : List<CampTypeResponse>,
    private val onClick : (CampTypeResponse) -> Unit
) : RecyclerView.Adapter<CampTypeAdapter.CampTypeViewHolder>() {

    class CampTypeViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val campTypeName : TextView = view.findViewById(R.id.camp_type_name)
        val price : TextView = view.findViewById(R.id.daily_price)
        val weekendPrice : TextView = view.findViewById(R.id.weekend_price)
        val capacity : TextView = view.findViewById(R.id.capacity)
        val camps : TextView = view.findViewById(R.id.number_camp)
        val campTypeCard : CardView = view.findViewById(R.id.campTypeCard)
        val image : ShapeableImageView = view.findViewById(R.id.img_camp_site_detail_camp_type)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampTypeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_camp_type, parent, false)

        return CampTypeViewHolder(view)
    }

    override fun getItemCount(): Int = campTypes.count()

    override fun onBindViewHolder(holder: CampTypeViewHolder, position: Int) {
        val campType = campTypes[position]

        holder.campTypeName.text = campType.type
        holder.price.text = PriceFormat.formatPrice(campType.price, BuildConfig.LANGUAGE, BuildConfig.COUNTRY)
        holder.weekendPrice.text = PriceFormat.formatPrice(campType.weekendRate, BuildConfig.LANGUAGE, BuildConfig.COUNTRY)
        holder.capacity.text = holder.itemView.context.getString(R.string.capacity_format, campType.capacity)
        holder.camps.text = holder.itemView.context.getString(R.string.camps_format, campType.quantity)
        Log.e("Camp Type Image", campType.image)
        holder.image.load(campType.image){
            placeholder(R.drawable.image_placeholder)
            error(R.drawable.image_placeholder)
        }

        holder.campTypeCard.setOnClickListener {
            onClick(campType)
        }
    }
}