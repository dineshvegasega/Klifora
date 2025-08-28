package com.klifora.shop.models.orderHistory

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Total(
    val base_shipping_amount: Int?= null,
    val base_shipping_discount_amount: Int?= null,
    val base_shipping_discount_tax_compensation_amnt: Int?= null,
    val base_shipping_incl_tax: Int?= null,
    val base_shipping_tax_amount: Int?= null,
    val shipping_amount: Int?= null,
    val shipping_discount_amount: Int?= null,
    val shipping_discount_tax_compensation_amount: Int?= null,
    val shipping_incl_tax: Int?= null,
    val shipping_tax_amount: Int?= null
): Parcelable{
    override fun hashCode(): Int {
        val result = base_shipping_amount.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Total

        if (base_shipping_amount != other.base_shipping_amount) return false
        if (base_shipping_discount_amount != other.base_shipping_discount_amount) return false
        if (base_shipping_discount_tax_compensation_amnt != other.base_shipping_discount_tax_compensation_amnt) return false
        if (base_shipping_incl_tax != other.base_shipping_incl_tax) return false
        if (base_shipping_tax_amount != other.base_shipping_tax_amount) return false
        if (shipping_amount != other.shipping_amount) return false
        if (shipping_discount_amount != other.shipping_discount_amount) return false
        if (shipping_discount_tax_compensation_amount != other.shipping_discount_tax_compensation_amount) return false
        if (shipping_incl_tax != other.shipping_incl_tax) return false
        if (shipping_tax_amount != other.shipping_tax_amount) return false

        return true
    }
}