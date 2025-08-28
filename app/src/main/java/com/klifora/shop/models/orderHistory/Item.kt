package com.klifora.shop.models.orderHistory

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Item(
    val base_currency_code: String ,
    val base_discount_amount: Int ?= null,
    val base_discount_tax_compensation_amount: Int?= null,
    val base_grand_total: Double,
    val base_shipping_amount: Int?= null,
    val base_shipping_discount_amount: Int?= null,
    val base_shipping_discount_tax_compensation_amnt: Int?= null,
    val base_shipping_incl_tax: Int?= null,
    val base_shipping_tax_amount: Int?= null,
    val base_subtotal: Double,
    val base_subtotal_incl_tax: Double,
    val base_tax_amount: Int?= null,
    val base_to_global_rate: Int?= null,
    val base_to_order_rate: Int?= null,
    val base_total_due: Double,
    val billing_address: @RawValue BillingAddress,
    val billing_address_id: Int?= null,
    val created_at: String,
    val customer_email: String,
    val customer_firstname: String,
    val customer_gender: Int?= null,
    val customer_group_id: Int?= null,
    val customer_id: Int?= null,
    val customer_is_guest: Int?= null,
    val customer_lastname: String,
    val customer_note_notify: Int?= null,
    val discount_amount: Int?= null,
    val discount_tax_compensation_amount: Int?= null,
    val entity_id: Int?= null,
    val extension_attributes: @RawValue ExtensionAttributes,
    val global_currency_code: String,
    val grand_total: Double,
    val increment_id: String,
    val is_virtual: Int?= null,
    val items: @RawValue List<ItemXX> = ArrayList(),
    val order_currency_code: String,
    val payment: @RawValue Payment,
    val protect_code: String,
    val quote_id: Int?= null,
    val remote_ip: String,
    val shipping_amount: Int?= null,
    val shipping_description: String,
    val shipping_discount_amount: Int?= null,
    val shipping_discount_tax_compensation_amount: Int?= null,
    val shipping_incl_tax: Int?= null,
    val shipping_tax_amount: Int?= null,
    val state: String,
    val status: String,
    val status_histories: @RawValue List<StatusHistory> = ArrayList(),
    val store_currency_code: String,
    val store_id: Int?= null,
    val store_name: String,
    val store_to_base_rate: Int?= null,
    val store_to_order_rate: Int?= null,
    val subtotal: Double,
    val subtotal_incl_tax: Double,
    val tax_amount: Int?= null,
    val total_due: Double,
    val total_item_count: Int?= null,
    val total_qty_ordered: Int?= null,
    val updated_at: String,
    val weight: Double
): Parcelable {
    override fun hashCode(): Int {
        val result = base_discount_amount.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Item

        if (base_currency_code != other.base_currency_code) return false
        if (base_discount_amount != other.base_discount_amount) return false
        if (base_discount_tax_compensation_amount != other.base_discount_tax_compensation_amount) return false
        if (base_grand_total != other.base_grand_total) return false
        if (base_shipping_amount != other.base_shipping_amount) return false
        if (base_shipping_discount_amount != other.base_shipping_discount_amount) return false
        if (base_shipping_discount_tax_compensation_amnt != other.base_shipping_discount_tax_compensation_amnt) return false
        if (base_shipping_incl_tax != other.base_shipping_incl_tax) return false
        if (base_shipping_tax_amount != other.base_shipping_tax_amount) return false
        if (base_subtotal != other.base_subtotal) return false
        if (base_subtotal_incl_tax != other.base_subtotal_incl_tax) return false
        if (base_tax_amount != other.base_tax_amount) return false
        if (base_to_global_rate != other.base_to_global_rate) return false
        if (base_to_order_rate != other.base_to_order_rate) return false
        if (base_total_due != other.base_total_due) return false
        if (billing_address != other.billing_address) return false
        if (billing_address_id != other.billing_address_id) return false
        if (created_at != other.created_at) return false
        if (customer_email != other.customer_email) return false
        if (customer_firstname != other.customer_firstname) return false
        if (customer_gender != other.customer_gender) return false
        if (customer_group_id != other.customer_group_id) return false
        if (customer_id != other.customer_id) return false
        if (customer_is_guest != other.customer_is_guest) return false
        if (customer_lastname != other.customer_lastname) return false
        if (customer_note_notify != other.customer_note_notify) return false
        if (discount_amount != other.discount_amount) return false
        if (discount_tax_compensation_amount != other.discount_tax_compensation_amount) return false
        if (entity_id != other.entity_id) return false
        if (extension_attributes != other.extension_attributes) return false
        if (global_currency_code != other.global_currency_code) return false
        if (grand_total != other.grand_total) return false
        if (increment_id != other.increment_id) return false
        if (is_virtual != other.is_virtual) return false
        if (items != other.items) return false
        if (order_currency_code != other.order_currency_code) return false
        if (payment != other.payment) return false
        if (protect_code != other.protect_code) return false
        if (quote_id != other.quote_id) return false
        if (remote_ip != other.remote_ip) return false
        if (shipping_amount != other.shipping_amount) return false
        if (shipping_description != other.shipping_description) return false
        if (shipping_discount_amount != other.shipping_discount_amount) return false
        if (shipping_discount_tax_compensation_amount != other.shipping_discount_tax_compensation_amount) return false
        if (shipping_incl_tax != other.shipping_incl_tax) return false
        if (shipping_tax_amount != other.shipping_tax_amount) return false
        if (state != other.state) return false
        if (status != other.status) return false
        if (status_histories != other.status_histories) return false
        if (store_currency_code != other.store_currency_code) return false
        if (store_id != other.store_id) return false
        if (store_name != other.store_name) return false
        if (store_to_base_rate != other.store_to_base_rate) return false
        if (store_to_order_rate != other.store_to_order_rate) return false
        if (subtotal != other.subtotal) return false
        if (subtotal_incl_tax != other.subtotal_incl_tax) return false
        if (tax_amount != other.tax_amount) return false
        if (total_due != other.total_due) return false
        if (total_item_count != other.total_item_count) return false
        if (total_qty_ordered != other.total_qty_ordered) return false
        if (updated_at != other.updated_at) return false
        if (weight != other.weight) return false

        return true
    }
}