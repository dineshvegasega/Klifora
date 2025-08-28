package com.klifora.shop.models.guestOrderList
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemGuestOrderListItem(
    val CustomerName: String,
    val cartItem: String,
    val createdtime: String,
    val customerEmail: String,
    val customerMobile: String,
    val franchiseCode: String,
    val guestcustomeroder_id: String,
    val status: String,
    val updatedtime: String
): Parcelable {
    override fun hashCode(): Int {
        val result = guestcustomeroder_id.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemGuestOrderListItem

        if (CustomerName != other.CustomerName) return false
        if (cartItem != other.cartItem) return false
        if (createdtime != other.createdtime) return false
        if (customerEmail != other.customerEmail) return false
        if (customerMobile != other.customerMobile) return false
        if (franchiseCode != other.franchiseCode) return false
        if (guestcustomeroder_id != other.guestcustomeroder_id) return false
        if (status != other.status) return false
        if (updatedtime != other.updatedtime) return false

        return true
    }
}