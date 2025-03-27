package com.avocado.glamping.data.model.dto

data class Service(
    val id : Int,
    val name : String,
    val price : Double,
    val image : String,
    val status : Boolean,
    val updatedAt : String,
    val description : String
) {
}