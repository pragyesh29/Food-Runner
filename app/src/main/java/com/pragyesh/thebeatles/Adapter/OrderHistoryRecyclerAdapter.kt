package com.pragyesh.thebeatles.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pragyesh.thebeatles.R
import com.pragyesh.thebeatles.model.OrderHistory
import kotlinx.android.synthetic.main.recycler_order_history_single_restaurant.view.*

class OrderHistoryRecyclerAdapter(val context: Context, private val itemList:ArrayList<OrderHistory>): RecyclerView.Adapter<OrderHistoryRecyclerAdapter.OrderHistoryViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_order_history_single_restaurant, parent, false)
        return OrderHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: OrderHistoryRecyclerAdapter.OrderHistoryViewHolder, position: Int) {
        val item = itemList[position]
        holder.txtRestaurantName.text = item.restaurant_name
        holder.txtOrderDate.text = item.order_placed_at.split(' ')[0]
        holder.recyclerOrderHistory.adapter = OrderedDishesRecyclerAdapter(item.food_items)
        holder.recyclerOrderHistory.layoutManager = LinearLayoutManager(context)
    }

    class OrderHistoryViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtOrderDate: TextView = view.findViewById(R.id.txtOrderDate)
        val recyclerOrderHistory:RecyclerView = view.recyclerDishes
    }
}