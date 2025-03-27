package com.avocado.glamping.repository

import android.util.Log
import com.avocado.glamping.data.model.req.OrderRequest
import com.avocado.glamping.data.model.resp.BaseResponse
import com.avocado.glamping.data.model.resp.BookingResponse
import com.avocado.glamping.data.model.resp.PagingResponse
import com.avocado.glamping.service.BookingApiService
import javax.inject.Inject

class BookingRepository @Inject constructor(
    private val apiService : BookingApiService
) {
    suspend fun getBookings(
        params: Map<String, String?>,
        page: Int,
        size: Int,
        fields: String,
        sortBy: String,
        direction: String
    ) : Result<BaseResponse<PagingResponse<List<BookingResponse>>>>{
        return try {
            val resp = apiService.getBookings(params,page,size,fields,sortBy,direction)
            if (resp.isSuccessful){
                resp.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Response body is null"))
            } else {
                Result.failure(Exception("Error: Here 1"))
            }
        } catch (e : Exception){
            Result.failure(Exception("Error: Here 2"))
        }
    }

    suspend fun updateBooking(
        bookingId : Int,
        status : String,
        reason : String,
        bookingDetailOrders : List<OrderRequest>
    ) : Result<BaseResponse<BookingResponse>>{
        return try {
            val resp = apiService.updateBookings(bookingId, status, reason, bookingDetailOrders)
            if (resp.isSuccessful){
                resp.body()?.let {
                    Result.success(it)

                } ?: Result.failure(Exception("Response body is null"))
            } else {
                Result.failure(Exception("Error: ${resp.errorBody()?.string()}"))
            }
        } catch (e : Exception){
            Result.failure(Exception("Error: $e"))
        }
    }
}