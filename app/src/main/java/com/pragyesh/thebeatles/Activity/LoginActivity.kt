package com.pragyesh.thebeatles.Activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.Volley
import com.pragyesh.thebeatles.R
import com.pragyesh.thebeatles.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    lateinit var etMobileNumber: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogin: Button
    lateinit var txtForgetPassword:TextView
    lateinit var txtRegisterYourself:TextView

    lateinit var sharedPrefrences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedPrefrences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        val isLoggedIn = sharedPrefrences.getBoolean("isLoggedIn", false)
        title = "LOGIN"
        if(isLoggedIn){
            val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgetPassword = findViewById(R.id.txtForgetPassword)
        txtRegisterYourself = findViewById(R.id.txtRegisterYourself)

        val forget = SpannableString("Forget Password?")
        forget.setSpan(UnderlineSpan(), 0, forget.length, 0)
        txtForgetPassword.text = forget

        val register = SpannableString("Register Yourself")
        register.setSpan(UnderlineSpan(), 0, register.length, 0)
        txtRegisterYourself.text = register

        btnLogin.setOnClickListener{
            val queue = Volley.newRequestQueue(this@LoginActivity)
            val url = " http://13.235.250.119/v2/login/fetch_result"
            val jsonParams = JSONObject()
            jsonParams.put("mobile_number",etMobileNumber.text.toString())
            jsonParams.put("password", etPassword.text.toString())

            if(ConnectionManager().checkConnectivity(this@LoginActivity)){
                val jsonRequest = object: JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener{
                    try{
                        val jsonObject = it.getJSONObject("data")
                        if(jsonObject.getBoolean("success")){
                            sharedPrefrences.edit().putString("user_id",jsonObject.getJSONObject("data").getString("user_id")).apply()
                            sharedPrefrences.edit().putString("name",jsonObject.getJSONObject("data").getString("name")).apply()
                            sharedPrefrences.edit().putString("email",jsonObject.getJSONObject("data").getString("email")).apply()
                            sharedPrefrences.edit().putString("mobile_number",jsonObject.getJSONObject("data").getString("mobile_number")).apply()
                            sharedPrefrences.edit().putString("address",jsonObject.getJSONObject("data").getString("address")).apply()
                            sharedPrefrences.edit().putBoolean("isLoggedIn",true).apply()
                            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                            finish()
                        }else Toast.makeText(this@LoginActivity, "Invalid Credentials, Retry", Toast.LENGTH_SHORT).show()
                    }catch (e :Exception){
                        Toast.makeText(this@LoginActivity, "Some Unexpected Error Occurred", Toast.LENGTH_SHORT).show()
                    }
                },Response.ErrorListener {
                    Toast.makeText(this@LoginActivity, "Volley Error Occurred", Toast.LENGTH_SHORT).show()
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
                val dialog = AlertDialog.Builder(this@LoginActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection Not Found")
                dialog.setPositiveButton("Open Settings"){ _, _ ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit"){ _, _ ->
                    ActivityCompat.finishAffinity(this@LoginActivity)
                }
                dialog.create()
                dialog.show()
            }
        }

        txtForgetPassword.setOnClickListener{
            startActivity(Intent(this@LoginActivity, ForgetActivity::class.java))
        }

        txtRegisterYourself.setOnClickListener{
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
    }

    override fun onPause() {
        super.onPause()
        if(sharedPrefrences.getBoolean("isLoggedIn",false)) finish()
    }
}