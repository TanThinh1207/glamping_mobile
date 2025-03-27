package com.avocado.glamping.service

import com.avocado.glamping.data.model.req.CampTypeUpdateRequest
import com.avocado.glamping.data.model.resp.BaseResponse
import com.avocado.glamping.data.model.resp.CampTypeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

interface CampTypeApiService {
    @PUT("api/camp-types/{campTypeId}")
    suspend fun updateCampType(
        @Body request : CampTypeUpdateRequest,
        @Path("campTypeId") id : Int
    ) : Response<BaseResponse<CampTypeResponse>>

    @PUT("api/camp-types/{campTypeId}/facilities/")
    suspend fun updateCampTypeFacilities(
        @Path("campTypeId") campTypeId : Int,
        @Body ids : List<Int>
    ) : Response<BaseResponse<CampTypeResponse>>
}