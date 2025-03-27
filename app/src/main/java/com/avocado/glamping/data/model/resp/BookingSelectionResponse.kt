package com.avocado.glamping.data.model.resp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookingSelectionResponse(
    val id : Int,
    val name : String,
    val quantity : Int,
    val totalAmount : Double
) : Parcelable{
}