package com.avocado.glamping.data.model.resp

import com.google.firebase.Timestamp

data class ChatMessageResponse(
    val senderId : Int,
    val recipientId : Int,
    val content : String,
    val timestamp: String
) {
}