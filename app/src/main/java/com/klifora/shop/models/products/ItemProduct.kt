package com.klifora.shop.models.products
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ItemProduct(
    val attribute_set_id: Int,
    val created_at: String,
    val custom_attributes: @RawValue ArrayList<CustomAttribute>,
    val extension_attributes: @RawValue ExtensionAttributes,
    val id: Int,
    val media_gallery_entries: @RawValue ArrayList<MediaGalleryEntry> = ArrayList(),
    val name: String,
    val options: @RawValue List<Any>,
    val price: Double = 0.0,
    val product_links: @RawValue List<Any>,
    val sku: String,
    val status: Int,
    val tier_prices: @RawValue List<Any>,
    val type_id: String,
    val updated_at: String,
    val visibility: Int,
    val weight: Double = 0.0,
    var isSelected: Boolean = false,
    var isCollapse: Boolean = false,
    var isAll: Boolean = false,
    var quantity: Int = 0,
): Parcelable{
    override fun hashCode(): Int {
        val result = attribute_set_id.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemProduct

        if (attribute_set_id != other.attribute_set_id) return false
        if (created_at != other.created_at) return false
        if (custom_attributes != other.custom_attributes) return false
        if (extension_attributes != other.extension_attributes) return false
        if (id != other.id) return false
        if (media_gallery_entries != other.media_gallery_entries) return false
        if (name != other.name) return false
        if (options != other.options) return false
        if (price != other.price) return false
        if (product_links != other.product_links) return false
        if (sku != other.sku) return false
        if (status != other.status) return false
        if (tier_prices != other.tier_prices) return false
        if (type_id != other.type_id) return false
        if (updated_at != other.updated_at) return false
        if (visibility != other.visibility) return false
        if (weight != other.weight) return false
        if (isSelected != other.isSelected) return false
        if (isCollapse != other.isCollapse) return false
        if (isAll != other.isAll) return false
        if (quantity != other.quantity) return false

        return true
    }
}