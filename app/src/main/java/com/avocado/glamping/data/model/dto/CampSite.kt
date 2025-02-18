package com.avocado.glamping.data.model.dto

data class CampSite(
    val name: String,
    val address: String,
    val city: String,
    val latitude: Double,
    val longitude: Double,
    val status: String,
    val message: String,
    val images: List<String>,
    val reports: List<Report>
) {
}