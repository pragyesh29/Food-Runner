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

class ResetPasswordActivity : AppCompatActivity() {

    lateinit var etOTP: EditText
    lateinit var etNewPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnSubmit: Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        etOTP = findViewById(R.id.etOTP)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPasswordAgain)
        btnSubmit = findViewById(R.id.btnSubmit)

        btnSubmit.setOnClickListener{
            if(etNewPassword.text.toString().compareTo(etConfirmPassword.text.toString(), false) == 0){
                val queue = Volley.newRequestQueue(this@ResetPasswordActivity)
                val url = "http://13.235.250.119/v2/reset_password/fetch_result"
                val jsonParams = JSONObject()
                jsonParams.put("mobile_number",intent.getStringExtra("mobile_number"))
                jsonParams.put("password",etNewPassword.text.toString())
                jsonParams.put("otp",etOTP.text.toString())
                if(ConnectionManager().checkConnectivity(this@ResetPasswordActivity)){
                    val jsonRequest = object: JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                        try{
                            val success = it.getJSONObject("data")
                            println(success.getBoolean("success"))
                            if(success.getBoolean("success")){
                                val dialog = AlertDialog.Builder(this@ResetPasswordActivity)
                                dialog.setTitle("Confirmation")
                                println(success.getString("successMessage"))
                                dialog.setMessage(success.getString("successMessage"))
                                dialog.setPositiveButton("OK"){text, listener ->
                                    val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                                    startActivity(intent)
                                    sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
                                    sharedPreferences.edit().clear().apply()
                                    finish()
                                }
                                dialog.create().show()
                            }else {
                                Toast.makeText(this@ResetPasswordActivity, "Invalid OTP", Toast.LENGTH_SHORT).show()
                            }
                        }catch (e : Exception){
                            Toast.makeText(this@ResetPasswordActivity, "Some Unexpected Error Occurred", Toast.LENGTH_SHORT).show()
                        }
                    },Response.ErrorListener {
                        Toast.makeText(this@ResetPasswordActivity, "Volley error occurred", Toast.LENGTH_SHORT).show()
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
                    val dialog = AlertDialog.Builder(this@ResetPasswordActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Not Found")
                    dialog.setPositiveButton("Open Settings"){ _, _ ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Exit"){ _, _ ->
                        ActivityCompat.finishAffinity(this@ResetPasswordActivity)
                    }
                    dialog.create().show()
                }
            }else{
                Toast.makeText(this@ResetPasswordActivity, "Passwords should be same", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
