package com.klifora.shop.models.cart

data class Currency(
    val base_currency_code: String,
    val base_to_global_rate: Int,
    val base_to_quote_rate: Int,
    val global_currency_code: String,
    val quote_currency_code: String,
    val store_currency_code: String,
    val store_to_base_rate: Int,
    val store_to_quote_rate: Int
)