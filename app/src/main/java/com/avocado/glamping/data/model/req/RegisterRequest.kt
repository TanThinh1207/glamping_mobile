package com.avocado.glamping.data.model.req

data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val phone : String,
    val address : String
) {
    constructor(email: String, password: String, firstName: String, lastName: String) : this(email, password, firstName, lastName, "", "")
}