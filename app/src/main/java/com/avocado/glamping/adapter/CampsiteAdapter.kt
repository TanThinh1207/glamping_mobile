package com.avocado.glamping.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.avocado.glamping.R
import com.avocado.glamping.data.model.resp.CampsiteResponse

class CampsiteAdapter(
    private val campsites: List<CampsiteResponse>,
    private val onClick: (CampsiteResponse) -> Unit
) : RecyclerView.Adapter<CampsiteAdapter.CampsiteViewHolder>() {

    class CampsiteViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val campsiteName : TextView = view.findViewById(R.id.campsite_name)
        val campsiteAddress : TextView = view.findViewById(R.id.campsite_address)
        val campsiteStatus : TextView = view.findViewById(R.id.campsite_status)
        val campsiteCard : CardView = view.findViewById(R.id.campsiteCard)
        val image : ImageView = view.findViewById(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampsiteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_campsite, parent, false)
        return CampsiteViewHolder(view)
    }

    override fun getItemCount(): Int = campsites.count()

    override fun onBindViewHolder(holder: CampsiteViewHolder, position: Int) {
        val campsite = campsites[position]

        holder.campsiteName.text = campsite.name
        holder.campsiteAddress.text = campsite.address
        holder.campsiteStatus.text = campsite.status

        val color = when(campsite.status){
            "Available" -> ContextCompat.getColor(holder.itemView.context, R.color.green)
            "Not_Available" -> ContextCompat.getColor(holder.itemView.context, R.color.red)
            else -> ContextCompat.getColor(holder.itemView.context, R.color.grey)
        }

        holder.campsiteStatus.setTextColor(color)
        holder.image.load(
            campsite.imageList?.getOrNull(0)?.path
        ){
            placeholder(R.drawable.image_placeholder)
            error(R.drawable.image_placeholder)
        }

        holder.campsiteCard.setOnClickListener {
            onClick(campsite)
        }
    }


}