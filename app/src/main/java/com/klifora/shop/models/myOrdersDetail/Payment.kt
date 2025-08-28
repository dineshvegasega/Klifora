package com.klifora.shop.models.myOrdersDetail

data class Payment(
    val account_status: Any,
    val additional_information: List<String>,
    val amount_ordered: Double = 0.0,
    val base_amount_ordered: Double = 0.0,
    val base_shipping_amount:Double = 0.0,
    val cc_last4: Any,
    val entity_id: Int,
    val method: String,
    val parent_id: Int,
    val shipping_amount: Double = 0.0,
)