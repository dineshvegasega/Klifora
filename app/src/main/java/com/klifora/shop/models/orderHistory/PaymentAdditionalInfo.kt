package com.klifora.shop.models.orderHistory
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentAdditionalInfo(
    val key: String,
    val value: String
): Parcelable {
    override fun hashCode(): Int {
        val result = key.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PaymentAdditionalInfo

        if (key != other.key) return false
        if (value != other.value) return false

        return true
    }
}