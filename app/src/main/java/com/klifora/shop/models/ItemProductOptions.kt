package com.klifora.shop.models

data class ItemProductOptions(
//    val gender: String,
//    val metal_color: String,
//    val metal_purity: String,
//    val name: String,
//    val price: String,
//    val size: String,
//    val sku: String,
//    val weight: String,
//    var isSelected : Boolean = false

    val entity_id: String,
    val name: String,
    val price: String,
    val sku: String,
    val metal_purity: String,
    val metal_weight: String,
    val totel_diamond_rate: String,
    val weight: String,
    val size: String,
    val gender: String,
    val metal_color: String,
    var isSelected : Boolean = false
)


//val entity_id: String,
//val name: String,
//val price: String,
//val sku: String,
//val metal_purity: String,
//val metal_weight: String,
//val totel_diamond_rate: String,
//val weight: String,
//val size: String,
//val gender: String,
//var isSelected : Boolean = false