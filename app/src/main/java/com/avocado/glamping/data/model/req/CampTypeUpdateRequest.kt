package com.avocado.glamping.data.model.req

data class CampTypeUpdateRequest(
    val type: String?,
    val capacity: Int?,
    val price: Double?,
    val weekendRate: Double?,
    val quantity: Int?,
    val status: Boolean?,
) {
}