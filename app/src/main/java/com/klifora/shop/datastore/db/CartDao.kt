package com.klifora.shop.datastore.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CartDao {
    @Query("SELECT DISTINCT * FROM cart ORDER BY `current_time` DESC")
    suspend fun getAll(): List<CartModel>

    @Query("SELECT * FROM cart WHERE _id IN (:ids)")
    suspend fun loadAllByIds(ids: IntArray): List<CartModel>

    @Query("SELECT * FROM cart WHERE name LIKE :name")
    suspend fun findBySearchName(name: String): CartModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: CartModel)

    @Delete
    suspend fun delete(user: CartModel)

    @Query("DELETE FROM cart WHERE product_id = :product_id")
    suspend fun deleteById(product_id: Int)

    @Query("UPDATE cart set quantity = :quantity WHERE product_id = :product_id")
    suspend fun updateById(product_id: Int, quantity: Int)

}