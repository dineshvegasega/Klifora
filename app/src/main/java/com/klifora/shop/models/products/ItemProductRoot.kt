package com.klifora.shop.models.products
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ItemProductRoot(
    val items: @RawValue ArrayList<ItemProduct> = ArrayList(),
    val search_criteria: @RawValue SearchCriteria,
    val total_count: Int
): Parcelable{
    override fun hashCode(): Int {
        val result = total_count.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemProductRoot

        if (items != other.items) return false
        if (search_criteria != other.search_criteria) return false
        if (total_count != other.total_count) return false

        return true
    }
}