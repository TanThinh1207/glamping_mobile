package com.avocado.glamping.data.model.dto

import org.locationtech.jts.geom.Point


data class CampSite(
    val name: String,
    val address: String,
    val city: String,
    val location : Point,
    val status: String,
    val message: String,
    val description: String,
    val images: List<String>,
    val reports: List<Report>
) {
    constructor(name : String, address: String, city: String, status: String, location: Point, description: String) : this(
        name,
        address,
        city,
        location,
        status,
        "",
        description,
        emptyList(),
        emptyList()
    )
}