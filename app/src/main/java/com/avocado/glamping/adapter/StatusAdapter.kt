package com.avocado.glamping.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.graphics.alpha
import androidx.recyclerview.widget.RecyclerView
import com.avocado.glamping.R
import com.google.android.material.button.MaterialButton

class StatusAdapter(private val statuses: List<String>, private val onClick: (String) -> Unit) :
    RecyclerView.Adapter<StatusAdapter.StatusViewHolder>(){

        private var selectedPosition = 0;

        class StatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val button: MaterialButton = itemView.findViewById(R.id.statusButton)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.status_button, parent, false)
            return StatusViewHolder(view)
        }

        override fun getItemCount(): Int = statuses.count()

        override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
            val status = statuses[position]

            if (holder.adapterPosition == selectedPosition){
                holder.button.setTypeface(null, Typeface.BOLD)
                holder.button.strokeWidth = 4
                holder.button.alpha = 1.0f
                holder.button.setStrokeColorResource(R.color.black)
            } else {
                holder.button.setTypeface(null, Typeface.NORMAL)
                holder.button.strokeWidth = 4
                holder.button.alpha = 0.5f
                val strokeColor = holder.button.context.getColor(R.color.black).applyAlpha(0.5f)
                holder.button.strokeColor = android.content.res.ColorStateList.valueOf(strokeColor)
            }
            holder.button.text = status
            holder.button.setOnClickListener{
                if (selectedPosition != holder.adapterPosition) { // Only update if the selection changes
                    val previousPosition = selectedPosition
                    selectedPosition = holder.adapterPosition

                    // Notify changes only for the previously selected and newly selected items
                    if (previousPosition != RecyclerView.NO_POSITION) {
                        notifyItemChanged(previousPosition) // Reset the previous selection
                    }
                    notifyItemChanged(selectedPosition) // Highlight the new selection

                    onClick(status)
                }
            }
        }

    private fun Int.applyAlpha(factor: Float): Int {
        val alpha = (Color.alpha(this) * factor).toInt()
        return Color.argb(alpha, Color.red(this), Color.green(this), Color.blue(this))
    }
}