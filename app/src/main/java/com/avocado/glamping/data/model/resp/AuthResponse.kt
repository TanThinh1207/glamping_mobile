package com.avocado.glamping.data.model.resp

data class AuthResponse(
    val accessToken : String,
    var user : UserResponse,
    val message : String,
    val new : Boolean
) {
}