package com.klifora.shop.datastore.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "search", indices = [Index(value = ["search_name"], unique = true)])
data class SearchModel (
    @PrimaryKey(autoGenerate = true) val _id: Int = 0,
    @ColumnInfo(name = "search_name") val search_name: String?,
    @ColumnInfo(name = "current_time") val currentTime: Long?,
)