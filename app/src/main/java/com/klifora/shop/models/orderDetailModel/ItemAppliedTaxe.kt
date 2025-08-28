package com.klifora.shop.models.orderDetailModel

data class ItemAppliedTaxe(
    val applied_taxes: List<AppliedTaxe>,
    val item_id: Int,
    val type: String
)