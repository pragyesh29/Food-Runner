package com.pragyesh.thebeatles.Activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.pragyesh.thebeatles.R
import com.pragyesh.thebeatles.util.ConnectionManager
import org.json.JSONObject

class ForgetActivity : AppCompatActivity() {

    lateinit var btnNext: Button
    lateinit var etMobileNumber: EditText
    lateinit var etEmail: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etEmail = findViewById(R.id.etEmail)
        btnNext = findViewById(R.id.btnNext)

        btnNext.setOnClickListener {
            val queue = Volley.newRequestQueue(this@ForgetActivity)
            val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
            val jsonParams = JSONObject()
            jsonParams.put("mobile_number",etMobileNumber.text.toString())
            jsonParams.put("email",etEmail.text.toString())
            if(ConnectionManager().checkConnectivity(this@ForgetActivity)){
                val jsonRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                    try{
                        if(it.getJSONObject("data").getBoolean("success") && it.getJSONObject("data").getBoolean("first_try")){
                            val dialog = AlertDialog.Builder(this@ForgetActivity)
                            dialog.setTitle("Information")
                            dialog.setMessage("Please check your email for the OTP")
                            dialog.setPositiveButton("OK"){ _, _ ->
                                val intent = Intent(this@ForgetActivity, ResetPasswordActivity::class.java)
                                intent.putExtra("mobile_number",etMobileNumber.text.toString())
                                startActivity(intent)
                                finish()
                            }
                            dialog.create()
                            dialog.show()
                        }else if(it.getJSONObject("data").getBoolean("success") && !it.getJSONObject("data").getBoolean("first_try")){
                            val dialog = AlertDialog.Builder(this@ForgetActivity)
                            dialog.setTitle("Information")
                            dialog.setMessage("Please refer to the previous email for the OTP")
                            dialog.setPositiveButton("OK"){ _, _ ->
                                val intent = Intent(this@ForgetActivity,ResetPasswordActivity::class.java)
                                intent.putExtra("mobile_number",etMobileNumber.text.toString())
                                startActivity(intent)
                                finish()
                            }
                            dialog.create()
                            dialog.show()
                        }else{
                            Toast.makeText(this@ForgetActivity, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                        }
                    }catch (e : Exception){
                        println(e)
                        Toast.makeText(this@ForgetActivity, "Some unexpected error occurred", Toast.LENGTH_SHORT).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(this@ForgetActivity, "Volley error occurred", Toast.LENGTH_SHORT).show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "c5b6606c9aa5af"
                        return headers
                    }
                }
                queue.add(jsonRequest)
            }else{
                val dialog = AlertDialog.Builder(this@ForgetActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection Not Found")
                dialog.setPositiveButton("Open Settings"){ _, _ ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit"){ _, _ ->
                    ActivityCompat.finishAffinity(this@ForgetActivity)
                }
                dialog.create()
                dialog.show()
            }
        }
    }
}
