package com.klifora.shop.models.orderDetailModel

data class ShippingAssignment(
    val items: List<ItemX>,
    val shipping: Shipping
)