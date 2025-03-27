package com.avocado.glamping.data.model.resp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageResponse(
    val id : Int,
    val path : String,
) : Parcelable{
}