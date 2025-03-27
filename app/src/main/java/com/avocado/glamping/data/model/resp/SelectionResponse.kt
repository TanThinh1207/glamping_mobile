package com.avocado.glamping.data.model.resp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectionResponse(
    val id : Int,
    val name : String,
    val description : String,
    val price : Double,
    val image : String,
    val status : Boolean
) : Parcelable{
}