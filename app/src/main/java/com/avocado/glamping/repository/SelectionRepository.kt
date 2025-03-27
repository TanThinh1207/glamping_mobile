package com.avocado.glamping.repository

import com.avocado.glamping.data.model.req.ServiceUpdateRequest
import com.avocado.glamping.data.model.resp.BaseResponse
import com.avocado.glamping.data.model.resp.SelectionResponse
import com.avocado.glamping.service.SelectionApiService
import javax.inject.Inject


class SelectionRepository @Inject constructor(
    private val apiService : SelectionApiService
){

    suspend fun updateSelection(
        request : ServiceUpdateRequest
    ) : Result<BaseResponse<SelectionResponse>> {
        return try {
            val resp = apiService.updateSelection(request)
            if (resp.isSuccessful) {
                resp.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Response body is null"))
            } else {
                Result.failure(Exception("Error: ${resp.errorBody()}"))
            }
        }catch (e : Exception){
            Result.failure(Exception("Error: $e"))
        }
    }
}