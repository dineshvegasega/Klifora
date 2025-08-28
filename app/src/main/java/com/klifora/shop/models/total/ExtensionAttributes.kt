package com.klifora.shop.models.total

data class ExtensionAttributes(
    val tax_grandtotal_details: List<TaxGrandtotalDetail> = ArrayList()
)