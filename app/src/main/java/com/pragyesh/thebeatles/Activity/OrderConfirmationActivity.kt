package com.pragyesh.thebeatles.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.pragyesh.thebeatles.R

class OrderConfirmationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmation)
        val btnOk:Button = findViewById(R.id.btnOk)
        btnOk.setOnClickListener{
            val intent = Intent(this@OrderConfirmationActivity, DashboardActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }
}
