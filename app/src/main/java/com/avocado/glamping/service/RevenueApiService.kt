package com.avocado.glamping.service

import com.avocado.glamping.data.model.resp.BaseResponse
import com.avocado.glamping.data.model.resp.RevenueResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RevenueApiService {
    @GET("api/revenue/{hostId}")
    suspend fun getCampSiteRevenue(
        @Path("hostId") id : Int,
        @Query("startDate") startDate : String,
        @Query("endDate") endDate : String,
        @Query("campSiteId") campSiteId : Int,
        @Query("interval") interval : String
    ) : Response<BaseResponse<RevenueResponse>>
}