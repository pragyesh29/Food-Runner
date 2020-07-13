package com.pragyesh.thebeatles.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Restaurant")
data class RestaurantEntity (
    @PrimaryKey val Restaurant_id: Int,
    @ColumnInfo(name = "Restaurant_name") val RestaurantName: String,
    @ColumnInfo(name = "Restaurant_image")  val RestaurantImage: String,
    @ColumnInfo(name = "Restaurant_price") val RestaurantPrice: String,
    @ColumnInfo(name = "Restaurant_rating") val RestaurantRating: String
)