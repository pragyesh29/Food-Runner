package com.pragyesh.thebeatles.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantDao {

    @Insert
    fun insertRestaurant(restaurantEntity: RestaurantEntity)

    @Delete
    fun deleteRestaurant(restaurantEntity: RestaurantEntity)

    @Query("SELECT * FROM Restaurant")
    fun getAllRestaurant():List<RestaurantEntity>

    @Query("SELECT * FROM Restaurant WHERE restaurant_id = :RestaurantId")
    fun getRestaurantById(RestaurantId:String):RestaurantEntity
}