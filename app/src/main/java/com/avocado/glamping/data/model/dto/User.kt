package com.avocado.glamping.data.model.dto

import java.util.Date

data class User(
    val id: Int,
    val email: String,
    val password: String,
    val firstname: String,
    val lastname: String,
    val phoneNumber: String,
    val address: String,
    val dob: Date,
    val role: String,
) {
}