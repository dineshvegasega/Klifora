package com.klifora.shop.models.cart

data class Customer(
    val addresses: List<Any>,
    val created_at: String,
    val created_in: String,
    val disable_auto_group_change: Int,
    val email: String,
    val extension_attributes: ExtensionAttributes,
    val firstname: String,
    val group_id: Int,
    val id: Int,
    val lastname: String,
    val store_id: Int,
    val updated_at: String,
    val website_id: Int
)