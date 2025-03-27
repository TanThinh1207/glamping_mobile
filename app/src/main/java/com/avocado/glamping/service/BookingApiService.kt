package com.avocado.glamping.service

import com.avocado.glamping.data.model.req.OrderRequest
import com.avocado.glamping.data.model.resp.BaseResponse
import com.avocado.glamping.data.model.resp.BookingResponse
import com.avocado.glamping.data.model.resp.PagingResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface BookingApiService {
    @GET("api/bookings")
    suspend fun getBookings(
        @QueryMap params: Map<String, String?>,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("fields") fields: String? = null,
        @Query("sortBy") sortBy: String = "id",
        @Query("direction") direction: String = "ASC"
    ) : Response<BaseResponse<PagingResponse<List<BookingResponse>>>>

    @PUT("api/bookings/{bookingId}")
    suspend fun updateBookings(
        @Path("bookingId") bookingId : Int,
        @Query("status") status : String,
        @Query("deniedReason") reason : String,
        @Body bookingDetailOrders : List<OrderRequest>
    ) : Response<BaseResponse<BookingResponse>>
}