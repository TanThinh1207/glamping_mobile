package com.avocado.glamping.di

import java.text.NumberFormat
import java.util.Locale

object PriceFormat {
    fun formatPrice(amount: Double?, language: String, country: String): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale(language, country))
        return formatter.format(amount)
    }
}