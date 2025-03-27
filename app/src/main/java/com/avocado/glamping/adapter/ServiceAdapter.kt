package com.avocado.glamping.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.avocado.glamping.BuildConfig
import com.avocado.glamping.R
import com.avocado.glamping.data.model.resp.SelectionResponse
import com.avocado.glamping.di.PriceFormat

class ServiceAdapter(
    private val services : List<SelectionResponse>,
    private val onClick : (SelectionResponse) -> Unit
) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>(){

    class ServiceViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val name : TextView = view.findViewById(R.id.name)
        val price : TextView = view.findViewById(R.id.price)
        val description : TextView = view.findViewById(R.id.description)
        val image : ImageView = view.findViewById(R.id.image)
        val serviceCard : CardView = view.findViewById(R.id.serviceCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_service, parent, false)

        return ServiceViewHolder(view)
    }

    override fun getItemCount(): Int = services.count()

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = services[position]

        holder.name.text = service.name
        holder.price.text = holder.itemView.context.getString(R.string.service_price_format, PriceFormat.formatPrice(
            service.price, BuildConfig.LANGUAGE, BuildConfig.COUNTRY
        ))
        holder.description.text = service.description
        holder.image.load(service.image){
            placeholder(R.drawable.image_placeholder)
            error(R.drawable.image_placeholder)
        }

        holder.serviceCard.setOnClickListener {
            onClick(service)
        }
    }
}