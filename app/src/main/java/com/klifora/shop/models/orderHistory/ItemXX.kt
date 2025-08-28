package com.klifora.shop.models.orderHistory

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemXX(
    val amount_refunded: Int ?= null,
    val base_amount_refunded: Int ?= null,
    val base_discount_amount: Int?= null,
    val base_discount_invoiced: Int?= null,
    val base_discount_tax_compensation_amount: Int?= null,
    val base_original_price: Double,
    val base_price: Double,
    val base_price_incl_tax: Double,
    val base_row_invoiced: Int?= null,
    val base_row_total: Double,
    val base_row_total_incl_tax: Double,
    val base_tax_amount: Int?= null,
    val base_tax_invoiced: Double?= null,
    val created_at: String,
    val discount_amount: Int?= null,
    val discount_invoiced: Int?= null,
    val discount_percent: Int?= null,
    val discount_tax_compensation_amount: Int?= null,
    val free_shipping: Int?= null,
    val is_qty_decimal: Int?= null,
    val is_virtual: Int?= null,
    val item_id: Int?= null,
    val name: String,
    val no_discount: Int?= null,
    val order_id: Int?= null,
    val original_price: Double,
    val price: Double,
    val price_incl_tax: Double,
    val product_id: Int?= null,
    val product_type: String,
    val qty_canceled: Int?= null,
    val qty_invoiced: Int?= null,
    val qty_ordered: Int?= null,
    val qty_refunded: Int?= null,
    val qty_shipped: Int?= null,
    val quote_item_id: Int?= null,
    val row_invoiced: Int?= null,
    val row_total: Double,
    val row_total_incl_tax: Double,
    val row_weight: Double,
    val sku: String,
    val store_id: Int?= null,
    val tax_amount: Int?= null,
    val tax_invoiced: Int?= null,
    val tax_percent: Int?= null,
    val updated_at: String,
    val weight: Double
): Parcelable{
    override fun hashCode(): Int {
        val result = amount_refunded.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemXX

        if (amount_refunded != other.amount_refunded) return false
        if (base_amount_refunded != other.base_amount_refunded) return false
        if (base_discount_amount != other.base_discount_amount) return false
        if (base_discount_invoiced != other.base_discount_invoiced) return false
        if (base_discount_tax_compensation_amount != other.base_discount_tax_compensation_amount) return false
        if (base_original_price != other.base_original_price) return false
        if (base_price != other.base_price) return false
        if (base_price_incl_tax != other.base_price_incl_tax) return false
        if (base_row_invoiced != other.base_row_invoiced) return false
        if (base_row_total != other.base_row_total) return false
        if (base_row_total_incl_tax != other.base_row_total_incl_tax) return false
        if (base_tax_amount != other.base_tax_amount) return false
        if (base_tax_invoiced != other.base_tax_invoiced) return false
        if (created_at != other.created_at) return false
        if (discount_amount != other.discount_amount) return false
        if (discount_invoiced != other.discount_invoiced) return false
        if (discount_percent != other.discount_percent) return false
        if (discount_tax_compensation_amount != other.discount_tax_compensation_amount) return false
        if (free_shipping != other.free_shipping) return false
        if (is_qty_decimal != other.is_qty_decimal) return false
        if (is_virtual != other.is_virtual) return false
        if (item_id != other.item_id) return false
        if (name != other.name) return false
        if (no_discount != other.no_discount) return false
        if (order_id != other.order_id) return false
        if (original_price != other.original_price) return false
        if (price != other.price) return false
        if (price_incl_tax != other.price_incl_tax) return false
        if (product_id != other.product_id) return false
        if (product_type != other.product_type) return false
        if (qty_canceled != other.qty_canceled) return false
        if (qty_invoiced != other.qty_invoiced) return false
        if (qty_ordered != other.qty_ordered) return false
        if (qty_refunded != other.qty_refunded) return false
        if (qty_shipped != other.qty_shipped) return false
        if (quote_item_id != other.quote_item_id) return false
        if (row_invoiced != other.row_invoiced) return false
        if (row_total != other.row_total) return false
        if (row_total_incl_tax != other.row_total_incl_tax) return false
        if (row_weight != other.row_weight) return false
        if (sku != other.sku) return false
        if (store_id != other.store_id) return false
        if (tax_amount != other.tax_amount) return false
        if (tax_invoiced != other.tax_invoiced) return false
        if (tax_percent != other.tax_percent) return false
        if (updated_at != other.updated_at) return false
        if (weight != other.weight) return false

        return true
    }
}