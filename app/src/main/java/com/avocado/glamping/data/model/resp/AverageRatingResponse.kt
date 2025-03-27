package com.avocado.glamping.data.model.resp

data class AverageRatingResponse(
    val averageRating : Double,
    val ratingResponseList : List<RatingResponse>
) {

}