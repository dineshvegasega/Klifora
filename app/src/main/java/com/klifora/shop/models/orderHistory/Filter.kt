package com.klifora.shop.models.orderHistory
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Filter(
    val condition_type: String,
    val `field`: String,
    val value: String
): Parcelable {
    override fun hashCode(): Int {
        val result = value.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Filter

        if (condition_type != other.condition_type) return false
        if (`field` != other.`field`) return false
        if (value != other.value) return false

        return true
    }
}