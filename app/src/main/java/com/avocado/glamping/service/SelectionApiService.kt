package com.avocado.glamping.service

import com.avocado.glamping.data.model.req.ServiceUpdateRequest
import com.avocado.glamping.data.model.resp.BaseResponse
import com.avocado.glamping.data.model.resp.SelectionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT

interface SelectionApiService {
    @PUT("api/selections")
    suspend fun updateSelection(
        @Body request : ServiceUpdateRequest
    ) : Response<BaseResponse<SelectionResponse>>
}