package com.avocado.glamping.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.glamping.data.model.req.ChatMessageRequest
import com.avocado.glamping.data.model.resp.ChatUserResponse
import com.avocado.glamping.data.model.resp.PagingResponse
import com.avocado.glamping.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor (
    private val chatRepository: ChatRepository
) : ViewModel() {

    private var currentPage = 0  // Track current page
    private val pageSize = 10    // Number of messages per page
    private var isLoading = false  // Prevent multiple calls

    private val _messages = MutableLiveData<MutableList<ChatMessageRequest>>()
    val messages : LiveData<MutableList<ChatMessageRequest>> get() = _messages

    private val _getUsersState = MutableLiveData<GetUsersState>()
    val getUsersState : LiveData<GetUsersState>get() = _getUsersState

    private val _getHistoryState = MutableLiveData<GetHistoryState>()
    val getHistoryState : LiveData<GetHistoryState>get() = _getHistoryState

    fun addMessage(message: ChatMessageRequest, type: Boolean) {
        val updatedMessage = if (type) message.copy(timestamp = convertToLocalTime(message.timestamp)) else message
        val updatedMessages = _messages.value?.toMutableList() ?: mutableListOf()
        updatedMessages.add(updatedMessage)
        updatedMessages.sortBy { parseTimestamp(it.timestamp) }
        _messages.postValue(updatedMessages)
    }


    private fun parseTimestamp(timestamp: String): Long {
        return try {
            timestamp.toLong() // ✅ Already a Unix timestamp in milliseconds
        } catch (e: NumberFormatException) {
            try {
                // ✅ Convert ISO 8601 string to milliseconds
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                sdf.parse(timestamp)?.time ?: 0L
            } catch (ex: Exception) {
                0L // Default to 0 if parsing fails
            }
        }
    }

    fun getUsers(
        userId : Int
    ) {
        viewModelScope.launch {
            _getUsersState.value = GetUsersState.Loading
            try {
                val response = chatRepository.getUsers(userId)
                response.onSuccess { resp ->
                    _getUsersState.value = GetUsersState.Success(resp)
                }.onFailure { resp ->
                    _getUsersState.value = GetUsersState.Error(resp.message.toString())
                }
            }catch (e : Exception){
                _getUsersState.value = GetUsersState.Error(e.message.toString())
            }
        }
    }



    fun getHistory(
        senderId: Int,
        recipientId: Int,
    ) {
        if (isLoading) return

        isLoading = true
        viewModelScope.launch {
            _getHistoryState.value = GetHistoryState.Loading
            try {
                val response = chatRepository.getHistory(
                    senderId, recipientId, currentPage, pageSize, "timestamp", "DESC"
                )
                response.onSuccess { resp ->
                    val newMessages = resp.content.map { message ->
                        message.copy(timestamp = convertToLocalTime(message.timestamp))
                    }

                    if (newMessages.isNotEmpty()) {
                        val updatedList = newMessages + (_messages.value ?: mutableListOf())
                        _messages.postValue(updatedList.toMutableList())
                        currentPage++
                    }
                }.onFailure { resp ->
                    _getHistoryState.postValue(GetHistoryState.Error(resp.message.toString()))
                }
            } catch (e: Exception) {
                _getHistoryState.postValue(GetHistoryState.Error(e.message.toString()))
            } finally {
                isLoading = false
            }
        }
    }


    private fun convertToLocalTime(utcTimestamp: String): String {
        return try {
            val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            utcFormat.timeZone = TimeZone.getTimeZone("UTC") // Treat as UTC

            val date = utcFormat.parse(utcTimestamp) ?: return utcTimestamp // Handle parsing failure

            val localFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            localFormat.timeZone = TimeZone.getDefault() // Convert to local timezone

            localFormat.format(date) // Return converted time
        } catch (e: Exception) {
            utcTimestamp // Return original if error occurs
        }
    }

    fun clearMessages() {
        _messages.value = mutableListOf()
        currentPage = 0
    }

}

sealed class GetUsersState{
    data object Loading : GetUsersState()
    data class Success (val response : List<ChatUserResponse>) : GetUsersState()
    data class Error (val mess : String) : GetUsersState()
}

sealed class GetHistoryState{
    data object Loading : GetHistoryState()
    data class Success (val response : PagingResponse<List<ChatMessageRequest>>) : GetHistoryState()
    data class Error (val mess : String) : GetHistoryState()
}