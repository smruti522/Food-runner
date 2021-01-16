package com.smruti.foodrunner.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.smruti.foodrunner.R
import com.smruti.foodrunner.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ForgotPassword : AppCompatActivity() {

    lateinit var etforgotnumber:EditText
    lateinit var etforgotemail:EditText
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var btnnext:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        etforgotemail=findViewById(R.id.etForgotEmail)
        etforgotnumber=findViewById(R.id.etForgotMobile)
        btnnext=findViewById(R.id.btnForgotNext)
        toolbar=findViewById(R.id.toolbar)
       setSupportActionBar(toolbar)
        supportActionBar?.title="Forgot Password"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnnext.setOnClickListener {
            if(etforgotemail.text.isBlank()||etforgotnumber.text.isBlank())
            {
                if(etforgotnumber.text.isBlank())
                {
                    etforgotnumber.setError("mobile number missing")
                }
                else
                {
                    etforgotemail.setError("email is missing")
                }
            }
            else{
            val queue = Volley.newRequestQueue(this@ForgotPassword)
            val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
            if (ConnectionManager().checkConnectivity(this@ForgotPassword)) {
                val jsonParams = JSONObject()
            jsonParams.put("mobile_number",etforgotnumber.text.toString())
            jsonParams.put("email",etforgotemail.text.toString())
                val jsonRequest =
                    object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                        try {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if (success)
                            {

                                val intent=Intent(this@ForgotPassword,ForgotPassword2::class.java)
                                    intent.putExtra("mobilenumber",etforgotnumber.text.toString())
                                    startActivity(intent)


                            }
                            else {
                                Toast.makeText(
                                    this@ForgotPassword,
                                    "this number doesnot exist",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: JSONException) {
                            Toast.makeText(this@ForgotPassword, "some exception  occurred ", Toast.LENGTH_SHORT)
                                .show()
                        }

                    }, Response.ErrorListener {
                        Toast.makeText(this@ForgotPassword, " volley error occured ", Toast.LENGTH_SHORT).show()
                    }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "ccf52a609cc3d2"
                            return headers
                        }}
                queue.add(jsonRequest)
            }
            else{

               Toast.makeText(this@ForgotPassword,"No internet",Toast.LENGTH_SHORT).show()
            }
                        }}
                        }



    override fun onResume() {

        if (!ConnectionManager().checkConnectivity(this@ForgotPassword)) {

            val alterDialog=androidx.appcompat.app.AlertDialog.Builder(this@ForgotPassword)
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
        finish()
        super.onBackPressed()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            android.R.id.home -> {
                finish()
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }


            }




