package com.klifora.shop.models.category

import com.klifora.shop.models.Items

data class ChildrenData(
    var children_data: ArrayList<ChildrenDataX> = ArrayList(),
    var id: Int = 0,
    var is_active: Boolean = false,
    var level: Int = 0,
    var name: String = "",
    var parent_id: Int = 0,
    var position: Int = 0,
    var product_count: Int = 0,
    var isSelected: Boolean = false,
    var isCollapse: Boolean = false,
    var isAll: Boolean = false,
)