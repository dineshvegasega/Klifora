package com.klifora.shop.models.orderHistory
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class FilterGroup(
    val filters: @RawValue List<Filter> = ArrayList()
): Parcelable {
    override fun hashCode(): Int {
        val result = filters.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FilterGroup

        return filters == other.filters
    }
}