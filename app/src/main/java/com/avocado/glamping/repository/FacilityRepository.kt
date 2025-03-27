package com.avocado.glamping.repository

import com.avocado.glamping.data.model.resp.BaseResponse
import com.avocado.glamping.data.model.resp.FacilityResponse
import com.avocado.glamping.data.model.resp.PagingResponse
import com.avocado.glamping.service.FacilityApiService
import javax.inject.Inject

class FacilityRepository @Inject constructor(
    private val apiService : FacilityApiService
) {

    suspend fun getFacilities(
        params : Map<String, String>,
        page : Int,
        size : Int,
        fields : String,
        sortBy : String,
        direction : String,
    ) : Result<BaseResponse<PagingResponse<List<FacilityResponse>>>> {
        return try {
            val resp = apiService.getFacilities(params, page, size, fields, sortBy, direction)
            if (resp.isSuccessful){
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