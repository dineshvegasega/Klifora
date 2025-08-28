package com.klifora.shop.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemFaq(
    val que: String = "Item Name",
    val ans: String = "Item Name"
): Parcelable {
    override fun hashCode(): Int {
        val result = que.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemFaq

        if (que != other.que) return false
        if (ans != other.ans) return false

        return true
    }
}