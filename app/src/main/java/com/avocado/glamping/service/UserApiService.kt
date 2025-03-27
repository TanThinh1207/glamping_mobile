package com.avocado.glamping.service

import com.avocado.glamping.data.model.req.UserRequest
import com.avocado.glamping.data.model.resp.BaseResponse
import com.avocado.glamping.data.model.resp.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApiService {

    @PUT("api/users/{id}")
    suspend fun updateUser(@Path("id") userId: Int, @Body request : UserRequest) : Response<BaseResponse<UserResponse>>
}