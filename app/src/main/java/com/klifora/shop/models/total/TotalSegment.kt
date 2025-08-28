package com.klifora.shop.models.total

data class TotalSegment(
    val area: String,
    val code: String,
    val extension_attributes: ExtensionAttributes,
    val title: String,
    val value: Double
)