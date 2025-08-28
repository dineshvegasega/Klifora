package com.klifora.shop.models.orderDetailModel

data class Total(
    val base_shipping_amount: Int,
    val base_shipping_discount_amount: Int,
    val base_shipping_discount_tax_compensation_amnt: Int,
    val base_shipping_incl_tax: Int,
    val base_shipping_tax_amount: Int,
    val shipping_amount: Int,
    val shipping_discount_amount: Int,
    val shipping_discount_tax_compensation_amount: Int,
    val shipping_incl_tax: Int,
    val shipping_tax_amount: Int
)