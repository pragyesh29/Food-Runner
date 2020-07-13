package com.pragyesh.thebeatles.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Dish")
data class DishEntity (
    @PrimaryKey val Dish_id:Int,
    @ColumnInfo(name = "Dish_name") val DishName: String,
    @ColumnInfo(name = "Dish_cost") val DishCost: String
)