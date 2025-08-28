package com.klifora.shop.models.products
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Value(
    val value_index: Int,
    var isSelected : Boolean = false
): Parcelable{
    override fun hashCode(): Int {
        val result = value_index.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Value

        if (value_index != other.value_index) return false
        if (isSelected != other.isSelected) return false

        return true
    }
}