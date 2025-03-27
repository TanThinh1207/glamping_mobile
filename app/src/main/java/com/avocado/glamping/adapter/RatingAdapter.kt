package com.avocado.glamping.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avocado.glamping.R
import com.avocado.glamping.data.model.resp.RatingResponse

class RatingAdapter(private val ratingList: List<RatingResponse>) :
    RecyclerView.Adapter<RatingAdapter.RatingViewHolder>() {

    class RatingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.tvUserName)
        val uploadTime: TextView = itemView.findViewById(R.id.tvUploadTime)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val comment: TextView = itemView.findViewById(R.id.tvComment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rating, parent, false)
        return RatingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        val rating = ratingList[position]
        holder.userName.text = rating.userName
        holder.uploadTime.text = rating.uploadTime
        holder.ratingBar.rating = rating.rating.toFloat()
        holder.comment.text = rating.comment
    }

    override fun getItemCount(): Int = ratingList.size
}
