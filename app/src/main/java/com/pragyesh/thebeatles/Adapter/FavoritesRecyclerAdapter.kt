package com.pragyesh.thebeatles.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pragyesh.thebeatles.Activity.MenuActivity
import com.pragyesh.thebeatles.Database.RestaurantEntity
import com.pragyesh.thebeatles.R
import com.pragyesh.thebeatles.model.Restaurant
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_favorites_single_row.view.*

class FavoritesRecyclerAdapter(val context:Context, private val itemList:List<RestaurantEntity>):RecyclerView.Adapter<FavoritesRecyclerAdapter.FavoritesViewHolder>() {
    class FavoritesViewHolder(view:View) : RecyclerView.ViewHolder(view){
        val llContentFav: LinearLayout = view.findViewById(R.id.llContentFav)
        val imgRestaurantImage:ImageView = view.findViewById(R.id.imgRestaurantImage)
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtRestaurantCost: TextView = view.findViewById(R.id.txtPrice)
        val txtRestaurantRating: TextView = view.findViewById(R.id.txtRating)
        val imgAddToFavorites: ImageView = view.findViewById(R.id.imgAddToFavorites)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_favorites_single_row, parent, false)
        return FavoritesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val restaurant = itemList[position]
        Picasso.get().load(restaurant.RestaurantImage).error(R.drawable.app_logo).into(holder.imgRestaurantImage)
        holder.txtRestaurantName.text = restaurant.RestaurantName
        holder.txtRestaurantCost.text = restaurant.RestaurantPrice
        holder.txtRestaurantRating.text = restaurant.RestaurantRating
        holder.imgAddToFavorites.setImageResource(R.drawable.ic_favorite)
        holder.llContentFav.setOnClickListener{
            val intent = Intent(context, MenuActivity::class.java)
            intent.putExtra("id", restaurant.Restaurant_id.toString())
            intent.putExtra("restaurantName",restaurant.RestaurantName)
            context.startActivity(intent)
        }
    }
}