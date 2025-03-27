package com.avocado.glamping.data.model.resp

data class RevenueResponse(
    val recentRevenue : Double,
    val profitList : List<ProfitResponse>
) {
}