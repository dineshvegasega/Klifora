package com.klifora.shop.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemFeedbackItem(
    val created_at: String,
    val detail: String,
    val entity_pk_value: String,
    val nickname: String,
    val percent: String,
    val rating_id: String,
    val review_id: String,
    val title: String,
    val value: String
): Parcelable {
    override fun hashCode(): Int {
        val result = review_id.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemFeedbackItem

        if (created_at != other.created_at) return false
        if (detail != other.detail) return false
        if (entity_pk_value != other.entity_pk_value) return false
        if (nickname != other.nickname) return false
        if (percent != other.percent) return false
        if (rating_id != other.rating_id) return false
        if (review_id != other.review_id) return false
        if (title != other.title) return false
        if (value != other.value) return false

        return true
    }
}