package com.klifora.shop.models.products
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ExtensionAttributes(
    val category_links:  @RawValue List<CategoryLink>,
    val configurable_product_links: @RawValue List<Int>,
    val configurable_product_options: @RawValue List<ConfigurableProductOption>,
    val stock_item: @RawValue StockItem,
    val website_ids: @RawValue List<Int>
): Parcelable{
    override fun hashCode(): Int {
        val result = stock_item.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExtensionAttributes

        if (category_links != other.category_links) return false
        if (configurable_product_links != other.configurable_product_links) return false
        if (configurable_product_options != other.configurable_product_options) return false
        if (stock_item != other.stock_item) return false
        if (website_ids != other.website_ids) return false

        return true
    }
}