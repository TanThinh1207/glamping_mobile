package com.avocado.glamping.data.model.req

data class ChatMessageRequest(
    val senderId : Int,
    val recipientId : Int,
    val content : String,
    val timestamp : String
) {
}