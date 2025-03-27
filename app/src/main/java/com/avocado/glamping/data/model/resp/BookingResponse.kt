package com.avocado.glamping.data.model.resp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Parcelize
data class BookingResponse(
    val id : Int,
    val user : UserResponse,
    val campSite : CampSiteResponseFilter,
    val checkIn : String,
    val checkOut : String,
    val createdAt : String,
    val status : String,
    val totalAmount : Double,
    val campTypeItemResponse : List<CampTypeItem>,
    val bookingDetailResponseList : List<BookingDetailResponse>,
    val bookingSelectionResponseList : List<BookingSelectionResponse>,
    val paymentResponseList : List<PaymentResponse>
) : Parcelable {
    fun getTotalDays(): Int {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") // Adjust based on your date format
        val checkInDate = LocalDate.parse(checkIn, formatter)
        val checkOutDate = LocalDate.parse(checkOut, formatter)

        return ChronoUnit.DAYS.between(checkInDate, checkOutDate).toInt()
    }
}