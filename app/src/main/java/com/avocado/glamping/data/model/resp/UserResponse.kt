package com.avocado.glamping.data.model.resp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class UserResponse(
    val id : Int,
    val email : String,
    val firstname : String,
    val lastname : String,
    val address : String,
    val phone : String,
    val birthday : String,
    val status : Boolean,
    val campSiteIds : List<Int>?,
    val isRestricted : Boolean
) : Parcelable{

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UtilityResponse) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}