package com.klifora.shop.models.orderHistory
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Shipping(
    val address: @RawValue Address,
    val method: @RawValue String,
    val total: @RawValue Total
): Parcelable {
    override fun hashCode(): Int {
        val result = address.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Shipping

        if (address != other.address) return false
        if (method != other.method) return false
        if (total != other.total) return false

        return true
    }
}