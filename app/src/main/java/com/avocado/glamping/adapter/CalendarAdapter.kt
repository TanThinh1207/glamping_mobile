package com.avocado.glamping.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.avocado.glamping.R
import com.avocado.glamping.data.model.resp.CampsiteResponse

class CalendarAdapter(
    private val campsites : List<CampsiteResponse>,
    private val onClick : (CampsiteResponse) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    class CalendarViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val campsiteName : TextView = view.findViewById(R.id.campsite_name)
        val campsiteStatus : TextView = view.findViewById(R.id.campsite_status)
        val campsiteCard : CardView = view.findViewById(R.id.campsiteCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_chat_user, parent, false)
        return CalendarViewHolder(view)
    }

    override fun getItemCount(): Int = campsites.count()

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val campsite = campsites[position]

        holder.campsiteName.text = campsite.name
        holder.campsiteStatus.text = campsite.status

        holder.campsiteCard.setOnClickListener {
            onClick(campsite)
        }
    }
}