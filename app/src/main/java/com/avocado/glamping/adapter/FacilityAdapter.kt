package com.avocado.glamping.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.avocado.glamping.R
import com.avocado.glamping.data.model.resp.FacilityResponse

class FacilityAdapter(
    private val facilities : List<FacilityResponse>,

) : RecyclerView.Adapter<FacilityAdapter.FacilityViewHolder>(){

    class FacilityViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val icon : ImageView = view.findViewById(R.id.ic_facility)
        val name : TextView = view.findViewById(R.id.tv_facility_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacilityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_facility, parent, false)
        return FacilityViewHolder(view)
    }

    override fun getItemCount(): Int = facilities.count()

    override fun onBindViewHolder(holder: FacilityViewHolder, position: Int) {
        val facility = facilities[position]
        Log.e("Image", facility.image)
        holder.name.text = facility.name
        holder.icon.load(facility.image){
            placeholder(R.drawable.image_placeholder)
            error(R.drawable.image_placeholder)
        }
    }
}