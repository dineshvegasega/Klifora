package com.klifora.shop.models

import com.google.android.gms.maps.model.LatLng

class ItemFranchise (
    val contact_person: String = "",
    val d_address: String = "",
    val d_city: String = "",
    val d_pincode: String = "",
    val d_state: String = "",
    val id: String = "",
    val mobile_number: String = "",
    val name: String = "",
    val register_address: String = "",
    var isSelected: Boolean = false,
    var latLng : LatLng = LatLng(0.0,0.0)
)