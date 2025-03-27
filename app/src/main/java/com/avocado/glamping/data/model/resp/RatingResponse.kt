package com.avocado.glamping.data.model.resp

class RatingResponse(
    val userId: Int,
    val userName: String,
    val uploadTime: String,
    val rating: Int,
    val comment: String
) {
}