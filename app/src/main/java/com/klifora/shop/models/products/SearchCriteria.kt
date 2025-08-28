package com.klifora.shop.models.products
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class SearchCriteria(
    val filter_groups: @RawValue List<FilterGroup>
) : Parcelable{
    override fun hashCode(): Int {
        val result = filter_groups.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SearchCriteria

        return filter_groups == other.filter_groups
    }
}