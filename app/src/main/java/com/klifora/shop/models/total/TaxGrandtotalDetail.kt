package com.klifora.shop.models.total

data class TaxGrandtotalDetail(
    val amount: Double,
    val group_id: Int,
    val rates: List<Rate> = ArrayList()
)