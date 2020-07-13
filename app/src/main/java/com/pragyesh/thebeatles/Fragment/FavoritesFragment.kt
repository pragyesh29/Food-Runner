package com.pragyesh.thebeatles.Fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.pragyesh.thebeatles.Adapter.FavoritesRecyclerAdapter
import com.pragyesh.thebeatles.Database.RestaurantDatabase
import com.pragyesh.thebeatles.Database.RestaurantEntity

import com.pragyesh.thebeatles.R
import com.pragyesh.thebeatles.model.Restaurant

class FavoritesFragment : Fragment() {

    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var recyclerAdapter: FavoritesRecyclerAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerFavorite: RecyclerView
    var dbRestaurantList = listOf<RestaurantEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_favorites, container, false)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout = view.findViewById(R.id.progressLayout)
        recyclerFavorite = view.findViewById(R.id.recyclerFavorite)
        layoutManager = LinearLayoutManager(activity)

        dbRestaurantList = RetrieveFavorites(activity as Context).execute().get()

        if(activity != null){
            progressLayout.visibility = View.GONE
            recyclerAdapter = FavoritesRecyclerAdapter(activity as Context, dbRestaurantList)
            recyclerFavorite.adapter = recyclerAdapter
            recyclerFavorite.layoutManager = layoutManager
        }
        return view
    }

    class RetrieveFavorites(val context: Context):AsyncTask<Void,Void,List<RestaurantEntity>>(){
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()
            return db.restaurantDao().getAllRestaurant()
        }

    }
}
