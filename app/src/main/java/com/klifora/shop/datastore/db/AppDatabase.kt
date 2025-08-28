package com.klifora.shop.datastore.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(exportSchema = true, entities = [SearchModel::class, CartModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun searchDao(): SearchDao
    abstract fun cartDao(): CartDao
}