package com.avocado.glamping.data.model.req

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderRequest(
    val bookingDetailId: Int?,
    val name: String?,
    val quantity: Int?,
    val price: Double?,
    val totalAmount: Double?,
    val note: String?
) : Parcelable {
}