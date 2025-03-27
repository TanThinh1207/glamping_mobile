package com.avocado.glamping.data.model.req

data class AuthRequest(
    val idToken: String,
    val fcmToken: String,
    val deviceId: String
) {
}