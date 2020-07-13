package com.pragyesh.thebeatles.Fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.pragyesh.thebeatles.Adapter.OrderHistoryRecyclerAdapter

import com.pragyesh.thebeatles.R
import com.pragyesh.thebeatles.model.OrderHistory
import com.pragyesh.thebeatles.util.ConnectionManager

class OrderHistoryFragment : Fragment() {

    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var recyclerOrderHistory: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: OrderHistoryRecyclerAdapter
    val itemList = ArrayList<OrderHistory>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        recyclerOrderHistory = view.findViewById(R.id.recyclerOrderHistory)
        layoutManager = LinearLayoutManager(context)

        val userId = activity!!.getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE).getString("user_id","false")
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"
        if(ConnectionManager().checkConnectivity(context as Context)){
            val jsonRequest = object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                try{
                    progressLayout.visibility = View.GONE
                    val success = it.getJSONObject("data")
                    if(success.getBoolean("success")){
                        val arrData = success.getJSONArray("data")
                        val gson = Gson()
                        for (i in 0 until arrData.length()){
                            val eachItem = gson.fromJson(arrData[i].toString(),OrderHistory::class.java)
                            itemList.add(eachItem)
                        }
                        recyclerAdapter = OrderHistoryRecyclerAdapter(activity as Context, itemList)
                        recyclerOrderHistory.adapter = recyclerAdapter
                        recyclerOrderHistory.layoutManager = layoutManager
                    }else {
                        Toast.makeText(activity as Context, "Some unexpected error occurred", Toast.LENGTH_SHORT).show()
                    }
                }catch (e :Exception){
                    Toast.makeText(activity as Context, "Some error occurred", Toast.LENGTH_SHORT).show()
                }
            },Response.ErrorListener {
                if(activity != null) Toast.makeText(activity as Context, "Volley error occurred!!!", Toast.LENGTH_SHORT).show()
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
            val dialog = AlertDialog.Builder(context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection not found")
            dialog.setPositiveButton("Open Settings"){ _, _ ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit"){_,_ ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create().show()
        }
        return view
    }

}
