package com.pragyesh.thebeatles.Adapter

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.pragyesh.thebeatles.Database.DishDatabase
import com.pragyesh.thebeatles.Database.DishEntity
import com.pragyesh.thebeatles.R

class CartRecyclerAdapter (val context: Context, private val itemList:List<DishEntity>):RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>(){
    class CartViewHolder(view: View):RecyclerView.ViewHolder(view){
        val txtDishName:TextView = view.findViewById(R.id.txtDishName)
        val txtDishPrice:TextView = view.findViewById(R.id.txtDishPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_cart_single_row, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val dish = itemList[position]
        holder.txtDishName.text = dish.DishName
        holder.txtDishPrice.text = dish.DishCost
    }

}
