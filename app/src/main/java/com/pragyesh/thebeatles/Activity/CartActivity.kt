package com.pragyesh.thebeatles.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.pragyesh.thebeatles.Adapter.CartRecyclerAdapter
import com.pragyesh.thebeatles.Database.DishDatabase
import com.pragyesh.thebeatles.Database.DishEntity
import com.pragyesh.thebeatles.R
import com.pragyesh.thebeatles.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    lateinit var txtOrderingFrom: TextView
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var recyclerCart: RecyclerView
    lateinit var btnPlaceOrder: Button
    lateinit var recylerAdapter: CartRecyclerAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var sharedPreferences: SharedPreferences
    var sum = 0
    var dbDishList = listOf<DishEntity>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        toolbar = findViewById(R.id.toolbar)
        txtOrderingFrom = findViewById(R.id.txtorderingFrom)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        layoutManager = LinearLayoutManager(this@CartActivity)
        recyclerCart = findViewById(R.id.recyclerCart)
        txtOrderingFrom.text = "Ordering From: " + intent.getStringExtra("restaurantName")
        setUpToolbar()

        dbDishList = RetrieveCartItems(this@CartActivity, 1).execute().get()

        val jsonParams = JSONObject()
        recylerAdapter = CartRecyclerAdapter(this@CartActivity , dbDishList)
        recyclerCart.adapter = recylerAdapter
        recyclerCart.layoutManager = layoutManager

        val jsonFoodArray = JSONArray()
        for (i in dbDishList){
            sum += i.DishCost.toInt()
            jsonFoodArray.put(JSONObject().put("food_item_id", i.Dish_id))
        }

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        jsonParams.put("user_id",sharedPreferences.getString("user_id",""))
        jsonParams.put("restaurant_id",intent.getStringExtra("id"))
        jsonParams.put("total_cost", sum.toString())
        jsonParams.put("food",jsonFoodArray)
        println("json will be : $jsonParams")

        btnPlaceOrder.text = "Place Order(Total Rs. $sum)"

        btnPlaceOrder.setOnClickListener{
            val queue = Volley.newRequestQueue(this@CartActivity)
            val url = "http://13.235.250.119/v2/place_order/fetch_result/"
            if(ConnectionManager().checkConnectivity(this@CartActivity)){
                val jsonRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                    if(it.getJSONObject("data").getBoolean("success")){
                        try{
                            RetrieveCartItems(this@CartActivity, 2).execute().get()
                        }catch (e:Exception){
                            Toast.makeText(this@CartActivity, "", Toast.LENGTH_SHORT).show()
                        }
                        Toast.makeText(this@CartActivity, "Order Placed successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@CartActivity, OrderConfirmationActivity::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(this@CartActivity, "Some unexpected error occurred", Toast.LENGTH_SHORT).show()
                    }
                },Response.ErrorListener {
                    Toast.makeText(this@CartActivity, "Volley error occurred", Toast.LENGTH_SHORT).show()
                }){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "c5b6606c9aa5af"
                        return headers
                    }
                }
                queue.add(jsonRequest)
            }else{
                val dialog = AlertDialog.Builder(this@CartActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection Not Found")
                dialog.setPositiveButton("Open Settings"){ _, _ ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit"){ _, _ ->
                    ActivityCompat.finishAffinity(this@CartActivity)
                }
                dialog.create().show()
            }
        }
    }
    private fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title = intent.getStringExtra("restaurantName")
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    class RetrieveCartItems(val context: Context, private val mode:Int):AsyncTask<Void,Void,List<DishEntity>>(){
        private val db = Room.databaseBuilder(context, DishDatabase::class.java,"dish-db").build()
        override fun doInBackground(vararg params: Void?): List<DishEntity> {
            when(mode){
                1 -> { return db.dishDao().getAllDish()}
                2 -> {db.dishDao().deleteAllRows()}
            }
            return db.dishDao().getAllDish()
        }
    }
}
