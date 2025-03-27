package com.avocado.glamping.data.model.resp

import android.os.Parcelable
import com.avocado.glamping.data.model.req.OrderRequest
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class BookingDetailResponse(
    val bookingDetailId : Int,
    val campTypeResponse: CampTypeResponse,
    val amount : Double,
    val checkInAt: String?,
    val checkOutAt: String?,
    val createdAt: String,
    val status: String,
    val campResponse: CampResponse?,
    val addOn : Double,
    var orders : MutableList<OrderRequest>? = mutableListOf()
) : Parcelable