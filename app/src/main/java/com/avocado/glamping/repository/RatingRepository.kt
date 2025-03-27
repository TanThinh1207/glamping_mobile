package com.avocado.glamping.repository

import com.avocado.glamping.data.model.resp.AverageRatingResponse
import com.avocado.glamping.data.model.resp.BaseResponse
import com.avocado.glamping.data.model.resp.PagingResponse
import com.avocado.glamping.data.model.resp.RatingResponse
import com.avocado.glamping.service.RatingApiService
import javax.inject.Inject

class RatingRepository @Inject constructor(
    private val ratingApiService : RatingApiService
) {

    suspend fun getRatings(
        campSiteId : Int,
        page : Int,
        size : Int,
        fields : String,
        sortBy : String,
        direction : String,
    ) : Result<BaseResponse<PagingResponse<AverageRatingResponse>>>{
        return try {
            val resp = ratingApiService.getRatings(
                campSiteId,
                page,
                size,
                fields,
                sortBy,
                direction
            )
            if (resp.isSuccessful){
                resp.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Response body is null"))
            } else{
                Result.failure(Exception("Error: ${resp.errorBody()}"))
            }
        } catch (e : Exception){
            Result.failure(Exception("Error: ${e}"))
        }
    }
}