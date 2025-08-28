package com.klifora.shop.models.category

data class ChildrenDataX(
    val children_data: ArrayList<ChildrenData> = ArrayList(),
    val id: Int = 0,
    var is_active: Boolean = false,
    val level: Int = 0,
    var name: String = "",
    var parent_id: Int = 0,
    val position: Int = 0,
    val product_count: Int = 0,
    var isSelected: Boolean = false,
    var isCollapse: Boolean = false,
    var isAll: Boolean = false,
)