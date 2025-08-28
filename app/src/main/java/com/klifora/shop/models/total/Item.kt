package com.klifora.shop.models.total

data class Item(
    val base_discount_amount: Double,
    val base_price: Double,
    val base_price_incl_tax: Double,
    val base_row_total: Double,
    val base_row_total_incl_tax: Double,
    val base_tax_amount: Double,
    val discount_amount: Double,
    val discount_percent: Double,
    val item_id: Int,
    val name: String,
    val options: String,
    val price: Double,
    val price_incl_tax: Double,
    val qty: Int,
    val row_total: Double,
    val row_total_incl_tax: Double,
    val row_total_with_discount: Double,
    val tax_amount: Double,
    val tax_percent: Double,
    val weee_tax_applied: Any,
    val weee_tax_applied_amount: Any
)