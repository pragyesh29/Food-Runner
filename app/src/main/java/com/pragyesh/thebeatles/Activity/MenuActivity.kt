package com.pragyesh.thebeatles.Activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.pragyesh.thebeatles.Adapter.MenuRecyclerAdapter
import com.pragyesh.thebeatles.R
import com.pragyesh.thebeatles.model.Menu
import com.pragyesh.thebeatles.util.ConnectionManager

class MenuActivity : AppCompatActivity() {

    lateinit var recyclerMenu: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: MenuRecyclerAdapter
    lateinit var txtChooseMessage: TextView
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout
    lateinit var toolbar: Toolbar
    lateinit var btnProceedToCart: Button
    lateinit var sharedPreferences: SharedPreferences
    var menuInfoList = ArrayList<Menu>()

    var restaurantId: String? = "-1"
    var restaurantName: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        progressBar = findViewById(R.id.progressBar)
        progressLayout = findViewById(R.id.progressLayout)
        txtChooseMessage = findViewById(R.id.txtChooseMessage)
        recyclerMenu = findViewById(R.id.recyclerMenu)
        toolbar = findViewById(R.id.toolbar)
        btnProceedToCart = findViewById(R.id.btnProceedToCart)
        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        layoutManager = LinearLayoutManager(this@MenuActivity)
        setUpToolbar()

        if(intent!=null){
            restaurantId = intent.getStringExtra("id")
            restaurantName = intent.getStringExtra("restaurantName")
        }else{
            finish()
            Toast.makeText(this@MenuActivity, "Some unexpected error occurred", Toast.LENGTH_SHORT).show()
        }

        if(restaurantId == "-1"){
            finish()
            Toast.makeText(this@MenuActivity, "Some unexpected error occurred", Toast.LENGTH_SHORT).show()
        }
        val request = Volley.newRequestQueue(this@MenuActivity)

        if(ConnectionManager().checkConnectivity(this@MenuActivity)){

            val jsonRequest = object: JsonObjectRequest(Request.Method.GET,"http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId", null, Response.Listener {
                try{
                    val success = it.getJSONObject("data").getBoolean("success")
                    if(success){
                        val data = it.getJSONObject("data").getJSONArray("data")
                        progressLayout.visibility = View.GONE
                        for(i in 0 until data.length()){
                            val singleItem = data.getJSONObject(i)
                            val menuObject = Menu(
                                singleItem.getString("id"),
                                singleItem.getString("name"),
                                singleItem.getString("cost_for_one"),
                                singleItem.getString("restaurant_id")
                            )
                            menuInfoList.add(menuObject)
                            recyclerAdapter = MenuRecyclerAdapter(this@MenuActivity, menuInfoList)
                            recyclerMenu.adapter = recyclerAdapter
                            recyclerMenu.layoutManager = layoutManager

                        }
                    }else{
                        Toast.makeText(this@MenuActivity, "Some unexpected error occurred", Toast.LENGTH_SHORT).show()
                    }
                }catch (e: Exception){
                    Toast.makeText(this@MenuActivity, "Some unexpected error occurred", Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener {
                Toast.makeText(this@MenuActivity, "Volley error occurred", Toast.LENGTH_SHORT).show()
            }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "c5b6606c9aa5af"
                    return headers
                }
            }
            request.add(jsonRequest)
        }else{
            val dialog = AlertDialog.Builder(this@MenuActivity)
            dialog.setTitle("Error")
            dialog.setMessage("No Internet Connection Found")
            dialog.setPositiveButton("Open Settings"){_,_ ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit"){_, _ ->
                ActivityCompat.finishAffinity(this@MenuActivity)
            }
        }

        btnProceedToCart.setOnClickListener{
            val intent = Intent(this@MenuActivity, CartActivity::class.java)
            intent.putExtra("id",restaurantId)
            intent.putExtra("restaurantName", restaurantName)
            startActivity(intent)
        }
    }

    fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title = intent.getStringExtra("restaurantName")
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        CartActivity.RetrieveCartItems(this@MenuActivity, 2).execute()
        if(item.itemId == android.R.id.home) onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}
