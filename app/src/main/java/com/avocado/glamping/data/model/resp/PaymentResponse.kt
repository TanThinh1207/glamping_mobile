package com.avocado.glamping.data.model.resp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentResponse(
    val id : Int,
    val paymentMethod : String,
    val totalAmount : Double,
    val status : String,
    val completedAt : String
) : Parcelable {
}