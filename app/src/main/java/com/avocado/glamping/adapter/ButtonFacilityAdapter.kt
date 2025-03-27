package com.avocado.glamping.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.load
import coil.request.ImageRequest
import com.avocado.glamping.R
import com.avocado.glamping.data.model.resp.FacilityResponse
import com.google.android.material.button.MaterialButton

class ButtonFacilityAdapter(
    private val facilities : List<FacilityResponse>,
    private var selectedFacilities : List<FacilityResponse>,
    private val onItemClick: (FacilityResponse) -> Unit
) : RecyclerView.Adapter<ButtonFacilityAdapter.ButtonFacilityViewHolder>() {
    class ButtonFacilityViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val button : LinearLayout = view.findViewById(R.id.btn_facility)
        val text : TextView = view.findViewById(R.id.text_facility)
        val icon : ImageView = view.findViewById(R.id.icon_facility)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonFacilityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.button_facility, parent, false)
        return ButtonFacilityViewHolder(view)
    }

    override fun getItemCount(): Int = facilities.count()

    override fun onBindViewHolder(holder: ButtonFacilityViewHolder, position: Int) {
        val facility = facilities[position]


        holder.text.text = facility.name
        holder.icon.load(facility.image){
            placeholder(R.drawable.image_placeholder)
            error(R.drawable.image_placeholder)
        }
        holder.button.setOnClickListener {
            onItemClick(facility)
        }

        if (selectedFacilities.contains(facility)){
            holder.button.setBackgroundResource(R.drawable.border_linear_color)
        } else {
            holder.button.setBackgroundResource(R.drawable.border_linear_layout)
        }
    }

    fun updateSelection(selectedList: List<FacilityResponse>) {
        selectedFacilities = selectedList
        notifyDataSetChanged() // Refresh UI
    }
}