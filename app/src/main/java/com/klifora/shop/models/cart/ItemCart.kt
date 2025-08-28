package com.klifora.shop.models.cart

data class ItemCart(
    val billing_address: BillingAddress,
    val created_at: String,
    val currency: Currency,
    val customer: Customer,
    val customer_is_guest: Boolean,
    val customer_note_notify: Boolean,
    val customer_tax_class_id: Int,
    val extension_attributes: ExtensionAttributesX,
    val id: Int,
    val is_active: Boolean,
    val is_virtual: Boolean,
    val items: ArrayList<ItemCartModel> = ArrayList(),
    val items_count: Int,
    val items_qty: Int,
    val orig_order_id: Int,
    val store_id: Int,
    val updated_at: String
)