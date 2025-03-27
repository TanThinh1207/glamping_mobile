package com.avocado.glamping.data.model.req

data class FcmTokenDeleteRequest(
    val userId : Int?,
    val deviceId : String
) {
}