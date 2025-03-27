package com.avocado.glamping.service

import com.avocado.glamping.data.model.req.FcmTokenDeleteRequest
import com.avocado.glamping.data.model.resp.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Query

interface FcmTokenApiService {
    @DELETE("api/fcm-tokens")
    suspend fun deleteToken(
        @Query("userId") userId : Int?,
        @Query("deviceId") deviceId : String
    ) : Response<BaseResponse<String>>
}