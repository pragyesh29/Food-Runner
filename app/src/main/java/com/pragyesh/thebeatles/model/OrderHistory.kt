package com.pragyesh.thebeatles.model

data class OrderHistory (
    val order_id: String,
    val restaurant_name: String,
    val total_cost: String,
    val order_placed_at: String,
    val food_items: ArrayList<Dish>
)