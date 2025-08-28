package com.klifora.shop.models.products
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryLink(
    val category_id: String,
    val position: Int
): Parcelable{
    override fun hashCode(): Int {
        val result = category_id.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CategoryLink

        if (category_id != other.category_id) return false
        if (position != other.position) return false

        return true
    }
}