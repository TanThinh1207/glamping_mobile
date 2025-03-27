package com.avocado.glamping.data.model.resp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CampSiteNameResponse(
    val id : Int,
    val name : String
) : Parcelable {
}