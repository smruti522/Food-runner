package com.smruti.foodrunner.Fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.smruti.foodrunner.R
import kotlinx.android.synthetic.main.fragment_my_profile.*

/**
 * A simple [Fragment] subclass.
 */
class MyProfile(): Fragment() {

    lateinit var txtname:TextView
    lateinit var txtemail:TextView
    lateinit var txtnumber:TextView

    lateinit var txtaddress:TextView
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_my_profile, container, false)
        txtname=view.findViewById(R.id.txtUserName)
        txtaddress=view.findViewById(R.id.txtAddress)
        txtnumber=view.findViewById(R.id.txtPhone)
        txtemail=view.findViewById(R.id.txtEmail)
        sharedPreferences= (activity as Context).getSharedPreferences(getString(R.string.sharedpreferences),Context.MODE_PRIVATE)
        txtname.text=sharedPreferences.getString("name","john")
        txtaddress.text=sharedPreferences.getString("address","america")
        txtnumber.text=sharedPreferences.getString("mobile","112233445")
        txtemail.text=sharedPreferences.getString("email","xyz@abc.com")

        return view
    }

}
