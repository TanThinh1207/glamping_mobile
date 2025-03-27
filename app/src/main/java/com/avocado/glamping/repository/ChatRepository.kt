package com.avocado.glamping.repository

import com.avocado.glamping.data.model.req.ChatMessageRequest
import com.avocado.glamping.data.model.resp.ChatUserResponse
import com.avocado.glamping.data.model.resp.PagingResponse
import com.avocado.glamping.service.ChatApiService
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val chatService : ChatApiService
) {

    suspend fun getUsers(
        userId : Int
    ) : Result<List<ChatUserResponse>> {
        return try {
            val response = chatService.getUsers(userId)
            if (response.isSuccessful){
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

    suspend fun getHistory(
        senderId : Int,
        recipientId : Int,
        page : Int,
        size : Int,
        sortBy : String,
        direction : String
    ) : Result<PagingResponse<List<ChatMessageRequest>>> {
        return try {
            val response = chatService.getHistory(senderId, recipientId, page, size, sortBy, direction)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Error: Response body is null"))
            } else {
                Result.failure(Exception("Error: ${response.errorBody().toString()}"))
            }
        } catch (e : Exception){
            Result.failure(Exception("Error: $e"))
        }
    }
}