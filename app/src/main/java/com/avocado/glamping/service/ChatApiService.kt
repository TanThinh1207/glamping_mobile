package com.avocado.glamping.service

import com.avocado.glamping.data.model.req.ChatMessageRequest
import com.avocado.glamping.data.model.resp.BaseResponse
import com.avocado.glamping.data.model.resp.ChatUserResponse
import com.avocado.glamping.data.model.resp.PagingResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ChatApiService {
    @GET("api/chat/recipients")
    suspend fun getUsers(
        @Query("userId") userId : Int
    ) : Response<List<ChatUserResponse>>

    @GET("api/chat/history")
    suspend fun getHistory(
        @Query("senderId") senderId : Int,
        @Query("recipientId") recipientId : Int,
        @Query("page") page : Int,
        @Query("size") size : Int,
        @Query("sortBy") sortBy : String,
        @Query("direction") direction : String
    ) : Response<PagingResponse<List<ChatMessageRequest>>>
}