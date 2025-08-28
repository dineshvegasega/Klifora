package com.klifora.shop.models.orderHistory

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Payment(
    val account_status: String,
    val additional_information: @RawValue List<String> = ArrayList(),
    val amount_ordered: Double,
    val base_amount_ordered: Double,
    val base_shipping_amount: Int ?= null,
    val cc_exp_year: String,
    val cc_last4: String,
    val cc_ss_start_month: String,
    val cc_ss_start_year: String,
    val entity_id: Int ?= null,
    val method: String,
    val parent_id: Int ?= null,
    val shipping_amount: Int ?= null
): Parcelable {
    override fun hashCode(): Int {
        val result = entity_id.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Payment

        if (account_status != other.account_status) return false
        if (additional_information != other.additional_information) return false
        if (amount_ordered != other.amount_ordered) return false
        if (base_amount_ordered != other.base_amount_ordered) return false
        if (base_shipping_amount != other.base_shipping_amount) return false
        if (cc_exp_year != other.cc_exp_year) return false
        if (cc_last4 != other.cc_last4) return false
        if (cc_ss_start_month != other.cc_ss_start_month) return false
        if (cc_ss_start_year != other.cc_ss_start_year) return false
        if (entity_id != other.entity_id) return false
        if (method != other.method) return false
        if (parent_id != other.parent_id) return false
        if (shipping_amount != other.shipping_amount) return false

        return true
    }
}