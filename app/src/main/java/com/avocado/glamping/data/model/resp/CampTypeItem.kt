package com.avocado.glamping.data.model.resp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CampTypeItem(
    val bookingDetail: BookingDetailResponse,
    val quantity : Int,
    val total : Double
) : Parcelable{
}