package com.smruti.foodrunner.Activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.smruti.foodrunner.R
import org.json.JSONException
import org.json.JSONObject
import com.smruti.foodrunner.util.ConnectionManager

class login : AppCompatActivity() {
    lateinit var etmobilenumber: EditText
    lateinit var etpassword: EditText
    lateinit var btnlogin: Button
    lateinit var forgotpassword: TextView
    lateinit var register: TextView
    lateinit var sharedpreference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedpreference =
            getSharedPreferences(getString(R.string.sharedpreferences), Context.MODE_PRIVATE)
        setContentView(R.layout.activity_login)
        val intent=Intent(this@login, MainActivity::class.java)
        etmobilenumber = findViewById(R.id.etloginnumber)
        etpassword = findViewById(R.id.etloginpassword)
        btnlogin = findViewById(R.id.btnLogin)
        forgotpassword = findViewById(R.id.txtForgotPassword)
        register = findViewById(R.id.txtRegisterYourself)
        register.setOnClickListener { startActivity(Intent(this@login,
            Registration::class.java)) }
        forgotpassword.setOnClickListener{ startActivity(Intent(this@login,
            ForgotPassword::class.java))}
        btnlogin.setOnClickListener {
             if(etmobilenumber.text.isBlank()||etpassword.text.isBlank())
             {
               if(etmobilenumber.text.isBlank())
               {
                   etmobilenumber.setError("missing mobile number")
               }
                 else
                   etpassword.setError("missing Password")

             }
            else{

        val queue = Volley.newRequestQueue(this@login)
        val url = "http://13.235.250.119/v2/login/fetch_result/"
        if (ConnectionManager().checkConnectivity(this@login)) {
            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", etmobilenumber.text.toString())
            jsonParams.put("password", etpassword.text.toString())

            val jsonRequest =
                object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")



                        if (success) {
                            val resobject = data.getJSONObject("data")

                            val user_id = resobject.getString("user_id")
                            val name = resobject.getString("name")
                            val email = resobject.getString("email")
                            val mobile_number = resobject.getString("mobile_number")
                            val address = resobject.getString("address")

                                sharedpreference.edit().putString("userid", user_id).apply()
                                sharedpreference.edit().putString("name", name).apply()
                                sharedpreference.edit().putString("email", email).apply()
                                sharedpreference.edit().putString("mobile", mobile_number).apply()
                                sharedpreference.edit().putString("address", address).apply()
                            finish()
                            startActivity(intent)
                        sharedpreference1()
                    } else {
                            Toast.makeText(
                                this@login,
                                "invalid user mobile number and password ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(this@login, "some exception  occurred ", Toast.LENGTH_SHORT)
                            .show()
                    }

                }, Response.ErrorListener {
                    Toast.makeText(this@login, " volley error occured ", Toast.LENGTH_SHORT).show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "ccf52a609cc3d2"
                        return headers
                    }
                }
            queue.add(jsonRequest)

        }
        else{

            val dialog = AlertDialog.Builder( this@login)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings"){text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                this@login?.finish()
            }

            dialog.setNegativeButton("Exit") {text, listener ->
                ActivityCompat.finishAffinity(this@login)
            }
            dialog.create()
            dialog.show()
        }
        }}}
    fun sharedpreference1()
    {
        sharedpreference.edit().putBoolean("login",true).apply()
    }

    override fun onResume() {

        if (!ConnectionManager().checkConnectivity(this))
        {

            val alterDialog=androidx.appcompat.app.AlertDialog.Builder(this)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be establish!")
            alterDialog.setPositiveButton("Open Settings"){text,listener->
                val settingsIntent= Intent(Settings.ACTION_SETTINGS)//open wifi settings
                startActivity(settingsIntent)
            }

            alterDialog.setNegativeButton("Exit"){ text,listener->
                ActivityCompat.finishAffinity(this)//closes all the instances of the app and the app closes completely
            }
            alterDialog.setCancelable(false)

            alterDialog.create()
            alterDialog.show()

        }

        super.onResume()
    }

    override fun onBackPressed() {

       finishAffinity()
    }
    }


