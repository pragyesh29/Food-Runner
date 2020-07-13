package com.pragyesh.thebeatles.Fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity

import com.pragyesh.thebeatles.R

class ProfileFragment : Fragment() {

    lateinit var txtName: TextView
    lateinit var txtMobileNumber: TextView
    lateinit var txtEmail: TextView
    lateinit var txtAddress: TextView
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        txtName = view.findViewById(R.id.txtName)
        txtMobileNumber = view.findViewById(R.id.txtMobileNumber)
        txtEmail = view.findViewById(R.id.txtEmail)
        txtAddress = view.findViewById(R.id.txtAddress)
        sharedPreferences = activity!!.getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE)
        txtName.text = sharedPreferences.getString("name","...")
        txtMobileNumber.text = "+91-" + sharedPreferences.getString("mobile_number","")
        txtEmail.text = sharedPreferences.getString("email","")
        txtAddress.text = sharedPreferences.getString("address","")
        return view
    }

}
