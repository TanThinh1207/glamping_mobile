package com.avocado.glamping.data.model.network

import com.avocado.glamping.data.model.req.LoginRequest
import com.avocado.glamping.data.model.resp.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApiService {
    @POST("api/v1/auth/authenticate")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
}