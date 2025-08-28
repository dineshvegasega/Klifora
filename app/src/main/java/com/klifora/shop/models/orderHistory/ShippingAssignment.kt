package com.klifora.shop.models.orderHistory
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ShippingAssignment(
    val items: @RawValue List<ItemXX> = ArrayList(),
    val shipping: @RawValue Shipping
): Parcelable {
    override fun hashCode(): Int {
        val result = shipping.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShippingAssignment

        if (items != other.items) return false
        if (shipping != other.shipping) return false

        return true
    }
}