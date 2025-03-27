package com.avocado.glamping.repository

import android.util.Log
import com.avocado.glamping.data.model.req.CampSiteUpdateRequest
import com.avocado.glamping.data.model.resp.BaseResponse
import com.avocado.glamping.data.model.resp.CampSiteNameResponse
import com.avocado.glamping.data.model.resp.CampsiteResponse
import com.avocado.glamping.data.model.resp.PagingResponse
import com.avocado.glamping.service.CampSiteService
import javax.inject.Inject

class CampSiteRepository @Inject constructor(
    private val apiService : CampSiteService
) {

    suspend fun getCampSites(
        params : Map<String, String>,
        page : Int,
        size : Int,
        fields : String,
        sortBy : String,
        direction : String,
    ) : Result<BaseResponse<PagingResponse<List<CampsiteResponse>>>> {
        return try {
            val resp = apiService.getCampsites(
                params,
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

    suspend fun getCampSitesName(
        params : Map<String, String>,
        page : Int,
        size : Int,
        fields : String,
        sortBy : String,
        direction : String,
    ) : Result<BaseResponse<PagingResponse<List<CampSiteNameResponse>>>> {
        return try {
            val resp = apiService.getCampsitesName(
                params,
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

    suspend fun updateCampSite(
        id : Int,
        request : CampSiteUpdateRequest
    ) : Result<BaseResponse<CampsiteResponse>> {
        return try {
            val resp = apiService.updateCampSite(
                id,
                request
            )
            if (resp.isSuccessful) {
                resp.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Response body is null"))
            } else {
                Result.failure(Exception("Error: ${resp.errorBody()}"))
            }
        } catch (e : Exception){
            Result.failure(Exception("Error: $e"))
        }
    }

    suspend fun updateUtilities(
        id : Int,
        utilitiesId : List<Int>
    ) : Result<BaseResponse<CampsiteResponse>> {
        return try {
            val resp = apiService.updateUtilities(id, utilitiesId)
            if (resp.isSuccessful){
                resp.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Respose body is null"))
            } else {
                Result.failure(Exception("Error: ${resp.errorBody()}"))
            }
        }catch (e : Exception){
            Result.failure(Exception("Error: $e"))
        }
    }
}