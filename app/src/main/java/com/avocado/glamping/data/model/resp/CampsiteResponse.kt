package com.avocado.glamping.data.model.resp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class CampsiteResponse(
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
    val campSiteSelectionsList : List<SelectionResponse>,
    val campSitePlaceTypeList : List<PlaceTypeResponse>,
    val campSiteUtilityList : List<UtilityResponse>,
    val campSiteCampTypeList : List<CampTypeResponse>
) : Parcelable{
}