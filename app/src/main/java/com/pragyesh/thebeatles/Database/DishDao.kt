package com.pragyesh.thebeatles.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DishDao {
    @Insert
    fun insertDish(dishEntity: DishEntity)

    @Delete
    fun deleteDish(dishEntity: DishEntity)

    @Query("SELECT * FROM Dish")
    fun getAllDish():List<DishEntity>

    @Query("SELECT * FROM Dish WHERE dish_id = :dishId")
    fun getDishById(dishId:String):DishEntity

    @Query("DELETE FROM Dish")
    fun deleteAllRows()
}