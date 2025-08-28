package com.klifora.shop.models.orderHistory

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ExtensionAttributes(
    val applied_taxes: @RawValue List<String> = ArrayList(),
    val item_applied_taxes: @RawValue List<String> = ArrayList(),
    val payment_additional_info: @RawValue List<PaymentAdditionalInfo> = ArrayList(),
    val shipping_assignments: @RawValue List<ShippingAssignment> = ArrayList()
): Parcelable {
    override fun hashCode(): Int {
        val result = applied_taxes.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExtensionAttributes

        if (applied_taxes != other.applied_taxes) return false
        if (item_applied_taxes != other.item_applied_taxes) return false
        if (payment_additional_info != other.payment_additional_info) return false
        if (shipping_assignments != other.shipping_assignments) return false

        return true
    }
}