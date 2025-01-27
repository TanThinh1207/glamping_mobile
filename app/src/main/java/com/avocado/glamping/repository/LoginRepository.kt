package com.avocado.glamping.repository

import com.avocado.glamping.data.model.network.LoginApiService
import com.avocado.glamping.data.model.req.LoginRequest
import com.avocado.glamping.data.model.resp.LoginResponse
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val apiService: LoginApiService
) {
    suspend fun login(email: String, password: String): Result<LoginResponse>{
        return try {
            val response = apiService.login(LoginRequest(email, password))
            if (response.isSuccessful){
                Result.success(response.body()!!)
            }else{
                Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}