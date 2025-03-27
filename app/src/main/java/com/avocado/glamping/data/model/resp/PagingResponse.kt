package com.avocado.glamping.data.model.resp

import java.util.Objects

data class PagingResponse <T>(
    val totalRecords : Int,
    val totalPages : Int,
    val currentPage : Int,
    val currentPageSize : Int,
    val content : T
) {
}