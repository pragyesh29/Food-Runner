package com.pragyesh.thebeatles.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.pragyesh.thebeatles.Fragment.*
import com.pragyesh.thebeatles.R
import kotlinx.android.synthetic.main.drawer_header.*

class DashboardActivity : AppCompatActivity() {

    lateinit var sharedPreferences : SharedPreferences
    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var frame: FrameLayout
    lateinit var toolbar: Toolbar
    lateinit var navigationView: NavigationView
    lateinit var headerName: TextView
    lateinit var headerNumber: TextView
    var previousMenuItem: MenuItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        frame = findViewById(R.id.frame)
        toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.navigationView)
        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        setUpToolbar()
        val actionBarDrawerToggle = ActionBarDrawerToggle(this@DashboardActivity, drawerLayout, R.string.open_drawer, R.string.close_drawer)
        val headerView = navigationView.getHeaderView(0)
        headerName = headerView.findViewById(R.id.navName)
        headerNumber = headerView.findViewById(R.id.navMobileNumber)

        headerName.text = sharedPreferences.getString("name","")
        headerNumber.text = "+91-" + (sharedPreferences.getString("mobile_number",""))


        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        openDashboard()

	    navigationView.setNavigationItemSelectedListener{

            if(previousMenuItem!=null) previousMenuItem?.isChecked = false
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

		    when(it.itemId){
                R.id.home -> {
                    openDashboard()
                    drawerLayout.closeDrawers()
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frame, ProfileFragment()).commit()
                    drawerLayout.closeDrawers()
                }
                R.id.favorites -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frame, FavoritesFragment()).commit()
                    drawerLayout.closeDrawers()
                }
                R.id.orderHistory -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frame, OrderHistoryFragment()).commit()
                    drawerLayout.closeDrawers()
                }
                R.id.faq -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frame, FaqFragment()).commit()
                    drawerLayout.closeDrawers()
                }
                R.id.logout -> {
                    val dialog = AlertDialog.Builder(this@DashboardActivity)
                    dialog.setTitle("Confirmation")
                    dialog.setMessage("Are you sure you want to exit?")
                    dialog.setPositiveButton("YES"){ _, _ ->
                        val intent = Intent(this@DashboardActivity, LoginActivity::class.java)
                        sharedPreferences.edit().clear().apply()
                        startActivity(intent)
                        finish()
                    }
                    dialog.setNegativeButton("NO"){ _, _ ->
                        drawerLayout.closeDrawers()
                        openDashboard()
                    }
                    dialog.create().show()
                }
		    }
		    return@setNavigationItemSelectedListener true
	    }
    }
    fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title = "All Restaurants"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) drawerLayout.openDrawer(GravityCompat.START)
        return super.onOptionsItemSelected(item)
    }

    fun openDashboard(){
        val fragment = DashboardFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.commit()
        supportActionBar?.title = "All Restaurants"
        navigationView.setCheckedItem(R.id.home)
    }

    override fun onBackPressed() {
        when(supportFragmentManager.findFragmentById(R.id.frame)){
            !is DashboardFragment -> openDashboard()
            else -> super.onBackPressed()
        }
    }
}
