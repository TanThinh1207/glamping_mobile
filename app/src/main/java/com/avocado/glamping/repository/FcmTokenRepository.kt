package com.avocado.glamping.repository

import android.util.Log
import com.avocado.glamping.data.model.req.FcmTokenDeleteRequest
import com.avocado.glamping.data.model.resp.BaseResponse
import com.avocado.glamping.service.FcmTokenApiService
import javax.inject.Inject

class FcmTokenRepository @Inject constructor(
    private val apiService : FcmTokenApiService
) {
    suspend fun deleteFcmToken(
        request : FcmTokenDeleteRequest
    ) : Result<BaseResponse<String>>{
        return try {
            val resp = apiService.deleteToken(request.userId, request.deviceId)
            Log.e("FCM repo", "Before")
            if (resp.isSuccessful){
                Log.e("FCM repo", "Success")
                resp.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Response body is null"))
            } else{
                Result.failure(Exception("Error: ${resp.message()}"))
            }
        }catch (e : Exception){
            Result.failure(Exception("Error: $e"))
        }
    }
}