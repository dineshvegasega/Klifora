package com.klifora.shop.models.cart

data class ItemCartModel(
    val item_id: Int,
    val name: String,
    val price: Double = 0.0,
    val product_type: String,
    var qty: Int = 0,
    val quote_id: String,
    val sku: String
)