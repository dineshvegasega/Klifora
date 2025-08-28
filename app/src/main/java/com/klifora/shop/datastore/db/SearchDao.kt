package com.klifora.shop.datastore.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SearchDao {

    @Query("SELECT DISTINCT _id, search_name, `current_time` FROM search ORDER BY `current_time` DESC")
    suspend fun getAll(): List<SearchModel>

    @Query("SELECT * FROM search WHERE _id IN (:ids)")
    suspend fun loadAllByIds(ids: IntArray): List<SearchModel>

    @Query("SELECT * FROM search WHERE search_name LIKE :search_name")
    suspend fun findBySearchName(search_name: String): SearchModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: SearchModel)

    @Delete
    suspend fun delete(user: SearchModel)


}