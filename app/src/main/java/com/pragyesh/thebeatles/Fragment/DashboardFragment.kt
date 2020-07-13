package com.pragyesh.thebeatles.Fragment

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.pragyesh.thebeatles.Adapter.DashboardRecyclerAdapter
import com.pragyesh.thebeatles.Database.RestaurantDatabase
import com.pragyesh.thebeatles.Database.RestaurantEntity

import com.pragyesh.thebeatles.R
import com.pragyesh.thebeatles.model.Restaurant
import com.pragyesh.thebeatles.util.ConnectionManager
import kotlinx.android.synthetic.*
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DashboardFragment : Fragment() {

    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: DashboardRecyclerAdapter
    var restaurentInfoList = ArrayList<Restaurant>()
    var ratingComparator = Comparator<Restaurant>{restaurant1, restaurant2 ->
        if(restaurant1.restaurantRating.compareTo(restaurant2.restaurantRating,true) == 0){
            restaurant1.restaurantName.compareTo(restaurant2.restaurantName,true)
        }else{
            restaurant1.restaurantRating.compareTo(restaurant2.restaurantRating,true)
        }
    }
    var costComparator = Comparator<Restaurant>{restaurant1, restaurant2 ->
        if(restaurant1.restaurantCost.compareTo(restaurant2.restaurantCost, true) == 0){
            restaurant1.restaurantCost.compareTo(restaurant2.restaurantCost, true)
        }else{
            restaurant1.restaurantCost.compareTo(restaurant2.restaurantCost, true)
        }
    }

    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        setHasOptionsMenu(true)
        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        layoutManager = LinearLayoutManager(activity)

        val request = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
        if(ConnectionManager().checkConnectivity(activity as Context)){
            val jsonObjectRequest = object: JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                try{
                    progressLayout.visibility = View.GONE
                    val success = it.getJSONObject("data").getBoolean("success")
                    if(success){
                        val data = it.getJSONObject("data").getJSONArray("data")
                        for(i in 0 until data.length()){
                            val restaurantJSONObject = data.getJSONObject(i)
                            val restaurantObject = Restaurant(
                                restaurantJSONObject.getString("id"),
                                restaurantJSONObject.getString("name"),
                                restaurantJSONObject.getString("cost_for_one"),
                                restaurantJSONObject.getString("rating"),
                                restaurantJSONObject.getString("image_url")
                            )

                            restaurentInfoList.add(restaurantObject)
                            recyclerAdapter = DashboardRecyclerAdapter(activity as Context, restaurentInfoList)
                            recyclerDashboard.adapter = recyclerAdapter
                            recyclerDashboard.layoutManager = layoutManager
                        }
                    }else {
                        Toast.makeText(activity as Context, "Some error Occoured", Toast.LENGTH_SHORT).show()
                    }
                }catch (e: JSONException){
                    Toast.makeText(activity as Context, "Some unexpected error Occurred!!!", Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener {
                if(activity != null) Toast.makeText(activity as Context, "Volley error occurred!!!", Toast.LENGTH_SHORT).show()
            }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "c5b6606c9aa5af"
                    return headers
                }
            }
            request.add(jsonObjectRequest)
        }else{
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings"){ _, _ ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit"){ _, _ ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create().show()
        }
        return view
    }

    class DBAsyncTask(val context: Context, private val restaurantEntity: RestaurantEntity, private val mode: Int):AsyncTask<Void, Void, Boolean>(){
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            when(mode){
                1 -> {
                    val restaurant:RestaurantEntity? = db.restaurantDao().getRestaurantById(restaurantEntity.Restaurant_id.toString())
                    db.close()
                    return restaurant != null
                }
                2 -> {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboard, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.actionSort){
            val dialog = AlertDialog.Builder(context as Context)
            dialog.setTitle("Sort By?")
            var selectedItem = -1
            val items = arrayOf("Cost(Low to High)", "Cost(High to Low)", "Rating")
            dialog.setSingleChoiceItems(
                items,
                selectedItem
            ) { _: DialogInterface?, item: Int -> selectedItem = item }
            dialog.setPositiveButton("OK"){_,_ ->
                println("selcected is $selectedItem")
                when(selectedItem){
                    0 -> {
                        Collections.sort(restaurentInfoList, costComparator)
                    }
                    1 -> {
                        Collections.sort(restaurentInfoList, costComparator)
                        restaurentInfoList.reverse()
                    }
                    2 -> {
                        Collections.sort(restaurentInfoList, ratingComparator)
                        restaurentInfoList.reverse()
                    }
                }
                recyclerAdapter.notifyDataSetChanged()
            }
            dialog.create().show()
        }
        return super.onOptionsItemSelected(item)
    }
}
