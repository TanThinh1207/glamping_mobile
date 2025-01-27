package com.avocado.glamping.repository

import com.avocado.glamping.data.model.network.RegisterApiService
import com.avocado.glamping.data.model.req.RegisterRequest
import com.avocado.glamping.data.model.resp.RegisterResponse
import javax.inject.Inject

class RegisterRepository @Inject constructor(
    private val registerApiService: RegisterApiService
){
    suspend fun register(firstName: String, lastName: String, email: String, password: String) : Result<RegisterResponse>{
        return try{
            val resp = registerApiService.register(RegisterRequest(email, password, firstName, lastName))
            if (resp.isSuccessful){
                Result.success(resp.body()!!)
            }else{
                Result.failure(Exception("Register failed"))
            }
        } catch (e : Exception){
            Result.failure(e)
        }
    }
}