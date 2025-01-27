package com.avocado.glamping.data.model.network


import com.avocado.glamping.data.model.req.RegisterRequest
import com.avocado.glamping.data.model.resp.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterApiService {
    @POST("api/v1/auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest) : Response<RegisterResponse>
}