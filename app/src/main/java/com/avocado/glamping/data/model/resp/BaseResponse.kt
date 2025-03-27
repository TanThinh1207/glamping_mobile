package com.avocado.glamping.data.model.resp

import java.util.Objects

data class BaseResponse<T>(
    val statusCode : Int,
    val message : String,
    val data : T
) {
}