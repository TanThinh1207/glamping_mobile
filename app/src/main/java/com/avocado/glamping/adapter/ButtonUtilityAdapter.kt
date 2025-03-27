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
import coil.load
import com.avocado.glamping.R
import com.avocado.glamping.data.model.resp.UtilityResponse

class ButtonUtilityAdapter(
    private val utilities : List<UtilityResponse>,
    private var selectedUtilities : List<UtilityResponse>,
    private val onItemClick : (UtilityResponse) -> Unit
) :RecyclerView.Adapter<ButtonUtilityAdapter.ButtonUtilityViewHolder>() {

    class ButtonUtilityViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val button : LinearLayout = view.findViewById(R.id.btn_utility)
        val text : TextView = view.findViewById(R.id.tv_utility)
        val icon : ImageView = view.findViewById(R.id.icon_utility)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonUtilityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.button_utility, parent, false)
        return ButtonUtilityViewHolder(view)
    }

    override fun getItemCount(): Int = utilities.count()

    override fun onBindViewHolder(holder: ButtonUtilityViewHolder, position: Int) {
        val utility = utilities[position]

        holder.text.text = utility.name
        holder.icon.load(utility.imagePath){
            placeholder(R.drawable.image_placeholder)
            error(R.drawable.image_placeholder)
        }
        holder.button.setOnClickListener {
            onItemClick(utility)
        }

        if (selectedUtilities.contains(utility)) {
            holder.button.setBackgroundResource(R.drawable.border_linear_color)
        } else {
            holder.button.setBackgroundResource(R.drawable.border_linear_layout)
        }
    }

    fun updateSelection(selectedList: List<UtilityResponse>) {
        selectedUtilities = selectedList
        notifyDataSetChanged() // Refresh UI
    }
}