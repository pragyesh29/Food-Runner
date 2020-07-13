package com.pragyesh.thebeatles.Adapter

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.gson.Gson
import com.pragyesh.thebeatles.Activity.CartActivity
import com.pragyesh.thebeatles.Activity.MenuActivity
import com.pragyesh.thebeatles.Database.DishDatabase
import com.pragyesh.thebeatles.Database.DishEntity
import com.pragyesh.thebeatles.Database.RestaurantDatabase
import com.pragyesh.thebeatles.Fragment.DashboardFragment
import com.pragyesh.thebeatles.R
import com.pragyesh.thebeatles.model.Dish
import com.pragyesh.thebeatles.model.Menu
import kotlinx.android.synthetic.main.activity_menu.view.*
import kotlinx.android.synthetic.main.recycler_menu_single_row.view.*

class MenuRecyclerAdapter(val context: Context, private val itemList:ArrayList<Menu>):RecyclerView.Adapter<MenuRecyclerAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_menu_single_row, parent, false)
        return MenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val dish = itemList[position]
        holder.txtDishName.text = dish.dishName
        holder.txtPrice.text = "Rs.${dish.dishPrice}"
        holder.txtSerialNumber.text = (position+1).toString()
        
        holder.btnAddToCart.text = "Add"
        holder.btnAddToCart.setBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimary))

        holder.btnAddToCart.setOnClickListener{
            val dishItem = DishEntity(
                dish.dishId.toInt(),
                dish.dishName,
                dish.dishPrice
            )

            if(!DBAsyncDishTask(context,dishItem,1).execute().get()){
                val async = DBAsyncDishTask(context,dishItem,2).execute()
                if(async.get()){
                    holder.btnAddToCart.setBackgroundColor(ContextCompat.getColor(context,R.color.colorBtnBackground))
                    holder.btnAddToCart.text = "Remove"
                    Toast.makeText(context, "Added to cart successfully", Toast.LENGTH_SHORT).show()
                }else Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
            }else{
                val async = DBAsyncDishTask(context,dishItem,3).execute()
                if(async.get()){
                    holder.btnAddToCart.setBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimary))
                    holder.btnAddToCart.text = "Add"
                    Toast.makeText(context, "Removed from cart successfully", Toast.LENGTH_SHORT).show()
                }else Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
            }
        }
    }

    class MenuViewHolder(view:View):RecyclerView.ViewHolder(view){
        val txtSerialNumber:TextView = view.findViewById(R.id.txtSerialNumber)
        val txtDishName:TextView = view.findViewById(R.id.txtNameOfDish)
        val txtPrice:TextView = view.findViewById(R.id.txtDishPrice)
        val btnAddToCart: Button = view.findViewById(R.id.btnAddToCart)
    }

    class DBAsyncDishTask(val context: Context, private val dishEntity: DishEntity, private val mode:Int):AsyncTask<Void,Void,Boolean>(){
        private val db = Room.databaseBuilder(context, DishDatabase::class.java,"dish-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            when(mode){
                1 -> {
                    val dish:DishEntity? = db.dishDao().getDishById(dishEntity.Dish_id.toString())
                    db.close()
                    return dish != null
                }
                2 -> {
                    db.dishDao().insertDish(dishEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.dishDao().deleteDish(dishEntity)
                    db.close()
                    return true
                }
                4 -> db.dishDao().deleteAllRows()
            }
            return false
        }
    }
}