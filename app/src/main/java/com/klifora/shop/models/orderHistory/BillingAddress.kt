package com.klifora.shop.models.orderHistory

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class BillingAddress(
    val address_type: String,
    val city: String,
    val country_id: String,
    val email: String,
    val entity_id: Int?= null,
    val firstname: String,
    val lastname: String,
    val parent_id: Int?= null,
    val postcode: String,
    val region: String,
    val region_code: String,
    val region_id: Int?= null,
    val street: @RawValue List<String> = ArrayList(),
    val telephone: String
): Parcelable{
    override fun hashCode(): Int {
        val result = entity_id.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BillingAddress

        if (address_type != other.address_type) return false
        if (city != other.city) return false
        if (country_id != other.country_id) return false
        if (email != other.email) return false
        if (entity_id != other.entity_id) return false
        if (firstname != other.firstname) return false
        if (lastname != other.lastname) return false
        if (parent_id != other.parent_id) return false
        if (postcode != other.postcode) return false
        if (region != other.region) return false
        if (region_code != other.region_code) return false
        if (region_id != other.region_id) return false
        if (street != other.street) return false
        if (telephone != other.telephone) return false

        return true
    }
}