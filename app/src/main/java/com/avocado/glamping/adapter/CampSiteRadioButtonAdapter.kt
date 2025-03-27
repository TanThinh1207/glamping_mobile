package com.avocado.glamping.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avocado.glamping.R
import com.avocado.glamping.data.model.resp.CampSiteNameResponse

class CampSiteRadioButtonAdapter(
    private val campSites : List<CampSiteNameResponse>,
    selectedCampSite : CampSiteNameResponse
) : RecyclerView.Adapter<CampSiteRadioButtonAdapter.CampSiteRadioButtonViewHolder>() {

    private var currentCampSite = selectedCampSite

    class CampSiteRadioButtonViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val campSiteName : TextView = view.findViewById(R.id.tv_camp_site_name)
        val radioBtn : RadioButton = view.findViewById(R.id.radio_btn)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CampSiteRadioButtonViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.item_camp_site_radio_button, p0 ,false)
        return CampSiteRadioButtonViewHolder(view)
    }

    override fun getItemCount(): Int = campSites.count()

    override fun onBindViewHolder(holder: CampSiteRadioButtonViewHolder, p1: Int) {
        val campSite = campSites[p1]


        holder.campSiteName.text = campSite.name
        holder.radioBtn.isChecked = campSite.id == currentCampSite.id

        holder.radioBtn.setOnClickListener{
            currentCampSite = campSite
            notifyDataSetChanged()
        }

    }

    fun getSelectedCampSite(): CampSiteNameResponse? {
        return currentCampSite
    }
}