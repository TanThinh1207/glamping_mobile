package com.avocado.glamping.service

import com.avocado.glamping.data.model.resp.BaseResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.Objects

interface PaymentApiService {
    @POST("api/payments/refund")
    suspend fun refund(
        @Query("bookingId") bookingId : Int,
        @Query("message") message : String
    ) : Response<BaseResponse<String>>

}