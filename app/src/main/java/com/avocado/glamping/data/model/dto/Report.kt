package com.avocado.glamping.data.model.dto

import java.time.LocalDateTime

data class Report(
    val id: Int,
    val user: User,
    val status: String,
    val createdTime: LocalDateTime,
    val message: String,
    val type: String
) {
}