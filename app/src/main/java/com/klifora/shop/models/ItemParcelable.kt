package com.klifora.shop.models
import android.os.Parcelable
import com.klifora.shop.models.products.MediaGalleryEntry
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ItemParcelable(
    val item: @RawValue ArrayList<MediaGalleryEntry> = ArrayList(),
    var position: Int = -1
): Parcelable {
    override fun hashCode(): Int {
        val result = position.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemParcelable

        if (item != other.item) return false
        if (position != other.position) return false

        return true
    }
}