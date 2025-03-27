package com.avocado.glamping.data.model.resp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatUserResponse(
    val id : Int,
    val firstname : String,
    val email : String
) : Parcelable {

}