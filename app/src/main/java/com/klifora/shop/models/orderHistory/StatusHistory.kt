package com.klifora.shop.models.orderHistory

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StatusHistory(
    val comment: String,
    val created_at: String,
    val entity_id: Int ?= null,
    val entity_name: String,
    val is_customer_notified: Int ?= null,
    val is_visible_on_front: Int ?= null,
    val parent_id: Int ?= null,
    val status: String
): Parcelable{
    override fun hashCode(): Int {
        val result = entity_id.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StatusHistory

        if (comment != other.comment) return false
        if (created_at != other.created_at) return false
        if (entity_id != other.entity_id) return false
        if (entity_name != other.entity_name) return false
        if (is_customer_notified != other.is_customer_notified) return false
        if (is_visible_on_front != other.is_visible_on_front) return false
        if (parent_id != other.parent_id) return false
        if (status != other.status) return false

        return true
    }
}