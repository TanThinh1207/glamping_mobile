package com.avocado.glamping.data.model.req

data class ServiceUpdateRequest(
    val id : Int,
    val name : String?,
    val description : String?,
    val price : Double?,
) {
}