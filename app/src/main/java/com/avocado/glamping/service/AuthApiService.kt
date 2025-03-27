package com.avocado.glamping.service


import com.avocado.glamping.data.model.req.AuthRequest
import com.avocado.glamping.data.model.resp.AuthResponse
import com.avocado.glamping.data.model.resp.BaseResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/v1/auth/verify")
    suspend fun loginWithFireBase(@Body request : AuthRequest) : Response<BaseResponse<AuthResponse>>
}