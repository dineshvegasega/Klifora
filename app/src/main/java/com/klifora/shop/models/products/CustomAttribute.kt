package com.klifora.shop.models.products
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class CustomAttribute(
    val attribute_code: String,
    val value: @RawValue Any
): Parcelable{
    override fun hashCode(): Int {
        val result = attribute_code.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CustomAttribute

        if (attribute_code != other.attribute_code) return false
        if (value != other.value) return false

        return true
    }
}