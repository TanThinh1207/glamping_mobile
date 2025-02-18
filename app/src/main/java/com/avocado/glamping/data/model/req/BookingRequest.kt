package com.avocado.glamping.data.model.req

data class BookingRequest(
    val id : Int,
    val username : String,
    val campSite : String,
    val startDate : String,
    val endDate : String,
    val total : Double,
    val status : String
) {
}