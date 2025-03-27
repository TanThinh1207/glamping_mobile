package com.avocado.glamping.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.avocado.glamping.R
import com.avocado.glamping.data.model.dto.Utility
import com.avocado.glamping.data.model.resp.UtilityResponse

class UtilityAdapter(
    private val utilities : List<UtilityResponse>,
    private val onClick : (UtilityResponse) -> Unit
) : RecyclerView.Adapter<UtilityAdapter.UtilityViewHolder>(){

    class UtilityViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val utilityIcon : ImageView = view.findViewById(R.id.utilityIcon)
        val utilityName : TextView = view.findViewById(R.id.utilityName)
        val utilityItem : LinearLayout = view.findViewById(R.id.utilityItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UtilityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_utility, parent, false)
        return UtilityViewHolder(view)
    }

    override fun getItemCount(): Int = utilities.count()

    override fun onBindViewHolder(holder: UtilityViewHolder, position: Int) {
        val utility = utilities[position]

        holder.utilityName.text = utility.name
        holder.utilityIcon.load(utility.imagePath){
            placeholder(R.drawable.image_placeholder)
            error(R.drawable.image_placeholder)
        }

        holder.utilityItem.setOnClickListener{
            onClick(utility)
        }
    }
}