package com.klifora.shop.models.category

data class ItemCategory(
    val children_data: ArrayList<ChildrenData> = ArrayList(),
    val id: Int,
    val is_active: Boolean,
    val level: Int,
    val name: String,
    val parent_id: Int,
    val position: Int,
    val product_count: Int
)