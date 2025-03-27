package com.avocado.glamping.data.model.resp

data class ProfitResponse(
    val date : String,
    val totalRevenue : Double,
    val totalProfit : Double,
    val numberOfBookings : Int
) {
}