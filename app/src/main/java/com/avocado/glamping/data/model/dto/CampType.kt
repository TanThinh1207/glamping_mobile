package com.avocado.glamping.data.model.dto

import java.time.LocalDateTime

data class CampType(
    val id : Int,
    val type : String,
    val capacity : Int,
    val price : Double,
    val weekendRate : Double,
    val updatedAt : String,
    val quantity : Int,
    val image : String,
    val status : Boolean
) {
}