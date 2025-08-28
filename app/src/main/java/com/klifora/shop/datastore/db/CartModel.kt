package com.klifora.shop.datastore.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(tableName = "cart", indices = [Index(value = ["product_id"], unique = true)])
@Parcelize
data class CartModel(
    @PrimaryKey(autoGenerate = true) val _id: Int = 0,
    @ColumnInfo(name = "product_id") val product_id: Int? = 0,
    @ColumnInfo(name = "name") val name: String? = "",
    @ColumnInfo(name = "price") val price: Double? = 0.0,
    @ColumnInfo(name = "isSelected") val isSelected: Boolean? = false,
    @ColumnInfo(name = "isCollapse") val isCollapse: Boolean? = false,
    @ColumnInfo(name = "isAll") val isAll: Boolean? = false,
    @ColumnInfo(name = "quantity") var quantity: Int = 0,
    @ColumnInfo(name = "sku") var sku: String = "",
    @ColumnInfo(name = "color") var color: String = "",
    @ColumnInfo(name = "material_type") var material_type: String = "",
    @ColumnInfo(name = "purity") var purity: String = "",
    @ColumnInfo(name = "size") var size: String = "",
    @ColumnInfo(name = "current_time") val currentTime: Long? = 0,
) : Parcelable
