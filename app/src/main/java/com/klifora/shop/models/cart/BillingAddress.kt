package com.klifora.shop.models.cart

data class BillingAddress(
    val city: Any,
    val country_id: Any,
    val customer_id: Int,
    val email: String,
    val firstname: Any,
    val id: Int,
    val lastname: Any,
    val postcode: Any,
    val region: Any,
    val region_code: Any,
    val region_id: Any,
    val same_as_billing: Int,
    val save_in_address_book: Int,
    val street: List<String>,
    val telephone: Any
)