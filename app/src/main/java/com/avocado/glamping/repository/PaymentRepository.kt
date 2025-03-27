package com.avocado.glamping.repository

import com.avocado.glamping.data.model.resp.BaseResponse
import com.avocado.glamping.service.PaymentApiService
import java.util.Objects
import javax.inject.Inject


class PaymentRepository @Inject constructor(
    private val service : PaymentApiService
){
    suspend fun refund(
        bookingId : Int,
        message : String
    ) : Result<BaseResponse<String>>{
        return try{
            val resp = service.refund(bookingId, message)
            if (resp.isSuccessful) {
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