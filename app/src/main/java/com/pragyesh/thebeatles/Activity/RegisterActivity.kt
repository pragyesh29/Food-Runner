package com.pragyesh.thebeatles.Activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import java.lang.Exception

class RegisterActivity : AppCompatActivity() {

    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etMobile: EditText
    lateinit var etAddress: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnRegister: Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        title = "Register Yourself"

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etMobile = findViewById(R.id.etMobileNumber)
        etAddress = findViewById(R.id.etAddress)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.ConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)

        val queue = Volley.newRequestQueue(this@RegisterActivity)
        val url = "http://13.235.250.119/v2/register/fetch_result"
        val jsonParams = JSONObject()

        btnRegister.setOnClickListener{
            jsonParams.put("name",etName.text.toString())
            jsonParams.put("mobile_number", etMobile.text.toString())
            jsonParams.put("password", etPassword.text.toString())
            jsonParams.put("address",etAddress.text.toString())
            jsonParams.put("email",etEmail.text.toString())

            if(ConnectionManager().checkConnectivity(this@RegisterActivity)){
                val jsonRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                    try{
                        val jsonObject = it.getJSONObject("data")
                        if(jsonObject.getBoolean("success")){
                            sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
                            sharedPreferences.edit().putString("user_id",jsonObject.getJSONObject("data").getString("user_id")).apply()
                            sharedPreferences.edit().putString("name",jsonObject.getJSONObject("data").getString("name")).apply()
                            sharedPreferences.edit().putString("email",jsonObject.getJSONObject("data").getString("email")).apply()
                            sharedPreferences.edit().putString("mobile_number",jsonObject.getJSONObject("data").getString("mobile_number")).apply()
                            sharedPreferences.edit().putString("address",jsonObject.getJSONObject("data").getString("address")).apply()
                            sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()
                            startActivity(Intent(this@RegisterActivity, DashboardActivity::class.java))
                            finish()
                        }else {
                            Toast.makeText(this@RegisterActivity, "Invalid Credentials, Retry", Toast.LENGTH_SHORT).show()
                        }
                    }catch (e :Exception){
                        Toast.makeText(this@RegisterActivity, "Some Unexpected Error Occurred", Toast.LENGTH_SHORT).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(this@RegisterActivity, "Volley Error Occurred", Toast.LENGTH_SHORT).show()
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
                val dialog = AlertDialog.Builder(this@RegisterActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection Not Found")
                dialog.setPositiveButton("Open Settings"){ _, _ ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit"){ _, _ ->
                    ActivityCompat.finishAffinity(this@RegisterActivity)
                }
                dialog.create().show()
            }
        }
    }
}
