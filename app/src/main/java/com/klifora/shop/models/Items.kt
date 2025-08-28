package com.klifora.shop.models
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Items(
    val image : Int = 0,
    val id : Int = 0,
    val idWoman : Int = 0,
    val idKids : Int = 0,
    val parent_id : Int = 18,
    val name: String = "Item Name",
    var isSelected: Boolean = false,
    var isCollapse: Boolean = false,
    var isAll: Boolean = false,
    var quantity: Int = 0,
    val subCategory : ArrayList<Items> = ArrayList()
): Parcelable