package com.pragyesh.thebeatles.Adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.pragyesh.thebeatles.Activity.MenuActivity
import com.pragyesh.thebeatles.Database.RestaurantDatabase
import com.pragyesh.thebeatles.Database.RestaurantEntity
import com.pragyesh.thebeatles.Fragment.DashboardFragment
import com.pragyesh.thebeatles.R
import com.pragyesh.thebeatles.model.Restaurant
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_dashboard.view.*

class DashboardRecyclerAdapter(val context: Context, private val itemList:ArrayList<Restaurant>): RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_dashboard_single_row, parent,false)
        return DashboardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val restaurant = itemList[position]
        holder.txtRestaurantName.text = restaurant.restaurantName
        holder.txtRestaurantCost.text = "Rs. ${restaurant.restaurantCost}/person"
        holder.txtRestaurantRating.text = restaurant.restaurantRating
        Picasso.get().load(restaurant.restaurantImage).error(R.drawable.app_logo).into(holder.imgRestaurantImage)

        val restaurantEntity = RestaurantEntity(
            restaurant.restaurantId?.toInt(),
            restaurant.restaurantName,
            restaurant.restaurantImage,
            restaurant.restaurantCost,
            restaurant.restaurantRating
        )

        val checkFav = DashboardFragment.DBAsyncTask(context,restaurantEntity,1).execute()
        val isFav = checkFav.get()
        if(isFav) holder.imgAddToFavorites.setImageResource(R.drawable.ic_favorite)
        else holder.imgAddToFavorites.setImageResource(R.drawable.ic_add_to_favorite)

        holder.llContent.setOnClickListener{
            val intent = Intent(context, MenuActivity::class.java)
            intent.putExtra("id", restaurant.restaurantId)
            intent.putExtra("restaurantName",restaurant.restaurantName)
            context.startActivity(intent)
        }

        holder.imgAddToFavorites.setOnClickListener{
            if(!DashboardFragment.DBAsyncTask(context,restaurantEntity,1).execute().get()){
                val async = DashboardFragment.DBAsyncTask(context,restaurantEntity,2).execute()
                val result = async.get()
                if(result){
                    Toast.makeText(context,"Restaurant added to Favorites", Toast.LENGTH_SHORT).show()
                    holder.imgAddToFavorites.setImageResource(R.drawable.ic_favorite)
                }else Toast.makeText(context,"Some error occurred", Toast.LENGTH_SHORT).show()
            }else{
                val async = DashboardFragment.DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()
                if(result){
                    Toast.makeText(context,"Restaurant removed from Favorites", Toast.LENGTH_SHORT).show()
                    holder.imgAddToFavorites.setImageResource(R.drawable.ic_add_to_favorite)
                }else Toast.makeText(context,"Some error occurred", Toast.LENGTH_SHORT).show()
            }
        }
    }
    class DashboardViewHolder(view: View): RecyclerView.ViewHolder(view){
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtRestaurantCost: TextView = view.findViewById(R.id.txtPrice)
        val imgRestaurantImage: ImageView = view.findViewById(R.id.imgRestaurantImage)
        val txtRestaurantRating: TextView = view.findViewById(R.id.txtRating)
        val imgAddToFavorites: ImageView = view.findViewById(R.id.imgAddToFavorites)
    }
}