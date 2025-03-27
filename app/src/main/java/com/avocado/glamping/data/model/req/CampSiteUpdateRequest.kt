package com.avocado.glamping.data.model.req

data class CampSiteUpdateRequest(
    var name: String? = null,
    var address: String? = null,
    var city: String? = null,
    var status: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var message: String? = null,
    var depositRate: Double? = null,
    var description: String? = null
)
