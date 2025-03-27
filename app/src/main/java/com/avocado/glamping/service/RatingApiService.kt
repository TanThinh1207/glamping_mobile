package com.avocado.glamping.service

import com.avocado.glamping.data.model.resp.AverageRatingResponse
import com.avocado.glamping.data.model.resp.BaseResponse
import com.avocado.glamping.data.model.resp.PagingResponse
import com.avocado.glamping.data.model.resp.RatingResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RatingApiService {
    @GET("api/ratings/{campSiteId}")
    suspend fun getRatings(
        @Path("campSiteId") campSiteId : Int,
        @Query("page") page : Int = 0,
        @Query("size") size : Int = 10,
        @Query("fields") fields : String,
        @Query("sortBy") sortBy: String = "id",
        @Query("direction") direction : String = "ASC"
    ) : Response<BaseResponse<PagingResponse<AverageRatingResponse>>>
}