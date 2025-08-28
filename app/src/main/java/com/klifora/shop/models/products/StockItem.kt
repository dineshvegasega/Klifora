package com.klifora.shop.models.products
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class StockItem(
    val backorders: Int,
    val enable_qty_increments: Boolean,
    val is_decimal_divided: Boolean,
    val is_in_stock: Boolean,
    val is_qty_decimal: Boolean,
    val item_id: Int,
    val low_stock_date: @RawValue Any,
    val manage_stock: Boolean,
    val max_sale_qty: Int,
    val min_qty: Int,
    val min_sale_qty: Int,
    val notify_stock_qty: Int,
    val product_id: Int,
    val qty: Int,
    val qty_increments: Int,
    val show_default_notification_message: Boolean,
    val stock_id: Int,
    val stock_status_changed_auto: Int,
    val use_config_backorders: Boolean,
    val use_config_enable_qty_inc: Boolean,
    val use_config_manage_stock: Boolean,
    val use_config_max_sale_qty: Boolean,
    val use_config_min_qty: Boolean,
    val use_config_min_sale_qty: Int,
    val use_config_notify_stock_qty: Boolean,
    val use_config_qty_increments: Boolean
): Parcelable{
    override fun hashCode(): Int {
        val result = item_id.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StockItem

        if (backorders != other.backorders) return false
        if (enable_qty_increments != other.enable_qty_increments) return false
        if (is_decimal_divided != other.is_decimal_divided) return false
        if (is_in_stock != other.is_in_stock) return false
        if (is_qty_decimal != other.is_qty_decimal) return false
        if (item_id != other.item_id) return false
        if (low_stock_date != other.low_stock_date) return false
        if (manage_stock != other.manage_stock) return false
        if (max_sale_qty != other.max_sale_qty) return false
        if (min_qty != other.min_qty) return false
        if (min_sale_qty != other.min_sale_qty) return false
        if (notify_stock_qty != other.notify_stock_qty) return false
        if (product_id != other.product_id) return false
        if (qty != other.qty) return false
        if (qty_increments != other.qty_increments) return false
        if (show_default_notification_message != other.show_default_notification_message) return false
        if (stock_id != other.stock_id) return false
        if (stock_status_changed_auto != other.stock_status_changed_auto) return false
        if (use_config_backorders != other.use_config_backorders) return false
        if (use_config_enable_qty_inc != other.use_config_enable_qty_inc) return false
        if (use_config_manage_stock != other.use_config_manage_stock) return false
        if (use_config_max_sale_qty != other.use_config_max_sale_qty) return false
        if (use_config_min_qty != other.use_config_min_qty) return false
        if (use_config_min_sale_qty != other.use_config_min_sale_qty) return false
        if (use_config_notify_stock_qty != other.use_config_notify_stock_qty) return false
        if (use_config_qty_increments != other.use_config_qty_increments) return false

        return true
    }
}