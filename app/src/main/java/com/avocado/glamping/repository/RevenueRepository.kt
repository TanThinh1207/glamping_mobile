package com.avocado.glamping.repository

import com.avocado.glamping.data.model.resp.BaseResponse
import com.avocado.glamping.data.model.resp.RevenueResponse
import com.avocado.glamping.service.RevenueApiService
import javax.inject.Inject

class RevenueRepository @Inject constructor(
    private val revenueService : RevenueApiService
) {

    suspend fun getCampSiteRevenue(
        id : Int,
        startDate : String,
        endDate : String,
        campSiteId : Int,
        interval : String
    ) : Result<BaseResponse<RevenueResponse>> {
        return try {
            val response = revenueService.getCampSiteRevenue(id,startDate,endDate,campSiteId,interval)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Error: Response body is null"))
            } else {
                Result.failure(Exception("Error: ${response.errorBody()}"))
            }
        }catch (e : Exception){
            Result.failure(Exception("Error: $e"))
        }
    }
}