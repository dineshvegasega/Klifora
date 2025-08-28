package com.klifora.shop.models.myOrdersDetail

data class Address(
    val address_type: String,
    val city: String,
    val country_id: String,
    val email: String,
    val entity_id: Int,
    val firstname: String,
    val lastname: String,
    val parent_id: Int,
    val postcode: String,
    val region: String,
    val region_code: String,
    val region_id: Int,
    val street: List<String>,
    val telephone: String
)