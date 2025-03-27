package com.avocado.glamping.service

import com.avocado.glamping.data.model.req.CampSiteUpdateRequest
import com.avocado.glamping.data.model.resp.BaseResponse
import com.avocado.glamping.data.model.resp.CampSiteNameResponse
import com.avocado.glamping.data.model.resp.CampsiteResponse
import com.avocado.glamping.data.model.resp.PagingResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface CampSiteService {
    @GET("api/campsites")
    suspend fun getCampsites(
        @QueryMap params : Map<String, String>,
        @Query("page") page : Int = 0,
        @Query("size") size : Int = 10,
        @Query("fields") fields : String,
        @Query("sortBy") sortBy: String = "id",
        @Query("direction") direction : String = "ASC"
    ) : Response<BaseResponse<PagingResponse<List<CampsiteResponse>>>>

    @GET("api/campsites")
    suspend fun getCampsitesName(
        @QueryMap params : Map<String, String>,
        @Query("page") page : Int = 0,
        @Query("size") size : Int = 10,
        @Query("fields") fields : String,
        @Query("sortBy") sortBy: String = "id",
        @Query("direction") direction : String = "ASC"
    ) : Response<BaseResponse<PagingResponse<List<CampSiteNameResponse>>>>

    @PATCH("api/campsites/{id}")
    suspend fun updateCampSite(
        @Path("id") id : Int,
        @Body request : CampSiteUpdateRequest
    ) : Response<BaseResponse<CampsiteResponse>>

    @PUT("api/campsites/{id}/utilities/")
    suspend fun updateUtilities(
        @Path("id") id : Int,
        @Body utilitiesIds : List<Int>
    ) : Response<BaseResponse<CampsiteResponse>>
}