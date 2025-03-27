package com.avocado.glamping.repository

import com.avocado.glamping.data.model.req.UserRequest
import com.avocado.glamping.data.model.resp.BaseResponse
import com.avocado.glamping.data.model.resp.UserResponse
import com.avocado.glamping.service.UserApiService
import retrofit2.HttpException
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: UserApiService
) {

    suspend fun updateUser(userId: Int, userRequest: UserRequest): Result<BaseResponse<UserResponse>> {
        return try {
            val response = apiService.updateUser(userId, userRequest)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Response body is null"))
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Error: $errorMessage"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Network error: ${e.message()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error: ${e.message}"))
        }
    }
}
