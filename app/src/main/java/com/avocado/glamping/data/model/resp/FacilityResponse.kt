package com.avocado.glamping.data.model.resp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FacilityResponse(
    val id: Int,
    val name: String,
    val description: String,
    val image: String,
    val status: Boolean
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FacilityResponse) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}