package com.avocado.glamping.data.model.resp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class CampResponse(
    val campId : Int,
    val campName : String,
    val createdAt : String,
    val status : String,
    val updatedAt : String
) : Parcelable{
}