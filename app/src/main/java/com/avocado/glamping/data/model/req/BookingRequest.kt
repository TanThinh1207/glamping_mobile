package com.avocado.glamping.data.model.req

data class BookingRequest(
    val userName : String,
    val campSite : String,
    val date : String
) {
}