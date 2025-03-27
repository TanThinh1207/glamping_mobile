package com.avocado.glamping.data.model.resp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class CampSiteResponseFilter(
    val id : Int,
    val name : String,
    val address : String,
    val city : String,
    val latitude : Double,
    val longitude : Double,
    val createdTime : String,
    val status : String,
    val depositRate : Double,
    val message: String,
    val description : String,
    val imageList : List<ImageResponse>,

) : Parcelable {

}