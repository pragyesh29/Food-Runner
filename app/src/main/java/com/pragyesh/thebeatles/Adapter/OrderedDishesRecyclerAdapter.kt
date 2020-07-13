package com.pragyesh.thebeatles.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pragyesh.thebeatles.R
import com.pragyesh.thebeatles.model.Dish

class OrderedDishesRecyclerAdapter(private val itemList:ArrayList<Dish>):RecyclerView.Adapter<OrderedDishesRecyclerAdapter.OrderedDishesViewHolder>() {
    class OrderedDishesViewHolder(view: View):RecyclerView.ViewHolder(view){
        val txtDishName: TextView = view.findViewById(R.id.txtDishName)
        val txtDishPrice: TextView = view.findViewById(R.id.txtDishPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderedDishesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_order_history_single_dish, parent, false)
        return OrderedDishesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: OrderedDishesViewHolder, position: Int) {
        val item = itemList[position]
        holder.txtDishName.text = item.name
        holder.txtDishPrice.text = item.cost
    }
}