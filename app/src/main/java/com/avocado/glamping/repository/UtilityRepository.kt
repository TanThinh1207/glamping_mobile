package com.avocado.glamping.repository

import com.avocado.glamping.data.model.resp.BaseResponse
import com.avocado.glamping.data.model.resp.PagingResponse
import com.avocado.glamping.data.model.resp.UtilityResponse
import com.avocado.glamping.service.UtilityApiService
import javax.inject.Inject

class UtilityRepository @Inject constructor(
    private val apiService : UtilityApiService
){
  suspend fun getUtilities(
      params : Map<String, String>,
      page : Int,
      size : Int,
      fields : String,
      sortBy : String,
      direction : String,
  ) : Result<BaseResponse<PagingResponse<List<UtilityResponse>>>>{
      return try {
          val resp = apiService.getUtilities(params, page, size, fields, sortBy, direction)
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