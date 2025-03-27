package com.avocado.glamping.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.avocado.glamping.R
import com.google.android.material.button.MaterialButton

class StatusAdapter(private val statuses: List<String>, private val onClick: (String) -> Unit) :
    RecyclerView.Adapter<StatusAdapter.StatusViewHolder>() {

    private var selectedPosition = 0

    class StatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button: MaterialButton = itemView.findViewById(R.id.statusButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.button_status, parent, false)
        return StatusViewHolder(view)
    }

    override fun getItemCount(): Int = statuses.size

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        val status = statuses[position]
        holder.button.text = status

        val isSelected = position == selectedPosition
        holder.button.setTypeface(null, if (isSelected) Typeface.BOLD else Typeface.NORMAL)
        holder.button.strokeWidth = 4
        holder.button.setStrokeColorResource(if (isSelected) R.color.black else R.color.grey)
        holder.button.setTextColor(ContextCompat.getColor(holder.button.context, if (isSelected) R.color.black else R.color.grey))
        holder.button.alpha = if (isSelected) 1.0f else 0.7f

        holder.button.setOnClickListener {
            val adapterPosition = holder.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION && adapterPosition != selectedPosition) {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition

                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)

                onClick(status)
            }
        }
    }
    fun setSelectedStatus(status: String) {
        val newPosition = statuses.indexOf(status)
        if (newPosition != -1 && newPosition != selectedPosition) {
            val previousPosition = selectedPosition
            selectedPosition = newPosition
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
        }
    }

}

