package com.avocado.glamping.service

import com.avocado.glamping.data.model.resp.BaseResponse
import com.avocado.glamping.data.model.resp.FacilityResponse
import com.avocado.glamping.data.model.resp.PagingResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface FacilityApiService {
    @GET("api/facilities")
    suspend fun getFacilities(
        @QueryMap params : Map<String, String>,
        @Query("page") page : Int = 0,
        @Query("size") size : Int = 10,
        @Query("fields") fields : String,
        @Query("sortBy") sortBy: String = "id",
        @Query("direction") direction : String = "ASC"
    ) : Response<BaseResponse<PagingResponse<List<FacilityResponse>>>>
}