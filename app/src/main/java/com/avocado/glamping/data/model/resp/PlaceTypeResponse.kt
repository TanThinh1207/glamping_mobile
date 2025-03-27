package com.avocado.glamping.data.model.resp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class PlaceTypeResponse(
    val id: Int,
    val name: String,
    val imagePath: String,
    val status: Boolean
) : Parcelable