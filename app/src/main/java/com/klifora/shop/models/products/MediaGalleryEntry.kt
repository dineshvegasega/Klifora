package com.klifora.shop.models.products

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MediaGalleryEntry(
    val disabled: Boolean,
    val `file`: String,
    val id: Int,
//    val label: @RawValue Any,
    val media_type: String,
    val position: Int,
//    val types: @RawValue List<String>
): Parcelable{
    override fun hashCode(): Int {
        val result = id.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MediaGalleryEntry

        if (disabled != other.disabled) return false
        if (`file` != other.`file`) return false
        if (id != other.id) return false
        if (media_type != other.media_type) return false
        if (position != other.position) return false

        return true
    }
}