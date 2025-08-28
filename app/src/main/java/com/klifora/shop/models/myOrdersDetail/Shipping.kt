package com.klifora.shop.models.myOrdersDetail

data class Shipping(
    val address: Address,
    val method: String,
    val total: Total
)