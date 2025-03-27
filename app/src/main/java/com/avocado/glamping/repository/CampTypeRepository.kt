package com.avocado.glamping.repository

import com.avocado.glamping.data.model.req.CampTypeUpdateRequest
import com.avocado.glamping.data.model.resp.BaseResponse
import com.avocado.glamping.data.model.resp.CampTypeResponse
import com.avocado.glamping.service.CampTypeApiService
import javax.inject.Inject

class CampTypeRepository @Inject constructor(
    private val apiService : CampTypeApiService
) {
    suspend fun updateCampType(
        request : CampTypeUpdateRequest,
        id : Int
    ) : Result<BaseResponse<CampTypeResponse>> {
        return try {
            val resp = apiService.updateCampType(request, id)
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

    suspend fun updateCampTypeFacilities(
        campTypeId : Int,
        ids : List<Int>
    ) : Result<BaseResponse<CampTypeResponse>> {
        return try {
            val resp = apiService.updateCampTypeFacilities(campTypeId, ids)
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