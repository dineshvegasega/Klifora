package com.klifora.shop.models.options

data class ItemOptionsItem(
    val entity_id: String,
    val gender: String,
    val metal_color: String,
    val metal_purity: String,
    val metal_weight: String,
    val name: String,
    val price: String,
    val size: String,
    val sku: String,
    val totel_diamond_rate: String,
    val weight: String,
    var isSelected : Boolean = false

)