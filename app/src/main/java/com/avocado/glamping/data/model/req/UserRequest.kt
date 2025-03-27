package com.avocado.glamping.data.model.req

data class UserRequest(
    val firstName: String,
    val lastName: String,
    val phone: String,
    val address: String,
    val dob: String,
) {
}