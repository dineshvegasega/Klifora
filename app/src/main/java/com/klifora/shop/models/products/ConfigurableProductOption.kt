package com.klifora.shop.models.products
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ConfigurableProductOption(
    val attribute_id: String,
    val id: Int,
    val label: String,
    val position: Int,
    val product_id: Int,
    val values: @RawValue ArrayList<Value> = ArrayList()
): Parcelable{
    override fun hashCode(): Int {
        val result = id.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ConfigurableProductOption

        if (attribute_id != other.attribute_id) return false
        if (id != other.id) return false
        if (label != other.label) return false
        if (position != other.position) return false
        if (product_id != other.product_id) return false
        if (values != other.values) return false

        return true
    }
}