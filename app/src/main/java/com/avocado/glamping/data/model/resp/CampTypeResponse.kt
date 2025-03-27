package com.avocado.glamping.data.model.resp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class CampTypeResponse(
    val id: Int,
    val type: String,
    val capacity: Int,
    val price: Double,
    val weekendRate: Double,
    val updatedAt: String,
    val quantity: Int,
    val status: Boolean,
    val campSiteId: Int,
    val image: String,
    val facilities : List<FacilityResponse>
) : Parcelable