package com.klifora.shop.models.total

data class ItemTotal(
    val base_currency_code: String,
    val base_discount_amount: Double,
    val base_grand_total: Double,
    val base_shipping_amount: Double,
    val base_shipping_discount_amount: Double,
    val base_shipping_incl_tax: Double,
    val base_shipping_tax_amount: Double,
    val base_subtotal: Double,
    val base_subtotal_with_discount: Double,
    val base_tax_amount: Double,
    val discount_amount: Double,
    val grand_total: Double,
    val items: List<Item> = ArrayList(),
    val items_qty: Int,
    val quote_currency_code: String,
    val shipping_amount: Double,
    val shipping_discount_amount: Double,
    val shipping_incl_tax: Double,
    val shipping_tax_amount: Double,
    val subtotal: Double,
    val subtotal_incl_tax: Double,
    val subtotal_with_discount: Double,
    val tax_amount: Double,
    val total_segments: List<TotalSegment>,
    val weee_tax_applied_amount: Double
)